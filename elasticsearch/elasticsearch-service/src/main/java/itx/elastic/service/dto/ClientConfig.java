package itx.elastic.service.dto;

import org.apache.http.HttpHost;

import java.util.*;

public class ClientConfig {

    private final List<EndPoint> endpoints;

    public ClientConfig(List<EndPoint> endpoints) {
        Objects.requireNonNull(endpoints);
        this.endpoints = endpoints;
    }

    public Collection<EndPoint> getEndpoints() {
        return endpoints;
    }

    public HttpHost[] getHttpHostArray() {
        HttpHost[] hosts = new HttpHost[endpoints.size()];
        for (int i=0; i<hosts.length; i++) {
            EndPoint endPoint = endpoints.get(i);
            hosts[i] = new HttpHost(endPoint.getHostname(), endPoint.getPort(), endPoint.getScheme());
        }
        return hosts;
    }

    public static class Builder {

        private List<EndPoint> endpoints = new ArrayList<>();

        public Builder addEndPoint(String hostname, int port, String scheme) {
            Objects.requireNonNull(hostname);
            Objects.requireNonNull(scheme);
            endpoints.add(new EndPoint(hostname, port, scheme));
            return this;
        }

        public ClientConfig build() {
            return new ClientConfig(Collections.unmodifiableList(endpoints));
        }
    }

}
