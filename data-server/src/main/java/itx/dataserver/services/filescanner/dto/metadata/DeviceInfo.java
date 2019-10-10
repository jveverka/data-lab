package itx.dataserver.services.filescanner.dto.metadata;

public class DeviceInfo {

    private final String vendor;
    private final String model;

    public DeviceInfo(String vendor, String model) {
        this.vendor = vendor;
        this.model = model;
    }

    public String getVendor() {
        return vendor;
    }

    public String getModel() {
        return model;
    }

}
