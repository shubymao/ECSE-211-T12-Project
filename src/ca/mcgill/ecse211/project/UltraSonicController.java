package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

public class UltraSonicController implements Runnable {

    /**
     * Flag for stopping the controller.
     */
    private volatile boolean active = true;
    /**
     * The distance remembered by the {@code filter()} method.
     */
    private static int prevDistance = Integer.MAX_VALUE;

    /**
     * The distance to be shown in the display.
     */
    private int dist = Integer.MAX_VALUE;

    /**
     * The number of invalid samples seen by {@code filter()} so far.
     */
    private static int invalidSampleCount;

    /**
     * Buffer (array) to store US samples. Declared as an instance variable to avoid creating a new
     * array each time {@code readUsSample()} is called.
     */
    private static float[] usData = new float[usSensor.sampleSize()];

    /**
     * Keep the ticCount when sensor see the newest lowest distance.
     */
    private long minTic = -1;

    /**
     * For calculating the central point.
     */
    private long endTick = 0;

    /**
     * The lowest distance seem.
     */
    private int minDist = Integer.MAX_VALUE;


    /**
     * Fetch the sample and return the current distance in cm
     * @return distance of the object nearest to us sensor.
     */
    public static int GetCurrentDistance() {
        usSensor.fetchSample(usData, 0);
        int curDist = filter((int) (usData[0] * 100.0));
        return curDist;
    }

    /**
     * reset the tick count and all measurement related parameter.
     */
    public void reset() {
        active = true;
        endTick = 0;
        minTic = -1;
        minDist = Integer.MAX_VALUE;
        dist = Integer.MAX_VALUE;
        prevDistance = Integer.MAX_VALUE;
    }

    /**
     * Set the flag to false and end the thread
     */
    public void stopController() {
        active = false;
    }

    /**
     * return the current distance
     * 
     * @return current status of the us sensor
     */
    public String getCurStatus() {
        return "Current Distance" + dist;
    }

    /**
     * Returns number of index away from the minimum value
     * 
     * @return number of index away from the minimum value
     */
    public long getAvgMinDistIndex() {
        return minTic;
    }

    /**
     * return the average distance for a given detection.
     * 
     * @return average distance for given detection
     */
    public double getCurDist() {
        return dist;
    }

    /*
     * Samples the US sensor and invokes the selected controller on each cycle (non Javadoc).
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
        while (active) {
            readUsDistance();
            Main.sleepFor(POLL_SLEEP_TIME);
        }
    }

    /**
     * Returns the filtered distance between the US sensor and an obstacle in cm.
     * 
     * @return the filtered distance between the US sensor and an obstacle in cm
     */
    public int readUsDistance() {
        usSensor.fetchSample(usData, 0);
        // extract from buffer, convert to cm, cast to int, and filter
        int curDist = filter((int) (usData[0] * 100.0));
        if (curDist < minDist) {
            minDist = curDist;
            minTic = System.currentTimeMillis();
            endTick = minTic;
        } else if (curDist == minDist && endTick != 0) {
            endTick = System.currentTimeMillis();
        } else if (endTick != 0) {
            minTic = (minTic + endTick) / 2;
            endTick = 0;
        }
        this.dist = curDist;
        return curDist;
    }
    
    /**
     * minimum distance when sweeping a distance in cm.
     * @return minimum distance in the sweep.
     */
    public static boolean isObstacle() {
        Navigation.setSpeed(EXTRA_SLOW_SPEED);
        Navigation.turnBy(-HALF_HALF_SMALL_ROTATION, true);
        Navigation.turnBy(HALF_SMALL_ROTATION, false);
        int minDist = 255;
        while(leftMotor.isMoving()) {
            minDist = Math.min(minDist, GetCurrentDistance());
            Main.sleepFor(POLL_SLEEP_TIME);
        }
        Navigation.turnBy(-HALF_HALF_SMALL_ROTATION, true);
        return minDist < OBSTACLE_THRESHOLD;
    }

    /**
     * Rudimentary filter - toss out invalid samples corresponding to null signal.
     * 
     * @param distance raw distance measured by the sensor in cm
     * @return the filtered distance in cm
     */
    static int filter(int distance) {
        if (distance >= 255 && invalidSampleCount < INVALID_SAMPLE_LIMIT) {
            // bad value, increment the filter value and return the distance remembered from
            // before
            invalidSampleCount++;
            return prevDistance;
        } else {
            if (distance < 255) {
                // distance went below 255: reset filter and remember the input distance.
                invalidSampleCount = 0;
            }
            prevDistance = distance;
            return distance;
        }
    }
}
