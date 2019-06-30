package network;

public class DefenseProBaselines {
    private float baselineIcmpBytesPerSecond;
    private float baselineIcmpPacketsPerSecond;
    private float baselineTcpBytesPerSecond;
    private float baselineTcpPacketsPerSecond;
    private float baselineUdpBytesPerSecond;
    private float baselineUdpPacketsPerSecond;

    public float getBaselineIcmpBytesPerSecond() {
        return baselineIcmpBytesPerSecond;
    }

    public void setBaselineIcmpBytesPerSecond(float baselineIcmpBytesPerSecond) {
        this.baselineIcmpBytesPerSecond = baselineIcmpBytesPerSecond;
    }

    public float getBaselineIcmpPacketsPerSecond() {
        return baselineIcmpPacketsPerSecond;
    }

    public void setBaselineIcmpPacketsPerSecond(float baselineIcmpPacketsPerSecond) {
        this.baselineIcmpPacketsPerSecond = baselineIcmpPacketsPerSecond;
    }

    public float getBaselineTcpBytesPerSecond() {
        return baselineTcpBytesPerSecond;
    }

    public void setBaselineTcpBytesPerSecond(float baselineTcpBytesPerSecond) {
        this.baselineTcpBytesPerSecond = baselineTcpBytesPerSecond;
    }

    public float getBaselineTcpPacketsPerSecond() {
        return baselineTcpPacketsPerSecond;
    }

    public void setBaselineTcpPacketsPerSecond(float baselineTcpPps) {
        this.baselineTcpPacketsPerSecond = baselineTcpPps;
    }

    public float getBaselineUdpBytesPerSecond() {
        return baselineUdpBytesPerSecond;
    }

    public void setBaselineUdpBytesPerSecond(float baselineUdpBytesPerSecond) {
        this.baselineUdpBytesPerSecond = baselineUdpBytesPerSecond;
    }

    public float getBaselineUdpPacketsPerSecond() {
        return baselineUdpPacketsPerSecond;
    }

    public void setBaselineUdpPacketsPerSecond(float baselineUdpPps) {
        this.baselineUdpPacketsPerSecond = baselineUdpPps;
    }

    @Override
    public String toString() {
        return "DefenseProBaselines{" +
                "baselineIcmpBytesPerSecond=" + baselineIcmpBytesPerSecond +
                ", baselineIcmpPacketsPerSecond=" + baselineIcmpPacketsPerSecond +
                ", baselineTcpBytesPerSecond=" + baselineTcpBytesPerSecond +
                ", baselineTcpPacketsPerSecond=" + baselineTcpPacketsPerSecond +
                ", baselineUdpBytesPerSecond=" + baselineUdpBytesPerSecond +
                ", baselineUdpPacketsPerSecond=" + baselineUdpPacketsPerSecond +
                '}';
    }
}
