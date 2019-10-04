package itx.elastic.service.tests.it.dto;

import java.time.ZonedDateTime;
import java.util.Objects;

public class EventData {

    private final String id;
    private final String name;
    private final String description;
    private final ZonedDateTime startDate;
    private final long duration;
    private final Location location;

    public EventData(String id, String name, String description, ZonedDateTime startDate, long duration, Location location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.duration = duration;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public long getDuration() {
        return duration;
    }

    public Location getLocation() {
        return location;
    }

    public static EventData from(String id, String name, String description, ZonedDateTime startDate, long duration, Location location) {
        return new EventData(id, name, description, startDate, duration, location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventData eventData = (EventData) o;
        return duration == eventData.duration &&
                Objects.equals(id, eventData.id) &&
                Objects.equals(name, eventData.name) &&
                Objects.equals(description, eventData.description) &&
                Objects.equals(startDate, eventData.startDate) &&
                Objects.equals(location, eventData.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, startDate, duration, location);
    }

}
