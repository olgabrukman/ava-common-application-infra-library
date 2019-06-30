package alert;

public enum AlertSeverity {
    DEBUG(10),
    INFO(20),
    WARNING(30),
    ERROR(40),
    FATAL(50);

    private int level;

    AlertSeverity(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

