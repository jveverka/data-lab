package itx.dataserver.services.filescanner.dto.metadata.video;

import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
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
    public VideoMetaDataInfo getInstance(DocumentId id, Map<String, Object> source) {
        FileInfoId fileInfoId = new FileInfoId(id.getId());
        String videoType = (String)source.get("videoType");
        return new VideoMetaDataInfo(fileInfoId, videoType);
    }

}
