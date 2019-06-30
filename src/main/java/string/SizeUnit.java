package string;

public enum SizeUnit {
    K(1),
    M(2),
    G(3),
    T(4);

    private long multiplierBasedDecimal;
    private long multiplierBasedBinary;

    SizeUnit(int power) {
        multiplierBasedDecimal = 1;
        multiplierBasedBinary = 1;
        for (int i = 0; i < power; i++) {
            multiplierBasedBinary *= 1024;
            multiplierBasedDecimal *= 1000;
        }
    }

    public long getMutiplier(boolean isBinaryBased) {
        if (isBinaryBased) {
            return multiplierBasedBinary;
        }
        return multiplierBasedDecimal;
    }
}
