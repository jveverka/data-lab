package itx.elastic.service.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClientConfig {

    private final Collection<EndPoint> endpoints;

    public ClientConfig(Collection<EndPoint> endpoints) {
        this.endpoints = endpoints;
    }

    public Collection<EndPoint> getEndpoints() {
        return endpoints;
    }

    public static class Builder {

        private List<EndPoint> endpoints = new ArrayList<>();

        public Builder addEndPoint(String hostname, int port, String scheme) {
            endpoints.add(new EndPoint(hostname, port, scheme));
            return this;
        }

        public ClientConfig build() {
            return new ClientConfig(Collections.unmodifiableCollection(endpoints));
        }
    }

}
