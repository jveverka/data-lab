package itx.dataserver.services.filescanner.dto.metadata.image;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.DeviceInfo;
import itx.dataserver.services.filescanner.dto.metadata.GPS;

public class ImageMetaDataInfo {

    private final FileInfoId id;
    private final String imageType;
    //private final int dataPrecision;
    private final long imageWidth;
    private final long imageHeight;
    private final DeviceInfo deviceInfo;
    private final String timeStamp;
    private final String mediaType;
    /*
    private final String deviceOrientation;
    private final int resolutionX;
    private final int resoutionY;
    private final int resoutionUnit;
    private final float exposureTime;
    private final float fNumber;
    private final int isoNumber;
    private final float shutterSpeedValue;
    private final float apertureSpeedValue;
    private final boolean flash;
    private final float focalLenght35mm;
    private final String colorSpace;
    */
    private final GPS gps;

    public ImageMetaDataInfo(FileInfoId id, String imageType, long imageWidth, long imageHeight,
                             DeviceInfo deviceInfo, String timeStamp, GPS gps, String mediaType) {
        this.id = id;
        this.imageType = imageType;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.deviceInfo = deviceInfo;
        this.timeStamp = timeStamp;
        this.gps = gps;
        this.mediaType = mediaType;
    }


    public FileInfoId getId() {
        return id;
    }

    public String getImageType() {
        return imageType;
    }

    public long getImageWidth() {
        return imageWidth;
    }

    public long getImageHeight() {
        return imageHeight;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public GPS getGps() {
        return gps;
    }

    public String getMediaType() {
        return mediaType;
    }
}
