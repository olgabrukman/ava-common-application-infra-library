package network;


import com.fasterxml.jackson.annotation.JsonIgnore;
import exception.AppException;
import org.apache.commons.net.util.SubnetUtils;
import resource.MessageApi;
import validation.NetworkValidation;
import validation.ShortUtil;

import java.net.InetAddress;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown = true)
public class Network {
    public static final long MASK = 0xffffffffl;

    private Short vlan;
    private String ip;
    private int prefix;
    @JsonIgnore
    private NetworkBits low;
    @JsonIgnore
    private NetworkBits high;

    public Network() {
        // used for JSON parsing - do not remove
    }

    public Network(Short vlan, String cidr, boolean toValidateIpv4) throws Exception {
        if (cidr == null) {
            throw new AppException("CIDR is null");
        }
        cidr = cidr.trim();
        String[] parts = cidr.split("/");
        if (parts.length == 1) {
            if (NetworkValidation.isIpv4(cidr)) {
                init(vlan, cidr, 32, toValidateIpv4);
            } else if (NetworkValidation.isIpv6(cidr)) {
                init(vlan, cidr, 128, toValidateIpv4);
            } else {
                throw getInvalidIpError(cidr);
            }
            return;
        }
        if (parts.length == 2) {
            init(vlan, parts[0], Integer.parseInt(parts[1]), toValidateIpv4);
            return;
        }

        throw new AppException(cidr + " should be in format IP or IP/prefix");
    }

    static public Exception getInvalidIpError(String ip) {
        // invalid IP address
        return MessageApi.getException("app00984",
                "IP", ip);
    }

    static public Exception getInvalidCidrError(String wrongIp, String correctIp, int prefix) {
        // invalid cidr
        return MessageApi.getException("app01064",
                "WRONG_CIDR", wrongIp + "/" + prefix,
                "CORRECT_CIDR", correctIp + "/" + prefix);
    }

    public Network(Short vlan, String ip, int prefix, boolean toValidateIpv4) throws Exception {
        init(vlan, ip, prefix, toValidateIpv4);
    }

    private void init(Short vlan, String ip, int prefix, boolean toValidateIpv4) throws Exception {
        ip = NetworkValidation.removeLeadingZeros(ip);
        this.vlan = vlan;
        this.ip = ip.toLowerCase();
        this.prefix = prefix;
        if (NetworkValidation.isIpv4(ip)) {
            calculateLowAndHighIpv4(ip, prefix, toValidateIpv4);
        } else {
            throw getInvalidIpError(ip);
        }
    }


    private void calculateLowAndHighIpv4(String ip, int prefix, boolean toValidateCidr) throws Exception {
        SubnetUtils utils = new SubnetUtils(getCidr());
        SubnetUtils.SubnetInfo info = utils.getInfo();

        long address = MASK & info.asInteger(info.getAddress());
        long lowAddress = MASK & info.asInteger(info.getLowAddress());
        long network = MASK & info.asInteger(info.getNetworkAddress());
        long highAddress = MASK & info.asInteger(info.getHighAddress());

        long result = address;
        if (lowAddress != 0) {
            result = Math.min(result, lowAddress);
        } else {
            lowAddress = address;
        }
        if (network != 0) {
            result = Math.min(result, network);
        } else {
            network = address;
        }

        low = new NetworkBits(result);

        if (toValidateCidr) {
            InetAddress lowProcessed = InetAddress.getByName(String.valueOf(lowAddress));
            InetAddress networkProcessed = InetAddress.getByName(String.valueOf(network));
            if (!(ip.equals(lowProcessed.getHostAddress()) || ip.equals(networkProcessed.getHostAddress()))) {
                throw getInvalidCidrError(ip, networkProcessed.getHostAddress(), prefix);
            }
        }

        result = address;
        if (highAddress != 0) {
            result = Math.max(result, highAddress);
        }
        high = new NetworkBits(result);
    }

    @Override
    public String toString() {
        if (vlan != null) {
            return "vlan:" + vlan + " cidr: " + getCidr();
        }

        return getCidr();
    }

    public String getIp() {
        return ip;
    }

