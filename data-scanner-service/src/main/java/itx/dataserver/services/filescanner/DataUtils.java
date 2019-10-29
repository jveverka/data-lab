package itx.dataserver.services.filescanner;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfo;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.fileinfo.FileSystemInfo;
import itx.dataserver.services.filescanner.dto.fileinfo.FileType;
import itx.dataserver.services.filescanner.dto.metadata.Coordinates;
import itx.dataserver.services.filescanner.dto.metadata.DeviceInfo;
import itx.dataserver.services.filescanner.dto.metadata.GPS;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfo;
import itx.fs.service.dto.DirItem;
import itx.image.service.model.DirectoryInfo;
import itx.image.service.model.MetaData;
import itx.image.service.model.TagInfo;
import itx.image.service.model.values.Fraction;
import itx.image.service.model.values.Fractions;
import itx.image.service.model.values.StringValue;
import itx.image.service.model.values.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public final class DataUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DataUtils.class);

    private DataUtils() {
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

    public static Optional<MetaDataInfo> createMetaDataInfo(FileInfoId id, MetaData metaData) {

        String imageType = "NA";
        long imageWidth = 0;
        long imageHeight = 0;
        String vendor = "NA";
        String model = "NA";
        String timeStamp = "";
        GPS gps = null;

        if (metaData.directoryNames().contains("jpeg")) {
            imageType = "jpeg";
        } else {
            LOG.warn("Image type can't be determined !");
            return Optional.empty();
        }
        Optional<DirectoryInfo> exifIfd0Info = metaData.directoryByName("exif-ifd0");
        if (exifIfd0Info.isPresent()) {
            Optional<TagInfo> makeTag = exifIfd0Info.get().tagInfoByName("make");
            if (makeTag.isPresent()) {
                vendor = (String)makeTag.get().getValue().getValue();
            } else {
                LOG.warn("Device vendor can't be determined !");
                return Optional.empty();
            }
            Optional<TagInfo> modelTag = exifIfd0Info.get().tagInfoByName("model");
            if (modelTag.isPresent()) {
                model = (String)modelTag.get().getValue().getValue();
            } else {
                LOG.warn("Device model can't be determined !");
                return Optional.empty();
            }
        } else {
            LOG.warn("Image exif-ifd0 data not found !");
            return Optional.empty();
        }

        Optional<DirectoryInfo> exifSubifdInfo = metaData.directoryByName("exif-subifd");
        if (exifSubifdInfo.isPresent()) {
            Optional<TagInfo> imageWidthTag = exifSubifdInfo.get().tagInfoByName("exif-image-width");
            if (imageWidthTag.isPresent()) {
                if (Type.INTEGER.equals(imageWidthTag.get().getValue().getType())) {
                    imageWidth = (Integer) imageWidthTag.get().getValue().getValue();
                } else if (Type.LONG.equals(imageWidthTag.get().getValue().getType())) {
                    imageWidth = (Long)imageWidthTag.get().getValue().getValue();
                } else {
                    LOG.warn("Image Width type can't be determined !");
                    return Optional.empty();
                }
            } else {
                LOG.warn("Image Width can't be determined !");
                return Optional.empty();
            }
            Optional<TagInfo> imageHeightTag = exifSubifdInfo.get().tagInfoByName("exif-image-height");
            if (imageHeightTag.isPresent()) {
                if (Type.INTEGER.equals(imageHeightTag.get().getValue().getType())) {
                    imageHeight = (Integer)imageHeightTag.get().getValue().getValue();
                } else if (Type.LONG.equals(imageHeightTag.get().getValue().getType())) {
                    imageHeight = (Long)imageHeightTag.get().getValue().getValue();
                } else {
                    LOG.warn("Image Height type can't be determined !");
                    return Optional.empty();
                }
            } else {
                LOG.warn("Image Height can't be determined !");
                return Optional.empty();
            }
            Optional<TagInfo> dateTimeTag = exifSubifdInfo.get().tagInfoByName("date/time-original");
            if (dateTimeTag.isPresent()) {
                timeStamp = (String)dateTimeTag.get().getValue().getValue();
            } else {
                LOG.warn("Image date/time-original can't be determined !");
                return Optional.empty();
            }
        } else {
            LOG.warn("Image exif-subifd data not found !");
            return Optional.empty();
        }

        Optional<GPS> gpsInfo = createGPS(metaData);
        if (gpsInfo.isPresent()) {
            gps = gpsInfo.get();
        } else {
            LOG.warn("GPS data not found !");
        }

        DeviceInfo deviceInfo = new DeviceInfo(vendor, model);
        MetaDataInfo metaDataInfo = new MetaDataInfo(id, imageType, imageWidth, imageHeight, deviceInfo, timeStamp, gps);
        return Optional.of(metaDataInfo);
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
            timeStamp = getTimeStamp(gpsDirectoryInfo);
            altitude = getAltitude(gpsDirectoryInfo);
            Optional<TagInfo> gpsProcessingMethodTag = gpsDirectoryInfo.tagInfoByName("gps-processing-method");
            if (gpsProcessingMethodTag.isPresent()) {
                processingMethod = gpsProcessingMethodTag.get().getDescription();
            }
        } else {
            LOG.warn("GPS data directory not found !");
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

    private static String getTimeStamp(DirectoryInfo gpsDirectoryInfo) {
        String result = "";
        Optional<TagInfo> gpsDateStampTag = gpsDirectoryInfo.tagInfoByName("gps-date-stamp");
        if (gpsDateStampTag.isPresent()) {
            result = gpsDateStampTag.get().getValue().getValue().toString();
        }
        Optional<TagInfo> gpsTimeStampTag = gpsDirectoryInfo.tagInfoByName("gps-time-stamp");
        if (gpsTimeStampTag.isPresent()) {
            result = result + " " + gpsTimeStampTag.get().getDescription();
        }
        return result;
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

}
