package itx.elastic.service.dto;

import java.util.Objects;

public class EndPoint {

    private final String hostname;
    private final int port;
    private final String scheme;

    public EndPoint(String hostname, int port, String scheme) {
        Objects.requireNonNull(hostname);
        Objects.requireNonNull(scheme);
        this.hostname = hostname;
        this.port = port;
        this.scheme = scheme;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getScheme() {
        return scheme;
    }

}
