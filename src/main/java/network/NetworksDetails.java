package network;

import validation.CollectionUtil;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@org.codehaus.jackson.annotate.JsonIgnoreProperties(ignoreUnknown = true)
public class NetworksDetails {
    private List<Network> networks;

    public NetworksDetails() {
        this.networks = new LinkedList<>();
    }

    public NetworksDetails(Network network) {
        this.networks = new LinkedList<>();
        networks.add(network);
    }

    public NetworksDetails(List<Network> networks) {
        this.networks = networks;
    }

    @Override
    public String toString() {
        return networks.toString();
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getUserFriendly() {
        LinkedList<String> sorted = new LinkedList<>();
        for (Network network : networks) {
            sorted.add(network.getCidr());
        }
        Collections.sort(sorted);
        return CollectionUtil.getNiceList(sorted);
    }

    public List<Network> getNetworks() {
        return networks;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    public List<Network> getNetworksSorted() {
        LinkedList<Network> sorted = new LinkedList<>(networks);
        Collections.sort(sorted, new NetworkComparator());
        return sorted;
    }

    public boolean isOverlapping(NetworksDetails otherDetails) throws Exception {
        for (Network myNetwork : networks) {
            for (Network otherNetwork : otherDetails.networks) {
                if (myNetwork.isOverlapping(otherNetwork)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOverlapping(String cidr) throws Exception {
        for (Network network : networks) {
            if (network.isOverlapping(cidr)) {
                return true;
            }
        }
        return false;
    }

    public void add(Short vlan, String cidr) throws Exception {
        Network network = new Network(vlan, cidr, true);
        add(network);
    }

    public void add(Network network) throws Exception {
        networks.add(network);
    }

    public boolean containsExact(Short vlan, String cidr) throws Exception {
        Network network = new Network(vlan, cidr, false);
        return containsExact(network);
    }

    public boolean removeAllEquals(Short vlan, String cidr) throws Exception {
        Network network = new Network(vlan, cidr, false);
        return removeAllEquals(network);
    }

    public boolean removeAllEquals(Network checkedNetwork) throws Exception {
        boolean removed = false;
        Iterator<Network> networksIterator = this.networks.iterator();
        while (networksIterator.hasNext()) {
            Network network = networksIterator.next();
            if (network.equals(checkedNetwork)) {
                networksIterator.remove();
                removed = true;
            }
        }

        return removed;
    }

    public boolean removeAllEquals(NetworksDetails otherNetworks) throws Exception {
        boolean removed = false;
        Iterator<Network> networksIterator = this.networks.iterator();
        while (networksIterator.hasNext()) {
            Network network = networksIterator.next();
            if (otherNetworks.containsExact(network)) {
                networksIterator.remove();
                removed = true;
            }
        }

        return removed;
    }

    public boolean retainOnly(NetworksDetails otherNetworks) throws Exception {
        boolean removed = false;
        Iterator<Network> networksIterator = this.networks.iterator();
        while (networksIterator.hasNext()) {
            Network network = networksIterator.next();
            if (!otherNetworks.containsExact(network)) {
                networksIterator.remove();
                removed = true;
            }
        }

        return removed;
    }

    public boolean retainOnlyIncludedBy(NetworksDetails otherNetworks) throws Exception {
        boolean removed = false;
        Iterator<Network> networksIterator = this.networks.iterator();
        while (networksIterator.hasNext()) {
            Network network = networksIterator.next();
            if (!otherNetworks.isIncluding(network)) {
                networksIterator.remove();
                removed = true;
            }
        }

        return removed;
    }

    public boolean containsExact(Network checkedNetwork) throws Exception {
        for (Network network : this.networks) {
            if (network.equals(checkedNetwork)) {
                return true;
            }
        }

        return false;
    }

    public boolean isIncluding(Network includedNetwork) throws Exception {
        return isIncluding(includedNetwork.getVlan(), includedNetwork.getCidr());
    }

    public boolean isIncluding(NetworksDetails includedNetworks) throws Exception {
        for (Network includedNetwork : includedNetworks.getNetworks()) {
            if (!isIncluding(includedNetwork)) {
                return false;
            }
        }
        return true;
    }

    public boolean isIncluding(Short includedVlan, String includedCidr) throws Exception {
        for (Network network : this.networks) {
            if (network.isIncluding(includedVlan, includedCidr)) {
                return true;
            }
        }
        return false;
    }

    public NetworksDetails deepClone() throws Exception {
        NetworksDetails networksDetails = new NetworksDetails();
        for (Network network : this.networks) {
            networksDetails.add(network.getVlan(), network.getCidr());
        }
        return networksDetails;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        NetworksDetails that = (NetworksDetails) other;
        return networksEquals(this.networks, that.networks);
    }

    private boolean networksEquals(List<Network> networks1, List<Network> networks2) {
        if (networks1.size() != networks2.size()) {
            return false;
        }

        List<Network> all = new LinkedList<>();
        all.addAll(networks1);
        all.removeAll(networks2);

        return all.isEmpty();
    }

    public void updateAfterJsonBuild() throws Exception {
        for (Network network : networks) {
            network.updateAfterJsonBuild();
        }
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    public boolean isEmpty() {
        return networks.isEmpty();
    }
}
