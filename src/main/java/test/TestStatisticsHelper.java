package test;

import java.util.LinkedList;
import java.util.List;

public class TestStatisticsHelper {
    private boolean activated;
    private List<Long> timings;
    private Long currentStartTime;

    public TestStatisticsHelper() {
        activated = true;
        currentStartTime = null;
        timings = new LinkedList<>();
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void resetTimer() {
        if (activated) {
            currentStartTime = System.currentTimeMillis();
        }
    }

    public void stopTimer() {
        if (activated && currentStartTime != null) {
            timings.add(System.currentTimeMillis() - currentStartTime);
        }
    }


    public void printStatistics() {
        int numOfMeasurements = timings.size();
        float average = 0;
        float maximum = 0;
        float minimum = Long.MAX_VALUE;

        for (Long measurement : timings) {
            average += measurement;
            maximum = maximum > measurement ? maximum : measurement;
            minimum = minimum < measurement ? minimum : measurement;
        }
        average /= numOfMeasurements;

        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("Statistics of timings are:");
        System.out.println("Number of measured timings: " + numOfMeasurements);
        System.out.print("Actual timings are (mSecs): {");
        for (Long measurement : timings) {
            System.out.print(measurement + " ");
        }
        System.out.println("}");
        System.out.println("Average time (mSecs):       " + average);
        System.out.println("Maximum time (mSecs):       " + maximum);
        System.out.println("Minimum time (mSecs):       " + minimum);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    }
}
