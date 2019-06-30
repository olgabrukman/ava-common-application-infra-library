package validation;

import network.IpVersion;
import network.Network;
import org.apache.commons.validator.GenericValidator;
import resource.MessageApi;
import string.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NetworkValidation {
    @SuppressWarnings("all")
    static public String validateAndUpdateIp(String fieldName, String ip, IpVersion version, boolean blockRadwareInternal,
                                             boolean mandatory) throws Exception {
        if (StringUtil.isEmpty(ip)) {
            // allow reset of values from CLI/GUI by empty string
            ip = null;
        }

        if (ip == null) {
            if (!mandatory) {
                return null;
            }
        }

        GeneralValidation.validateNotEmpty(fieldName, ip);

        ip = removeLeadingZeros(ip);
        // IPv6 standard is lower case, also match ExaBGP log
        ip = ip.toLowerCase();

        if (version == null) {
            if (!InetAddressValidator.getInstance().isValidInet4Address(ip) &&
                    !InetAddressValidator.getInstance().isValidInet6Address(ip)) {
                //ip is an invalid IP address
                throw MessageApi.getException("app00916",
                        "IP", ip,
                        "FIELD_NAME", fieldName);
            }
        } else if (version == IpVersion.IPV4) {
            if (!InetAddressValidator.getInstance().isValidInet4Address(ip)) {
                //ip is an invalid IPv4 address
                throw MessageApi.getException("app00917",
                        "IP", ip,
                        "FIELD_NAME", fieldName);
            }
        } else {
            if (!InetAddressValidator.getInstance().isValidInet6Address(ip)) {
                //ip is an invalid IPv6 address
                throw MessageApi.getException("app00918",
                        "IP", ip,
                        "FIELD_NAME", fieldName);
            }
        }


        if (blockRadwareInternal) {
            if (ip.startsWith("1.1.1.")) {
                //1.1.1.x network is reserved for DefensePro internal usage
                throw MessageApi.getException("app00919");
            }
            if (ip.startsWith("100.64.0..")) {
                //100.64.0.x network is reserved for DefenseFlow internal usage
                throw MessageApi.getException("app00923");
            }
        }
        return ip;
    }

    /*
     * while not according to RFC, we remove trailing zeros from IPv4 IP
     * for example:193.008.000.011 will be updated to 193.8.0.11
     */
    public static String removeLeadingZeros(String ip) {
        if (ip == null) {
            return null;
        }
        ip = ip.trim();
        Pattern pattern = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
        Matcher matcher = pattern.matcher(ip);
        if (!matcher.find()) {
            return ip;
        }

        return removeLeadingZerosSection(matcher.group(1)) + "." +
                removeLeadingZerosSection(matcher.group(2)) + "." +
                removeLeadingZerosSection(matcher.group(3)) + "." +
                removeLeadingZerosSection(matcher.group(4));
    }

    private static String removeLeadingZerosSection(String section) {
        return section.replaceFirst("^0+(?!$)", "");
    }

    static public boolean isIpv4(String ip) throws Exception {
        return InetAddressValidator.getInstance().isValidInet4Address(ip);
    }

    static public boolean isIpv6(String ip) throws Exception {
        return InetAddressValidator.getInstance().isValidInet6Address(ip);
    }

    static public void validateMask(String fieldName, String mask, IpVersion version, boolean mandatory)
            throws Exception {
        if (GenericValidator.isBlankOrNull(mask)) {
            if (!mandatory) {
                return;
            }
        }
        GeneralValidation.validateNotEmpty(fieldName, mask);

        if (version == IpVersion.IPV4) {
            if (!InetAddressValidator.getInstance().isValid(mask)) {
                //mask is an invalid IP mask
                throw MessageApi.getException("app00924",
                        "MASK", mask,
                        "FIELD_NAME", fieldName);
            }
        } else {
            GeneralValidation.validateInteger(fieldName, mask, mandatory);
            int value = Integer.parseInt(mask);
            if ((value < 1) || (value > 128)) {
                // mask should be in range 1-128
                throw MessageApi.getException("app00925",
                        "MASK", mask,
                        "FIELD_NAME", fieldName);
            }
        }

        if (mask.equals("0.0.0.0")) {
            //mask is an invalid IP mask
            throw MessageApi.getException("app00924",
                    "MASK", mask,
                    "FIELD_NAME", fieldName);
        }
    }

    static public void validatePort(String fieldName, String port, boolean mandatory)
            throws Exception {

        if (GenericValidator.isBlankOrNull(port)) {
            if (!mandatory) {
                return;
            }
        }

        GeneralValidation.validateNotEmpty(fieldName, port);

        int portInt = Integer.parseInt(port);
        if ((portInt < 1) || (portInt > 65535)) {
            //invalid port; port should be in range 1-65535
            throw MessageApi.getException("app00926",
                    "PORT", port);
        }
    }

    static public String validateAndUpdateCidr(String fieldName, String cidr, boolean blockRadwareInternal,
                                               boolean mandatory) throws Exception {
        if (GenericValidator.isBlankOrNull(cidr)) {
            if (!mandatory) {
                return cidr;
            }
        }

        GeneralValidation.validateNotEmpty(fieldName, cidr);

        Network network = new Network(null, cidr, true);
        validateAndUpdateIp(fieldName, network.getIp(), null, blockRadwareInternal, mandatory);
        return network.getCidr();
    }

    static public boolean isSameVersion(String cidr1, String cidr2) throws Exception {
        String ip1 = new Network(null, cidr1, false).getIp();
        String ip2 = new Network(null, cidr2, false).getIp();
        return isIpv4(ip1) && isIpv4(ip2) || isIpv6(ip1) && isIpv6(ip2);
    }
}
