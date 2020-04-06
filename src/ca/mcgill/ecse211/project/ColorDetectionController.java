package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import lejos.hardware.Sound;

public class ColorDetectionController implements Runnable {

	/**
	 * color samples to record the value. The first three indices contain R G and B respectively.
	 */
	public static float[] colorSamples = new float[myColorSample.sampleSize()];

	/**
	 * Dictionary to store the rings RGB arrays and easy retrieval from their color
	 * which is going to be the key in the dict.
	 */
	public static Dictionary<String, double[]> RINGS_RGB_NORM;

	/**
	 * The color field stored the current color detected by the color sensor. It is either yellow, orange, green, blue, or gound if the eucl distance is large.
	 */
	public static String color = "";

	/**
	 * Color list to print out the answer.
	 */
	public static ArrayList<String> colors;
	/**
	 * The minimal_dist field stores the current minimal distance measured by the sensor from any ring color. 
	 */
	public static double minimal_dist = 0;

	/**
	 * A boolean to keep end the thread.
	 */
	private volatile boolean active = true;

	/**
	 * Constructor
	 */
	public ColorDetectionController() {
		// populate the ring RGB dictionary

	}

	public static void initNorm() {
	  RINGS_RGB_NORM = new Hashtable<String, double[]>();
	  colors = new ArrayList<>();
      RINGS_RGB_NORM.put("YELLOW", YELLOW_RING_RGB_NORM);
      RINGS_RGB_NORM.put("BLUE", BLUE_RING_RGB_NORM);
      RINGS_RGB_NORM.put("ORANGE", ORANGE_RING_RGB_NORM);
      RINGS_RGB_NORM.put("GREEN", GREEN_RING_RGB_NORM);
	}
	/**
	 * @return the average intensity
	 */
	private static void readColor() {
		myColorSample.fetchSample(colorSamples, 0);
	}

	/**
	 * Show the color on display.
	 */
	public static void showColors() {
	  MainController.stopDisplay();
	    String[] cs = new String[colors.size()+1];
	    cs[0] = "Num Obj: "+colors.size();
	    int i = 1; 
	    for(String c: colors) {
	      cs[i++]=c;
	    }
	    Display.showText(cs);
	    Main.sleepFor(4000);
	}
	
	/**
	 * stop the motor and print the color into the display.
	 */
	public static void stopAndDisplayColor() {
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		
		MainController.stopDisplay();
		Display.printOption(color);
		colors.add(color);
		Sound.twoBeeps();
		// Sleep for 10 second.
		Main.sleepFor(4000);
		MainController.startDisplay();
		Main.sleepFor(1000);
	}

	private void pollColorSensor() {
		// populate the colorSamples array with the current RGB values read
		readColor();

		// array to store the RGB values read from the sensor
		double[] RGB_array = {
				(double) colorSamples[0],
				(double) colorSamples[1],
				(double) colorSamples[2]
		};


		// normalize the rgb array
		normalize_RGB_array(RGB_array);

		double yellow_ring_dist = euclidian_distance(RGB_array, "YELLOW");
		double blue_ring_dist = euclidian_distance(RGB_array, "BLUE");
		double orange_ring_dist = euclidian_distance(RGB_array, "ORANGE");
		double green_ring_dist = euclidian_distance(RGB_array, "GREEN");


		double minimal_dist_temp1 = Math.min(yellow_ring_dist, blue_ring_dist);
		double minimal_dist_temp2 = Math.min(orange_ring_dist, green_ring_dist);
		minimal_dist = Math.min(minimal_dist_temp1, minimal_dist_temp2);

		// we have now found the minimal euclidian distance
		// find the color associated with the distance
		if(minimal_dist >= MIN_GROUND_EUCLID_DIST)	
			color = "GROUND";
		else if(minimal_dist == yellow_ring_dist) 
			color = "YELLOW";
		else if(minimal_dist == blue_ring_dist) 
			color = "BLUE";
		else if(minimal_dist == orange_ring_dist) 
			color = "ORANGE";
		else if(minimal_dist == green_ring_dist) 
			color = "GREEN";
	}

