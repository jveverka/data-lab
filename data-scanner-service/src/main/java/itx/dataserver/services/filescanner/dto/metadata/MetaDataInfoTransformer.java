package itx.dataserver.services.filescanner.dto.metadata;

import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
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
        Map<String, Object> source = new HashMap<>();
        source.put("fileInfoId", data.getId().getId());
        source.put("imageType", data.getImageType());
        source.put("imageWidth", data.getImageWidth());
        source.put("imageHeight", data.getImageHeight());
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("vendor", data.getDeviceInfo().getVendor());
        deviceInfo.put("model", data.getDeviceInfo().getModel());
        source.put("deviceInfo", deviceInfo);
        source.put("timeStamp", data.getTimeStamp());

        Map<String, Object> gps = new HashMap<>();
        Map<String, Object> coordinates = new HashMap<>();
        if (data.getGps() != null) {
            if (data.getGps().getCoordinates() != null) {
                coordinates.put("lon", data.getGps().getCoordinates().getLon());
                coordinates.put("lat", data.getGps().getCoordinates().getLat());
            }
            gps.put("coordinates", coordinates);
            gps.put("altitude", data.getGps().getAltitude());
            gps.put("timeStamp", data.getGps().getTimeStamp());
            gps.put("processingMethod", data.getGps().getProcessingMethod());
        }

        source.put("gps", gps);
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
                DataUtils.addMappingField(builder, "imageType", "keyword");
                DataUtils.addMappingField(builder, "imageWidth", "long");
                DataUtils.addMappingField(builder, "imageHeight", "long");
                DataUtils.addMappingField(builder, "timeStamp", "date");

                builder.startObject("deviceInfo");
                {
                    DataUtils.addMappingField(builder, "vendor", "keyword");
                    DataUtils.addMappingField(builder, "model", "keyword");
                }
                builder.endObject();

                builder.startObject("gps");
                {
                    DataUtils.addMappingField(builder, "coordinates", "geo_point");
                    DataUtils.addMappingField(builder, "altitude", "long");
                    DataUtils.addMappingField(builder, "timeStamp", "date");
                    DataUtils.addMappingField(builder, "processingMethod", "keyword");
                }
                builder.endObject();
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
    @SuppressWarnings("unchecked")
    public MetaDataInfo getInstance(DocumentId id, Map<String, Object> source) {
        FileInfoId fileInfoId = new FileInfoId(id.getId());
        String imageType = (String)source.get("imageType");
        long imageWidth = (Long)source.get("imageWidth");
        long imageHeight = (Long)source.get("imageHeight");
        Map<String, Object> deviceInfoSource = (Map<String, Object>)source.get("deviceInfo");
        String vendor = (String)deviceInfoSource.get("vendor");
        String model = (String)deviceInfoSource.get("model");
        DeviceInfo deviceInfo = new DeviceInfo(vendor, model);
        String timeStamp = (String)source.get("timeStamp");

        Map<String, Object> gpsInfoSource = (Map<String, Object>)source.get("gps");
        Map<String, Object> coordinatedSource = (Map<String, Object>)gpsInfoSource.get("coordinates");

        Coordinates coordinates = new Coordinates((Float)coordinatedSource.get("lon"), (Float)coordinatedSource.get("lat"));
        GPS gps = new GPS(coordinates, (Integer)gpsInfoSource.get("altitude"), (String)gpsInfoSource.get("timeStamp"), (String)gpsInfoSource.get("processingMethod"));

        return new MetaDataInfo(fileInfoId, imageType, imageWidth, imageHeight, deviceInfo, timeStamp, gps);

    }

}
