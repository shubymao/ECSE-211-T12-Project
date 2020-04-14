package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import java.text.DecimalFormat;

/**
 * This class is used to access the robot's LCD screen and display whatever output
 * is desired.
 * 
 * @author charlesbourbeau
 *
 */
public class Display implements Runnable {

	/**
	 * A state variable for storing the x, y, theta value receive from the odometer.
	 */
	private double[] position;

	/**
	 * The period between each refresh of the display (ms).
	 */
	private static final long DISPLAY_PERIOD = 100;

	/**
	 * Time out time in (ms)
	 */
	private volatile boolean active = true;

	/**
	 * The thread's main task is to print the position color and angle of the vechicle in read time.
	 */
	public void run() {
		while (active) {
			TEXT_LCD.clear();
			// Retrieve x, y and Theta information
			position = odometer.getXyt();
			// Print x,y, and theta information
			DecimalFormat numberFormat = new DecimalFormat("######0.00");
			TEXT_LCD.drawString("X: " + numberFormat.format(position[0]), 0, 0);
			TEXT_LCD.drawString("Y: " + numberFormat.format(position[1]), 0, 1);
			TEXT_LCD.drawString("T: " + numberFormat.format(position[2]), 0, 2);
			TEXT_LCD.drawString("color    : " + ColorDetectionController.color, 0, 3);
			Main.sleepFor(DISPLAY_PERIOD);
		}
	}

	/**
	 * Stop the thread.
	 */
	public void stopDisplay() {
		active = false;
	}

	/**
	 * Shows the text on the LCD, line by line.
	 * 
	 * @param strings comma-separated list of strings, one per line
	 */
	public static void showText(String... strings) {
		TEXT_LCD.clear();
		for (int i = 0; i < strings.length; i++) {
			TEXT_LCD.drawString(strings[i], 0, i);
		}
	}

	/**
	 * Shows the option of the main selection given the option
	 * @param currentOption, a string in which the user specify the option to display to the user.
	 */
	public static void printOption(String currentOption) {
		TEXT_LCD.clear();
		TEXT_LCD.drawString(currentOption, 0, 3);
	}

}
