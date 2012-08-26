package net.pickapack.spider.noJs.crawler.example;

public class HideMyPassHttpProxy {
    private String timeSinceLastCheck;
    private String proxyHost;
    private int proxyPort;
    private String country;
    private String protocol;
    private boolean reachable;
    private boolean portReachable;

    public HideMyPassHttpProxy(String timeSinceLastCheck, String proxyHost, int proxyPort, String country, String protocol) {
        this.timeSinceLastCheck = timeSinceLastCheck;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.country = country;
        this.protocol = protocol;
    }

    public String getTimeSinceLastCheck() {
        return timeSinceLastCheck;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getCountry() {
        return country;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public boolean isPortReachable() {
        return portReachable;
    }

    public void setPortReachable(boolean portReachable) {
        this.portReachable = portReachable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HideMyPassHttpProxy that = (HideMyPassHttpProxy) o;

        return proxyPort == that.proxyPort && !(proxyHost != null ? !proxyHost.equals(that.proxyHost) : that.proxyHost != null);
    }

    @Override
    public int hashCode() {
        int result = proxyHost != null ? proxyHost.hashCode() : 0;
        result = 31 * result + proxyPort;
        return result;
    }

    @Override
    public String toString() {
        return String.format("HideMyPassHttpProxy{timeSinceLastCheck='%s', proxyHost='%s', proxyPort=%d, country='%s', protocol='%s', reachable=%s, portReachable=%s}", timeSinceLastCheck, proxyHost, proxyPort, country, protocol, reachable, portReachable);
    }
}
