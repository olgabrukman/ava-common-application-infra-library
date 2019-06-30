package network;

import logger.AppLogger;

public class IpsComparator {
    private static final AppLogger logger = AppLogger.getLogger(IpsComparator.class);

    static public int compare(String ip1, String ip2) {
        try {
            Network network1 = new Network(null, ip1, 32, false);
            Network network2 = new Network(null, ip2, 32, false);
            return network1.getLow().compareTo(network2.getLow());
        } catch (Exception e) {
            logger.warn("compare failed", e);
            return 0;
        }
    }
}
