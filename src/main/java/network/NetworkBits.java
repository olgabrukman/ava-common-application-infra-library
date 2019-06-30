package network;


import validation.LongUtil;

public class NetworkBits implements Comparable<NetworkBits> {
    private long highBits;
    private long lowBits;

    // for JSON
    @SuppressWarnings("unused")
    public NetworkBits() {
    }

    public NetworkBits(long result) {
        this.highBits = 0;
        this.lowBits = result;
    }

    public NetworkBits(long highBits, long lowBits) {
        this.highBits = highBits;
        this.lowBits = lowBits;
    }

    public boolean isBiggerOrEqual(NetworkBits other) {
        if (LongUtil.isLessThanUnsigned(other.highBits, this.highBits)) {
            return true;
        }
        if (this.highBits == other.highBits) {
            if (LongUtil.isLessThanOrEqualUnsigned(other.lowBits, this.lowBits)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSmallerOrEqual(NetworkBits other) {
        if (LongUtil.isLessThanUnsigned(this.highBits, other.highBits)) {
            return true;
        }
        if (this.highBits == other.highBits) {
            if (LongUtil.isLessThanOrEqualUnsigned(this.lowBits, other.lowBits)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(NetworkBits other) {
        if (LongUtil.isLessThanUnsigned(this.highBits, other.highBits)) {
            return -1;
        }
        if (this.highBits == other.highBits) {
            if (LongUtil.isLessThanUnsigned(this.lowBits, other.lowBits)) {
                return -1;
            }
            if (this.lowBits == other.lowBits) {
                return 0;
            }
            return 1;
        }
        return 1;
    }

    public double minus(NetworkBits other) {
        long diff0 = this.highBits - other.highBits;
        long diff1 = this.lowBits - other.lowBits;
        if (diff0 == 0) {
            return diff1;
        }
        double result = Math.pow(2, 32) * diff0;
        return result + diff1;
    }

    // for JSON
    @SuppressWarnings("unused")
    public long getHighBits() {
        return highBits;
    }

    // for JSON
    @SuppressWarnings("unused")
    public long getLowBits() {
        return lowBits;
    }

    @Override
    public String toString() {
        return "NetworkBits{" +
                "highBits=" + highBits +
                ", lowBits=" + lowBits +
                '}';
    }
}
