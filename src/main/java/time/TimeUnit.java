package time;

public enum TimeUnit {
    SECOND(1000),
    MINUTE(60000),
    HOUR(3600000),
    DAY(86400000),
    WEEK(604800000),
    MONTH(2592000000L),
    YEAR(31536000000L);

    private long time;

    TimeUnit(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
