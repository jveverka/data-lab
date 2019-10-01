package itx.elastic.service.dto;

public class EndPoint {

    private final String hostname;
    private final int port;
    private final String scheme;

    public EndPoint(String hostname, int port, String scheme) {
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
