package network;

public class TrafficVolume {
    private Long bytesPerSecond;
    private Long packetsPerSecond;

    public TrafficVolume() {
        // used for JSON parsing - do not remove
    }

    public TrafficVolume(Long bytesPerSecond, Long packetsPerSecond) {
        this.bytesPerSecond = bytesPerSecond;
        this.packetsPerSecond = packetsPerSecond;
    }

    @Override
    public String toString() {
        return bytesPerSecond + "bps / " + packetsPerSecond + "pps";
    }

    public Long getBytesPerSecond() {
        return bytesPerSecond;
    }

    public Long getPacketsPerSecond() {
        return packetsPerSecond;
    }
}
