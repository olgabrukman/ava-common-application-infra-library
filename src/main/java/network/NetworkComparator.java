package network;

public class NetworkComparator implements java.util.Comparator<Network> {
    @Override
    public int compare(Network n1, Network n2) {
        NetworkBits from1 = n1.getLow();
        NetworkBits from2 = n2.getLow();
        return from1.compareTo(from2);
    }
}
