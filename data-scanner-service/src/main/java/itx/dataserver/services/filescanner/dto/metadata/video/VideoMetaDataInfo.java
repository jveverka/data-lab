package itx.dataserver.services.filescanner.dto.metadata.video;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;

public class VideoMetaDataInfo {

    private final FileInfoId id;
    private final String videoType;
    private final float duration;
    private final long width;
    private final long height;
    private final float frameRate;
    private final String timeStamp;
    private final String mediaType;

    public VideoMetaDataInfo(FileInfoId id, String videoType, float duration, long width, long height, float frameRate, String timeStamp, String mediaType) {
        this.id = id;
        this.videoType = videoType;
        this.duration = duration;
        this.width = width;
        this.height = height;
        this.frameRate = frameRate;
        this.timeStamp = timeStamp;
        this.mediaType = mediaType;
    }

    public FileInfoId getId() {
        return id;
    }

    public String getVideoType() {
        return videoType;
    }

    public float getDuration() {
        return duration;
    }

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getMediaType() {
        return mediaType;
    }
}
