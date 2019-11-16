package itx.dataserver.services.filescanner.dto.metadata.video;

import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.elastic.service.DataMappingException;
import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
import itx.elastic.service.impl.ESUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VideoMetaDataInfoTransformer implements DataTransformer<VideoMetaDataInfo> {

    @Override
    public Map<String, Object> getSource(VideoMetaDataInfo data) {
        Map<String, Object> source = new HashMap<>();
        source.put("fileInfoId", data.getId().getId());
        source.put("videoType", data.getVideoType());
        source.put("duration", data.getDuration());
        source.put("width", data.getWidth());
        source.put("height", data.getHeight());
        source.put("frameRate", data.getFrameRate());
        source.put("timeStamp", data.getTimeStamp());
        return source;
    }

    @Override
    public XContentBuilder getIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                DataUtils.addMappingField(builder, "fileInfoId", "keyword");
                DataUtils.addMappingField(builder, "videoType", "keyword");
                DataUtils.addMappingField(builder, "duration", "float");
                DataUtils.addMappingField(builder, "width", "long");
                DataUtils.addMappingField(builder, "height", "long");
                DataUtils.addMappingField(builder, "frameRate", "float");
                DataUtils.addDateMappingField(builder, "timeStamp", ESUtils.DATE_FORMAT);
            }
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    @Override
    public String getIndexName() {
        return "video-meta-data-info";
    }

    @Override
    public DocumentId getDocumentId(VideoMetaDataInfo data) {
        return new DocumentId(data.getId().getId());
    }

    @Override
    public VideoMetaDataInfo getInstance(DocumentId id, Map<String, Object> source) throws DataMappingException {
        FileInfoId fileInfoId = new FileInfoId(id.getId());
        String videoType = (String)source.get("videoType");
        float duration = (Float)source.get("duration");
        long width = (Long)source.get("width");
        long height = (Long)source.get("height");
        float frameRate = (Float)source.get("frameRate");
        String timeStamp = (String)source.get("timeStamp");
        return new VideoMetaDataInfo(fileInfoId, videoType, duration, width, height, frameRate, timeStamp);
    }

}
