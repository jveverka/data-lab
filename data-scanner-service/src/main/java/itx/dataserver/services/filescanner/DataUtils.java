package itx.dataserver.services.filescanner;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfo;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.fileinfo.FileSystemInfo;
import itx.dataserver.services.filescanner.dto.fileinfo.FileType;
import itx.dataserver.services.filescanner.dto.metadata.Coordinates;
import itx.dataserver.services.filescanner.dto.metadata.DeviceInfo;
import itx.dataserver.services.filescanner.dto.metadata.GPS;
import itx.dataserver.services.filescanner.dto.metadata.image.ImageMetaDataInfo;
import itx.dataserver.services.filescanner.dto.metadata.video.VideoMetaDataInfo;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedData;
import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.impl.ESUtils;
import itx.fs.service.dto.DirItem;
import itx.image.service.ParsingUtils;
import itx.image.service.model.DirectoryInfo;
import itx.image.service.model.MetaData;
import itx.image.service.model.TagInfo;
import itx.image.service.model.values.Fraction;
import itx.image.service.model.values.Fractions;
import itx.image.service.model.values.StringValue;
import itx.image.service.model.values.Type;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public final class DataUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DataUtils.class);

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final SimpleDateFormat dateFormatIn01 = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    public static final SimpleDateFormat dateFormatOut = new SimpleDateFormat(DATE_TIME_FORMAT);
    public static final DateTimeFormatter formatterWithTimeZoneIn01 = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.n z");
    public static final DateTimeFormatter formatterWithTimeZoneIn02 = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");

    private DataUtils() {
    }

    public static void logESError(ElasticSearchService elasticSearchService, Class<?> type, FileInfoId id, MetaData metaData, Path path, String reason) throws IOException {
        logESError(elasticSearchService, type, id, metaData, null, path, reason);
    }

    public static void logESError(ElasticSearchService elasticSearchService, Class<?> type, FileInfoId id, MetaData metaData, Exception e, Path path, String reason) throws IOException {
        String jsonData = ParsingUtils.writeAsJsonString(metaData);
        String stackTrace = e != null ? DataUtils.getStackTraceAsString(e) : "";
        UnmappedData unmappedData =
                new UnmappedData(id, type.getTypeName(), jsonData, path.toString(), reason, stackTrace);
        elasticSearchService.saveDocument(UnmappedData.class, unmappedData);
    }

    public static void ligESErrorDirMapping(ElasticSearchService elasticSearchService, Path path, Exception e) throws IOException {
        String id = UUID.randomUUID().toString();
        String jsonData = path.toString();
        String stackTrace = DataUtils.getStackTraceAsString(e);
        UnmappedData unmappedData =
                new UnmappedData(id, DirItem.class.getTypeName(), jsonData, path.toString(), "DirItem_mapping_failed", stackTrace);
        elasticSearchService.saveDocument(UnmappedData.class, unmappedData);
    }

    public static void addMappingField(XContentBuilder builder, String fieldName, String type) throws IOException {
        builder.startObject(fieldName);
        builder.field("type", type);
        builder.endObject();
    }

    public static void addDateMappingField(XContentBuilder builder, String fieldName, String dateFormat) throws IOException {
        builder.startObject(fieldName);
        builder.field("type", "date");
        builder.field("format", dateFormat);
        builder.endObject();
    }

    public static String getStackTraceAsString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        sw.flush();
        return sw.toString();
    }

    public static FileInfoId createFileInfoId(DirItem dirItem) throws NoSuchAlgorithmException {
        String checkSumStr = dirItem.getCheckSum().isPresent() ? dirItem.getCheckSum().get().toString() : "";
        String id = dirItem.getPath().toString() + ":" + checkSumStr + ":" + dirItem.getAttributes().lastModifiedTime().toString();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(id.getBytes());
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return new FileInfoId(result.toString());
    }

    public static FileType createFileType(DirItem dirItem) {
        if (dirItem.getAttributes().isRegularFile()) {
            return FileType.REGULAR_FILE;
        } else if (dirItem.getAttributes().isDirectory()) {
            return FileType.DIRECTORY;
        } else if (dirItem.getAttributes().isSymbolicLink()) {
            return FileType.SYMBOLIC_LINK;
        } else {
            return FileType.OTHER;
        }
    }

    public static FileSystemInfo createFileSystemInfo(DirItem dirItem) {
        FileType type = createFileType(dirItem);
        return new FileSystemInfo(dirItem.getPath().toString(), dirItem.getCheckSum(),
                dirItem.getAttributes().creationTime(), dirItem.getAttributes().lastModifiedTime(),
                dirItem.getAttributes().lastAccessTime(), type, dirItem.getAttributes().size(), dirItem.getExtension());
    }

    public static FileInfo createFileInfo(DirItem dirItem) throws NoSuchAlgorithmException {
        FileInfoId id = createFileInfoId(dirItem);
        FileSystemInfo fileSystemInfo = createFileSystemInfo(dirItem);
        return new FileInfo(id, fileSystemInfo);
    }

    public static FileTime createFileTime(String timeStamp) {
        long timeStampLong = Long.parseLong(timeStamp);
        return FileTime.fromMillis(timeStampLong);
    }

    public static Optional<VideoMetaDataInfo> createVideoMetaDataInfo(FileInfoId id, MetaData metaData) {
        try {
            String videoType = "";
            float duration = 0;
            long width = 0;
            long height = 0;
            float frameRate = 0;
            String timeStamp = "";

            if (metaData.directoryNames().contains("mp4")) {
                videoType = "mp4";
            } else {
                LOG.warn("Video type can't be determined !");
                return Optional.empty();
            }

            Optional<String> creationTime = metaData.getValueByPath(String.class, "mp4-metadata", "creation-time");
            if (creationTime.isPresent()) {
                Optional<String> normalizedTimeZone = normalizeDateTimeWithTimeZone(formatterWithTimeZoneIn02, creationTime.get());
                if (normalizedTimeZone.isPresent()) {
                    timeStamp = normalizedTimeZone.get();
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }

            Optional<Integer> widthOptional = metaData.getValueByPath(Integer.class, "mp4-video", "width");
            if (widthOptional.isPresent()) {
                width = widthOptional.get();
            } else {
                return Optional.empty();
            }

            Optional<Integer> heightOptional = metaData.getValueByPath(Integer.class, "mp4-video", "height");
            if (heightOptional.isPresent()) {
                height = heightOptional.get();
            } else {
                return Optional.empty();
            }

            Optional<Float> durationOptional = metaData.getFloatValueByPath("mp4", "duration-in-seconds");
            if (durationOptional.isPresent()) {
                duration = durationOptional.get();
            } else {
                return Optional.empty();
            }

            Optional<Float> frameRateOptional = metaData.getValueByPath(Float.class, "mp4-video", "frame-rate");
            if (frameRateOptional.isPresent()) {
                frameRate = frameRateOptional.get();
            } else {
                return Optional.empty();
            }

            VideoMetaDataInfo videoMetaDataInfo = new VideoMetaDataInfo(id, videoType, duration, width, height, frameRate, timeStamp);
            return Optional.of(videoMetaDataInfo);
        } catch (Exception e) {
            LOG.error("Video mapping Exception: ", e);
            return Optional.empty();
        }
    }

    public static Optional<ImageMetaDataInfo> createImageMetaDataInfo(FileInfoId id, MetaData metaData) {
        try {
            String imageType = "";
            long imageWidth = 0;
            long imageHeight = 0;
            String vendor = null;
            String model = null;
            String timeStamp = null;
            GPS gps = null;

            if (metaData.directoryNames().contains("jpeg")) {
                imageType = "jpeg";
            } else {
                LOG.warn("Image type can't be determined !");
                return Optional.empty();
            }

            Optional<String> timeStampOptional = getTimeStamp(metaData);
            if (timeStampOptional.isPresent()) {
                timeStamp = timeStampOptional.get();
            } else {
                LOG.warn("TimeStamp data not found !");
            }

            Optional<DirectoryInfo> exifIfd0Info = metaData.directoryByName("exif-ifd0");
            if (exifIfd0Info.isPresent()) {
                Optional<TagInfo> makeTag = exifIfd0Info.get().tagInfoByName("make");
                if (makeTag.isPresent()) {
                    vendor = (String) makeTag.get().getValue().getValue();
                } else {
                    LOG.warn("Device vendor can't be determined !");
                }
                Optional<TagInfo> modelTag = exifIfd0Info.get().tagInfoByName("model");
                if (modelTag.isPresent()) {
                    model = (String) modelTag.get().getValue().getValue();
                } else {
                    LOG.warn("Device model can't be determined !");
                }
            } else {
                LOG.warn("Image exif-ifd0 data not found !");
            }

            Optional<Long> optionalImageWidth = getImageWidth(metaData);
            if (optionalImageWidth.isPresent()) {
                imageWidth = optionalImageWidth.get();
            } else {
                return Optional.empty();
            }

            Optional<Long> optionalImageHeight = getImageHeight(metaData);
            if (optionalImageHeight.isPresent()) {
                imageHeight = optionalImageHeight.get();
            } else {
                return Optional.empty();
            }

            Optional<GPS> gpsInfo = createGPS(metaData);
            if (gpsInfo.isPresent()) {
                gps = gpsInfo.get();
            } else {
                LOG.warn("GPS data not found !");
            }

            DeviceInfo deviceInfo = null;
            if (vendor != null || model != null) {
                deviceInfo = new DeviceInfo(vendor, model);
            }
            ImageMetaDataInfo imageMetaDataInfo = new ImageMetaDataInfo(id, imageType, imageWidth, imageHeight, deviceInfo, timeStamp, gps);
            return Optional.of(imageMetaDataInfo);
        } catch (Exception e) {
            LOG.error("Image mapping Exception: ", e);
            return Optional.empty();
        }
    }

    private static Optional<String> getTimeStamp(MetaData metaData) {
        Optional<DirectoryInfo> exifSubifdInfo = metaData.directoryByName("exif-subifd");
        if (exifSubifdInfo.isPresent()) {
            Optional<TagInfo> dateTimeTag = exifSubifdInfo.get().tagInfoByName("date/time-original");
            if (dateTimeTag.isPresent()) {
                String timeStampTagValue = (String) dateTimeTag.get().getValue().getValue();
                Optional<String> normalizedTimeStamp = normalizeDateTime(timeStampTagValue);
                if (normalizedTimeStamp.isPresent()) {
                    return Optional.of(normalizedTimeStamp.get());
                }
            }
        } else {
            LOG.warn("Image exif-subifd data not found !");
        }
        Optional<DirectoryInfo> exifIfd0Info = metaData.directoryByName("exif-ifd0");
        if (exifIfd0Info.isPresent()) {
            Optional<TagInfo> dateTimeTag = exifIfd0Info.get().tagInfoByName("date/time");
            if (dateTimeTag.isPresent()) {
                String timeStampTagValue = (String) dateTimeTag.get().getValue().getValue();
                Optional<String> normalizedTimeStamp = normalizeDateTime(timeStampTagValue);
                if (normalizedTimeStamp.isPresent()) {
                    return Optional.of(normalizedTimeStamp.get());
                }
            }
        }
        LOG.warn("Image date/time can't be determined !");
        return Optional.empty();
    }

    private static Optional<Long> getImageWidth(MetaData metaData) {
        return getImageWidthOrHeight(metaData, true);
    }

    private static Optional<Long> getImageHeight(MetaData metaData) {
        return getImageWidthOrHeight(metaData, false);
    }

    private static Optional<Long> getImageWidthOrHeight(MetaData metaData, boolean width) {
        String key = "image-width";
        if (!width) {
            key = "image-height";
        }
        Optional<DirectoryInfo> exifSubifdInfo = metaData.directoryByName("exif-subifd");
        if (exifSubifdInfo.isPresent()) {
            Optional<TagInfo> imageWidthTag = exifSubifdInfo.get().tagInfoByName("exif-" + key);
            if (imageWidthTag.isPresent()) {
                if (Type.INTEGER.equals(imageWidthTag.get().getValue().getType())) {
                    return Optional.of(((Integer) imageWidthTag.get().getValue().getValue()).longValue());
                } else if (Type.LONG.equals(imageWidthTag.get().getValue().getType())) {
                    return Optional.of((Long) imageWidthTag.get().getValue().getValue());
                }
            }
        }
        Optional<DirectoryInfo> exifIfd0Info = metaData.directoryByName("exif-ifd0");
        if (exifIfd0Info.isPresent()) {
            Optional<TagInfo> imageWidthTag = exifIfd0Info.get().tagInfoByName(key);
            if (imageWidthTag.isPresent()) {
                if (Type.LONG.equals(imageWidthTag.get().getValue().getType())) {
                    return Optional.of((Long) imageWidthTag.get().getValue().getValue());
                }
            }
        }
        Optional<DirectoryInfo> jpegInfo = metaData.directoryByName("jpeg");
        if (jpegInfo.isPresent()) {
            Optional<TagInfo> imageDataTag = jpegInfo.get().tagInfoByName(key);
            if (imageDataTag.isPresent()) {
                if (Type.INTEGER.equals(imageDataTag.get().getValue().getType())) {
                    return Optional.of(Long.valueOf((Integer) imageDataTag.get().getValue().getValue()));
                }
            }
        }
        LOG.warn("Image Width type can't be determined !");
        return Optional.empty();
    }

    private static Optional<GPS> createGPS(MetaData metaData) {

        float lon = 0F;
        float lat = 0F;
        int altitude = 0;
        String timeStamp = "";
        String processingMethod = "";

        Optional<DirectoryInfo> gpsInfo = metaData.directoryByName("gps");
        if (gpsInfo.isPresent()) {
            DirectoryInfo gpsDirectoryInfo = gpsInfo.get();
            lon = getLongitude(gpsDirectoryInfo);
            lat = getLatitude(gpsDirectoryInfo);
            Optional<String> timeStampOptional = getTimeStamp(gpsDirectoryInfo);
            if (timeStampOptional.isPresent()) {
                timeStamp = timeStampOptional.get();
            } else {
                LOG.warn("GPS timestamp cannot be parsed !");
                return Optional.empty();
            }
            altitude = getAltitude(gpsDirectoryInfo);
            Optional<TagInfo> gpsProcessingMethodTag = gpsDirectoryInfo.tagInfoByName("gps-processing-method");
            if (gpsProcessingMethodTag.isPresent()) {
                processingMethod = gpsProcessingMethodTag.get().getDescription();
            }
        } else {
            return Optional.empty();
        }

        Coordinates coordinates = new Coordinates(lon, lat);
        return Optional.of(new GPS(coordinates, altitude, timeStamp, processingMethod));
    }

    private static float getLongitude(DirectoryInfo gpsDirectoryInfo) {
        float longitude = 0F;
        Optional<TagInfo> gpsLongitudeTag = gpsDirectoryInfo.tagInfoByName("gps-longitude");
        if (gpsLongitudeTag.isPresent()) {
            Fractions fractions = (Fractions)gpsLongitudeTag.get().getValue();
            Fraction.Value[] values = fractions.getValue();
            longitude = values[0].getFloatValue();
            longitude = longitude + values[1].getFloatValue()/60;
            longitude = longitude + values[2].getFloatValue()/3600;
        }
        Optional<TagInfo> gpsLongitudeRefTag = gpsDirectoryInfo.tagInfoByName("gps-longitude-ref");
        if (gpsLongitudeRefTag.isPresent()) {
            StringValue stringValue = (StringValue)gpsLongitudeRefTag.get().getValue();
            if ("W".equals(stringValue.getValue())) {
                longitude = longitude * -1;
            }
        }
        return longitude;
    }

    private static float getLatitude(DirectoryInfo gpsDirectoryInfo) {
        float latitude = 0F;
        Optional<TagInfo> gpsLatitudeTag = gpsDirectoryInfo.tagInfoByName("gps-latitude");
        if (gpsLatitudeTag.isPresent()) {
            Fractions fractions = (Fractions)gpsLatitudeTag.get().getValue();
            Fraction.Value[] values = fractions.getValue();
            latitude = values[0].getFloatValue();
            latitude = latitude + values[1].getFloatValue()/60;
            latitude = latitude + values[2].getFloatValue()/3600;
        }
        Optional<TagInfo> gpsLatitudeRefTag = gpsDirectoryInfo.tagInfoByName("gps-latitude-ref");
        if (gpsLatitudeRefTag.isPresent()) {
            StringValue stringValue = (StringValue)gpsLatitudeRefTag.get().getValue();
            if ("S".equals(stringValue.getValue())) {
                latitude = latitude * -1;
            }
        }
        return latitude;
    }

    private static Optional<String> getTimeStamp(DirectoryInfo gpsDirectoryInfo) {
        String result = "";
        Optional<TagInfo> gpsDateStampTag = gpsDirectoryInfo.tagInfoByName("gps-date-stamp");
        if (gpsDateStampTag.isPresent()) {
            result = gpsDateStampTag.get().getValue().getValue().toString();
        }
        Optional<TagInfo> gpsTimeStampTag = gpsDirectoryInfo.tagInfoByName("gps-time-stamp");
        if (gpsTimeStampTag.isPresent()) {
            result = result + " " + gpsTimeStampTag.get().getDescription();
        }
        return normalizeDateTimeWithTimeZone(result);
    }

    private static int getAltitude(DirectoryInfo gpsDirectoryInfo) {
        int altitude = 0;
        Optional<TagInfo> gpsAltitudeTag = gpsDirectoryInfo.tagInfoByName("gps-altitude");
        if (gpsAltitudeTag.isPresent()) {
            Fraction alt = (Fraction)gpsAltitudeTag.get().getValue();
            altitude = (int)alt.getValue().getFloatValue();
        }
        Optional<TagInfo> gpsAltitudeRefTag = gpsDirectoryInfo.tagInfoByName("gps-altitude-ref");
        if (gpsAltitudeRefTag.isPresent()) {

        }
        return altitude;
    }

    /**
     * Normalizes datetime String.
     * @param dateTime input datetime string in format "yyyy:MM:dd HH:mm:ss".
     * @return dateTime String in "yyyy-MM-dd HH:mm:ss" format.
     */
    //TODO: remove synchronized, use thread safe date formatter instead.
    public synchronized static Optional<String> normalizeDateTime(String dateTime) {
        try {
            if (dateTime == null) return Optional.empty();
            if (dateTime.isBlank()) return Optional.empty();
            Date parsedDate = dateFormatIn01.parse(dateTime);
            return Optional.of(dateFormatOut.format(parsedDate));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    public static Optional<String> normalizeDateTimeWithTimeZone(DateTimeFormatter formatterWithTimeZoneIn, String dateTimeWithTimeZone) {
        if (dateTimeWithTimeZone == null) return Optional.empty();
        if (dateTimeWithTimeZone.isBlank()) return Optional.empty();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeWithTimeZone, formatterWithTimeZoneIn);
        int year = zonedDateTime.getYear();
        if (year < 1900 || year > 2200) {
            return Optional.empty();
        }
        return Optional.of(ESUtils.toString(zonedDateTime));
    }

    /**
     * Normalizes datetime String.
     * @param dateTimeWithTimeZone input datetime string in format "yyyy:MM:dd HH:mm:ss Z".
     * @return dateTime string in "yyyyMMdd'T'HHmmss.SSSZ" format.
     */
    public static Optional<String> normalizeDateTimeWithTimeZone(String dateTimeWithTimeZone) {
        return normalizeDateTimeWithTimeZone(formatterWithTimeZoneIn01, dateTimeWithTimeZone);
    }

    public static MetaDataType getMetaDataType(MetaData metaData) {
        if (metaData.directoryNames().contains("jpeg")) {
            return MetaDataType.JPEG;
        } else if (metaData.directoryNames().contains("mp4")) {
            return MetaDataType.MP4;
        } else {
            return MetaDataType.NA;
        }
    }

}
