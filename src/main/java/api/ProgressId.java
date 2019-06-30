package api;

import java.util.Random;

public class ProgressId {
    private String id;

    private ProgressId(String id) {
        this.id = id;
    }

    public static ProgressId generate() {
        String id = System.currentTimeMillis() + "_" + Math.abs(new Random().nextLong());
        return new ProgressId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgressId that = (ProgressId) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ProgressId{" +
                "id='" + id + '\'' +
                '}';
    }
}
