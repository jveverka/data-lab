package itx.dataserver.services.filescanner.dto.metadata.video;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;

public class VideoMetaDataInfo {

    private final FileInfoId id;
    private final String videoType;
    /*
    private final long duration;
    private final long width;
    private final long height;
    private final String timeStamp;
    */

    public VideoMetaDataInfo(FileInfoId id, String videoType) {
        this.id = id;
        this.videoType = videoType;
    }

    public FileInfoId getId() {
        return id;
    }

    public String getVideoType() {
        return videoType;
    }

}