    public int getPrefix() {
        return prefix;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getCidr() {
        return ip + '/' + prefix;
    }

    public Short getVlan() {
        return vlan;
    }

    public boolean isMatchVlan(Short checkVlan) throws Exception {
        if ((this.vlan == null) && (checkVlan == null)) {
            return true;
        }
        if ((this.vlan != null) && (checkVlan != null)) {
            if (this.vlan.equals(checkVlan)) {
                return true;
            }
        }
        return false;
    }

    public boolean isIncluding(Network includedNetwork) throws Exception {
        return isIncluding(includedNetwork.getVlan(), includedNetwork.getCidr());
    }

    public boolean isIncluding(Short includedVlan, String includedCidr) throws Exception {
        return isMatchVlan(includedVlan) && isIncluding(includedCidr);
    }

    @JsonIgnore
    public NetworkBits getLow() {
        return low;
    }

    @JsonIgnore
    public NetworkBits getHigh() {
        return high;
    }

    public boolean isIncluding(String includedCidr) throws Exception {
        Network includedNetwork = new Network(null, includedCidr, false);
        return includedNetwork.low.isBiggerOrEqual(this.low)
                && includedNetwork.high.isSmallerOrEqual(high);
    }

    public boolean isOverlapping(Network overlappingNetwork) throws Exception {
        return isOverlapping(overlappingNetwork.getVlan(), overlappingNetwork.getCidr());
    }

    public boolean isOverlapping(Short overlappingVlan, String overlappingCidr) throws Exception {
        return isMatchVlan(overlappingVlan) && isOverlapping(overlappingCidr);
    }

    public boolean isOverlapping(String overlappingCidr) throws Exception {
        Network overlappingNetwork = new Network(null, overlappingCidr, false);
        if (overlappingNetwork.low.isBiggerOrEqual(this.low) &&
                overlappingNetwork.low.isSmallerOrEqual(this.high)) {
            return true;
        }
        if (overlappingNetwork.high.isBiggerOrEqual(this.low) &&
                overlappingNetwork.high.isSmallerOrEqual(high)) {
            return true;
        }
        return overlappingNetwork.low.isSmallerOrEqual(this.low) &&
                overlappingNetwork.high.isBiggerOrEqual(high);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    public double getAddressesAmount() throws Exception {
        return high.minus(low) + 1;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Network otherNetwork = (Network) other;
        return equals(otherNetwork.getVlan(), otherNetwork.getCidr());
    }

    public boolean equals(Short vlan, String cidr) {
        return cidr.equals(getCidr()) && ShortUtil.equalsIgnoreNull(vlan, this.vlan);
    }

    public boolean equalsIpOrCidr(String ipOrCidr) throws Exception {
        Network checked = new Network(null, ipOrCidr, false);
        return checked.getCidr().equals(getCidr());
    }

    @Override
    public int hashCode() {
        int result = vlan != null ? vlan.hashCode() : 0;
        result = 31 * result + ip.hashCode();
        result = 31 * result + prefix;
        return result;
    }

    public static int convertToPrefix(String ip, String interfaceMask, boolean isIpv4) {
        if (isIpv4) {
            SubnetUtils utils = new SubnetUtils(ip, interfaceMask);
            SubnetUtils.SubnetInfo info = utils.getInfo();
            long address = MASK & info.asInteger(info.getAddress());

            long lowAddress = MASK & info.asInteger(info.getLowAddress());
            long lowValue = address;
            if (lowAddress != 0) {
                lowValue = Math.min(lowValue, lowAddress);
            }
            long network = MASK & info.asInteger(info.getNetworkAddress());
            if (network != 0) {
                lowValue = Math.min(lowValue, network);
            }

            long highAddress = MASK & info.asInteger(info.getHighAddress());
            long highValue = highAddress;
            if (highAddress != 0) {
                highValue = Math.max(lowValue, highAddress);
            }
            int amount = (int) (highValue - lowAddress);
            return 32 - (int) (Math.log(amount + 3) / Math.log(2));
        }
        return Integer.parseInt(interfaceMask);
    }

    public void updateAfterJsonBuild() throws Exception {
        init(this.vlan, this.ip, this.prefix, false);
    }
}
