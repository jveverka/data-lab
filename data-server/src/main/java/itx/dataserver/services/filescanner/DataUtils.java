package itx.dataserver.services.filescanner;

import itx.dataserver.services.filescanner.dto.FileInfo;
import itx.dataserver.services.filescanner.dto.FileInfoId;
import itx.dataserver.services.filescanner.dto.FileSystemInfo;
import itx.dataserver.services.filescanner.dto.FileType;
import itx.dataserver.services.filescanner.dto.metadata.Coordinates;
import itx.dataserver.services.filescanner.dto.metadata.DeviceInfo;
import itx.dataserver.services.filescanner.dto.metadata.GPS;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfo;
import itx.fs.service.dto.DirItem;
import itx.image.service.model.DirectoryInfo;
import itx.image.service.model.MetaData;
import itx.image.service.model.TagInfo;
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
                dirItem.getAttributes().lastAccessTime(), type, dirItem.getAttributes().size());
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
            return Optional.empty();
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

        } else {
            LOG.warn("GPS data directory not found !");
            return Optional.empty();
        }

        Coordinates coordinates = new Coordinates(lon, lat);
        return Optional.of(new GPS(coordinates, altitude, timeStamp, processingMethod));
    }

}