	/**
	 * Method to modify a RGB array to a normalized RGB array
	 * @param RGB_array
	 */
	public static void normalize_RGB_array(double[] RGB_array) {
		assert RGB_array.length == 3;

		double R = RGB_array[0];
		double G = RGB_array[1];
		double B = RGB_array[2];

		double normalizor = sqrt(pow(R,2) + pow(G,2) + pow(B,2));

		if (normalizor == 0) {
			RGB_array[0] = 0;
			RGB_array[1] = 0;
			RGB_array[2] = 0;
			return;
		}

		for(int i = 0; i < RGB_array.length; i++) {
			RGB_array[i] /= normalizor;	
		}	
	}

	/**
	 * Method to compute the euclidian distance between the sample measurements
	 * and the absolute measurement stored in Resources.
	 */
	public static double euclidian_distance(double[] RGB_array, String color) {
		double[] RING_RGB_NORM = RINGS_RGB_NORM.get(color.toUpperCase());

		double red_diff = RGB_array[0] - RING_RGB_NORM[0];
		double green_diff = RGB_array[1] - RING_RGB_NORM[1];
		double orange_diff = RGB_array[2] - RING_RGB_NORM[2];

		double eucl_dist =  sqrt(pow(red_diff,2) + pow(green_diff,2) + pow(orange_diff,2));
		return eucl_dist;
	}

	/**
	 * Constantly poll the sensor and sleep
	 */
	public void run() {
		while (active) {
			// the pollColorSensor method will update the color field
			pollColorSensor();
			// if the color is not "GROUND" stop.
			if (color != "GROUND") {
				stopAndDisplayColor();
			}
			Main.sleepFor(CS_POLL_SLEEP_TIME);
		}
	}

	/**
	 * Stop the controller by setting the active flag
	 */
	public void stopController() {
		active = false;
	}

	/**
	 * @return the average intensity of r g b components.
	 */
	public static double[] calibrateColorSensor() {
		double[] sum = new double[3];
		for (int i = 0; i < CALIBRATION_SAMPLE; i++) {
			colorSensor.fetchSample(colorSamples, 0);
			sum[0] += colorSamples[0] * 100.0;
			sum[1] += colorSamples[1] * 100.0;
			sum[2] += colorSamples[2] * 100.0;
			Main.sleepFor(CS_POLL_SLEEP_TIME);
		}
		sum[0] = sum[0] / CALIBRATION_SAMPLE;
		sum[1] = sum[1] / CALIBRATION_SAMPLE;
		sum[2] = sum[2] / CALIBRATION_SAMPLE;
		return sum;
	}
	

	/**
	 * check if the current reading a color.
	 * 
	 * @return isColor
	 */
	public static boolean isColor() {
	// array to store the RGB values read from the sensor
	  readColor();
      double[] RGB_array = {
              (double) colorSamples[0],
              (double) colorSamples[1],
              (double) colorSamples[2]
      };


      // normalize the rgb array
      normalize_RGB_array(RGB_array);

      double yellow_ring_dist = euclidian_distance(RGB_array, "YELLOW");
      double blue_ring_dist = euclidian_distance(RGB_array, "BLUE");
      double orange_ring_dist = euclidian_distance(RGB_array, "ORANGE");
      double green_ring_dist = euclidian_distance(RGB_array, "GREEN");


      double minimal_dist_temp1 = Math.min(yellow_ring_dist, blue_ring_dist);
      double minimal_dist_temp2 = Math.min(orange_ring_dist, green_ring_dist);
      minimal_dist = Math.min(minimal_dist_temp1, minimal_dist_temp2);

      // we have now found the minimal euclidian distance
      // find the color associated with the distance
      if(minimal_dist >= MIN_GROUND_EUCLID_DIST) {
          color = "GROUND";
          return false;
      }
      else if(minimal_dist == yellow_ring_dist) 
          color = "YELLOW";
      else if(minimal_dist == blue_ring_dist) 
          color = "BLUE";
      else if(minimal_dist == orange_ring_dist) 
          color = "ORANGE";
      else if(minimal_dist == green_ring_dist) 
          color = "GREEN";
      return true;
	}

	/**
	 * check if value is within the interval
	 * 
	 * @param value
	 * @param center
	 * @param offset
	 * @return value within interval
	 */
	private static boolean isWithinInterval(double value, double center, double offset) {
		return value >= center - offset && value <= center + offset;
	}
}
