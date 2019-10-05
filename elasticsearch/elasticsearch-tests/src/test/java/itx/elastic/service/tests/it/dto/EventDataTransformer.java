package itx.elastic.service.tests.it.dto;

import itx.elastic.service.DataTransformer;
import itx.elastic.service.impl.ESUtils;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;

import static itx.elastic.service.impl.ESUtils.DATE_FORMAT;

public class EventDataTransformer implements DataTransformer<EventData> {

    @Override
    public XContentBuilder getSource(EventData eventData) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("name", eventData.getName());
            builder.field("description", eventData.getDescription());
            builder.field("startDate", ESUtils.toString(eventData.getStartDate()));
            builder.field("duration", eventData.getDuration());
            builder.startObject("location");
            {
                builder.field("lon", eventData.getLocation().getLongitude());
                builder.field("lat", eventData.getLocation().getLatitude());
            }
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    @Override
    public XContentBuilder getIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("name");
                {
                    builder.field("type", "text");
                }
                builder.endObject();
                builder.startObject("description");
                {
                    builder.field("type", "text");
                }
                builder.endObject();
                builder.startObject("location");
                {
                    builder.field("type", "geo_point");
                }
                builder.endObject();
                builder.startObject("startDate");
                {
                    builder.field("type", "date");
                    builder.field("format", DATE_FORMAT);
                }
                builder.endObject();
                builder.startObject("duration");
                {
                    builder.field("type", "long");
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
        return "event-data";
    }

    @Override
    public DocumentId getDocumentId(EventData data) {
        return new DocumentId(data.getId());
    }

    @Override
    public EventData getInstance(DocumentId id, Map<String, Object> source) {
        Map<String, Object> geoLocationMap = (Map<String, Object>)source.get("location");
        Location geoLocation = new Location(Float.parseFloat(geoLocationMap.get("lon").toString()), Float.parseFloat(geoLocationMap.get("lat").toString()));
        ZonedDateTime zonedDateTime = ESUtils.fromString(source.get("startDate").toString());
        long duration = Long.parseLong(source.get("duration").toString());
        return new EventData(id.getId(), source.get("name").toString(),
                source.get("description").toString(), zonedDateTime, duration, geoLocation);
    }

}
