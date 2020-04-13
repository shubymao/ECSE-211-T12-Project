package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * Provides methods which helps with light localization and light intensity detection.
 * @author Shuby
 *
 */
public class LightSensorController {

    /**
     * Left light sensor sample provider.
     */
    private static SampleProvider lightSensorL = leftLightSensor.getMode("Red");

    /**
     * Right light sensor sample provider.
     */
    private static SampleProvider lightSensorR = rightLightSensor.getMode("Red");

    /**
     * Float array where left sample is stored.
     */
    private static float[] leftLightData = new float[lightSensorL.sampleSize()];

    /**
     * Float array where right sample is stored.
     */
    private static float[] rightLightData = new float[lightSensorR.sampleSize()];

    /**
     * Measure and fetch the intensity of the left sensor
     * 
     * @return The detected intensity of the left sensor
     */
    public static float readLeftLightSensor() {
        lightSensorL.fetchSample(leftLightData, 0);
        return leftLightData[0] * 1000;
    }

    /**
     * Measure and fetch the intensity of the right sensor
     * 
     * @return The detected intensity of the right sensor
     */
    public static float readRightLightSensor() {
        lightSensorR.fetchSample(rightLightData, 0);
        return rightLightData[0] * 1000;
    }

    /**
     * The move until the robot detected line on both side of the wheel.
     */
    public static void lineLocalization() {
        Navigation.setSpeed(MEDIUM_SLOW_SPEED);
        Navigation.moveStraightFor(TILE_SIZE, false);
        lightSensorL.fetchSample(leftLightData, 0);
        lightSensorR.fetchSample(rightLightData, 0);
        float prevLeft = leftLightData[0] * 100;
        float prevRight = rightLightData[0] * 100;
        float deltaLeft = 0;
        float deltaRight = 0;
        boolean left = true;
        boolean right = true;
        while (leftMotor.isMoving() || rightMotor.isMoving()) {
            if (deltaLeft < LINE_DELTA_THRESHOLD && left) {
                leftMotor.stop();
                left = false;
                Sound.beep();
                if(right) {
                    Navigation.setSpeed(SLOW_SPEED);
                    rightMotor.backward();
                    Main.sleepFor(DEFAULT_SLEEP_TIME);
                    rightMotor.forward();
                }
            }
            if (deltaRight < LINE_DELTA_THRESHOLD && right) {
                rightMotor.stop();
                right = false;
                Sound.beep();
                if(left) {
                    Navigation.setSpeed(SLOW_SPEED);
                    leftMotor.backward();
                    Main.sleepFor(DEFAULT_SLEEP_TIME);
                    leftMotor.forward();
                }
            }
            lightSensorL.fetchSample(leftLightData, 0);
            lightSensorR.fetchSample(rightLightData, 0);
            deltaLeft = leftLightData[0] * 100 - prevLeft;
            deltaRight = rightLightData[0] * 100 - prevRight;
            prevRight = rightLightData[0] * 100;
            prevLeft = leftLightData[0] * 100;
            Main.sleepFor(CS_POLL_SLEEP_TIME);
        }
        Navigation.setSpeed(FAST_SPEED);
    }
}
