package itx.dataserver.services.filescanner.dto.metadata;

import itx.dataserver.services.filescanner.dto.FileInfoId;
import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MetaDataInfoTransformer implements DataTransformer<MetaDataInfo> {

    @Override
    public Map<String, Object> getSource(MetaDataInfo data) {
        Map<String, Object> metaDataInfo = new HashMap<>();
        metaDataInfo.put("fileInfoId", data.getId().getId());
        metaDataInfo.put("imageType", data.getImageType());
        metaDataInfo.put("imageWidth", data.getImageWidth());
        metaDataInfo.put("imageHeight", data.getImageHeight());
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("vendor", data.getDeviceInfo().getVendor());
        deviceInfo.put("model", data.getDeviceInfo().getModel());
        metaDataInfo.put("deviceInfo", deviceInfo);
        metaDataInfo.put("timeStamp", data.getTimeStamp());
        return metaDataInfo;
    }

    @Override
    public XContentBuilder getIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                    builder.field("fileInfoId", "keyword");
                    builder.field("imageType", "keyword");
                    builder.field("imageWidth", "long");
                    builder.field("imageHeight", "long");
                    builder.startObject("deviceInfo");
                    {
                        builder.field("vendor", "keyword");
                        builder.field("model", "keyword");
                    }
                    builder.endObject();
                    builder.field("timeStamp", "date");
            }
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    @Override
    public String getIndexName() {
        return "meta-data-info";
    }

    @Override
    public DocumentId getDocumentId(MetaDataInfo data) {
        return new DocumentId(data.getId().getId());
    }

    @Override
    public MetaDataInfo getInstance(DocumentId id, Map<String, Object> source) {

        String imageType = "NA";
        long imageWidth = 0;
        long imageHeight = 0;
        String vendor = "NA";
        String model = "NA";
        String timeStamp = "";

        FileInfoId fileInfoId = new FileInfoId(id.getId());
        imageType = (String)source.get("imageType");
        imageWidth = (Long)source.get("imageWidth");
        imageHeight = (Long)source.get("imageHeight");
        Map<String, Object> deviceInfoSource = (Map<String, Object>)source.get("deviceInfo");
        vendor = (String)deviceInfoSource.get("vendor");
        model = (String)deviceInfoSource.get("model");
        DeviceInfo deviceInfo = new DeviceInfo(vendor, model);
        timeStamp = (String)source.get("timeStamp");
        return new MetaDataInfo(fileInfoId, imageType, imageWidth, imageHeight, deviceInfo, timeStamp);

    }

}
