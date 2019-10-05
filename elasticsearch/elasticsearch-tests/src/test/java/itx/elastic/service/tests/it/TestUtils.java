package itx.elastic.service.tests.it;

import itx.elastic.service.impl.ESUtils;
import itx.elastic.service.tests.it.dto.EventData;
import itx.elastic.service.tests.it.dto.Location;

public final class TestUtils {

    private TestUtils() {
    }

    public static EventData createEventData(int ordinal) {
        Location location = new Location(40.000F + (ordinal/1000F), 50.000F + (ordinal/(1000F   )));
        return new EventData("id-" + ordinal, "name-" + ordinal, "description " + ordinal, ESUtils.getNow(), 3600, location);
    }

}
