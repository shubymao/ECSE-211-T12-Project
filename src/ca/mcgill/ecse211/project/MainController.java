package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import lejos.hardware.Sound;


public class MainController implements Runnable {

  /**
   * Option for selecting mode.
   */
  private int option;

  /**
   * Option for light constant localization. True == brown board, False == Blue board.
   */
  private boolean red = false;

  /**
   * Display thread for stopping or starting the thread.
   */
  private static Thread display;

  /**
   * Records of all the maps given from the lab document.
   */
  private int[][][] paths =
      {{{1, 7}, {3, 4}, {7, 7}, {7, 4}, {4, 1}}, {{5, 4}, {1, 7}, {7, 7}, {7, 4}, {4, 1}},
          {{3, 1}, {7, 4}, {7, 7}, {1, 7}, {1, 4}}, {{1, 4}, {3, 7}, {3, 1}, {7, 4}, {7, 7}},
          {{1, 2}, {2, 3}, {2, 1}, {3, 2}, {3, 3}}, {{1, 3}, {2, 2}, {3, 3}, {3, 2}, {2, 1}}};

  /**
   * Constructor for the class. Set the option entered by the user.
   * 
   * @param option for setup
   */
  public MainController(int option) {
    this.option = option;
  }

  /**
   * Run the corresponding mode based on the option.
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    if (this.option == 0) {
      main();
    } else if (this.option == 1) {
      initialLocalization();
    } else if (this.option == 2) {
      lightLocalization();
    } else if (this.option == 3) {
      calibrateAngle();
    } else if (this.option == 4) {
      calibrateDistance();
    } else if (this.option == 5) {
      calibrateColorSensor();
    } else if (this.option == 6) {
      calibrateLightSensor();
    } else if (this.option == 7) {
        calibrateUSSensor();
    }

  }

  /**
   * For testing the initial localization.
   */
  private void initialLocalization() {
    Navigation.initialLocalization();
  }

  /**
   * Mode for testing the light localization.
   */
  private void lightLocalization() {
    new Thread(odometer).start();
    odometer.setXyt(TILE_SIZE/2, TILE_SIZE/2, 0);
    startDisplay();
    Navigation.lightLocalization();
  }

  /**
   * Primary mode for lab
   */
  private void main() {
  }

  /**
   * Wheel angle and odometer calibration mode. Perform 2 full rotation turn and test the accuracy
   * of the turn.
   */
  private void calibrateAngle() {
    new Thread(odometer).start();
    odometer.setXyt(15.0, 15.0, 0);
    new Thread(new Display()).start();
    Main.sleepFor(DEFAULT_SLEEP_TIME);
    Navigation.setSpeed(MEDIUM_SPEED);
    Navigation.turnBy(2 * FULL_ROTATION, true);
  }

  /**
   * Wheel angle and odometer calibration mode. Perform 2 full rotation turn and test the accuracy
   * of the turn.
   */
  private void calibrateDistance() {
    new Thread(odometer).start();
    odometer.setXyt(30.0, 30.0, 0);
    new Thread(new Display()).start();
    Main.sleepFor(DEFAULT_SLEEP_TIME);
    Navigation.setSpeed(FAST_SPEED);
    Navigation.moveStraightFor(TILE_SIZE * 6, true);
  }

  /**
   * Calibrate the light sensor in the back to get the average value.
   */
  private void calibrateUSSensor() {
      while(true) {
          int dist = UltraSonicController.GetCurrentDistance();
          System.out.println("Dist: "+dist);
          Main.sleepFor(POLL_SLEEP_TIME);
          
      }
  }
  
  /**
   * Calibrate the light sensor in the back to get the average value.
   */
  private void calibrateLightSensor() {
	  while(true) {
	      float left = LightSensorController.readLeftLightSensor();
	      float right = LightSensorController.readRightLightSensor();
	      System.out.println("left: "+left+"right: "+right);
	      Main.sleepFor(CS_POLL_SLEEP_TIME);
	      
	  }
  }

  /**
   * Calibrate the color sensor in the back to get the average value.
   */
  private void calibrateColorSensor() {
    double[] averageIntensity = ColorDetectionController.calibrateColorSensor();
    Display.showText(new String[] {"Avg R:", "" + averageIntensity[0], "Avg G:",
        "" + averageIntensity[1], "Avg B:", "" + averageIntensity[2]});
    Main.sleepFor(4000); // sleep for 4 second for user to see and record value;
    System.out.println("Avg R:" + averageIntensity[0] + "\n Avg G:" + averageIntensity[1]
        + "\n Avg B: " + averageIntensity[2]);
  }

  /**
   * Start the display thread
   */
  static void startDisplay() {
    displayController = new Display();
    display = new Thread(displayController);
    display.start();
  }

  /**
   * Stop the display thread
   */
  static void stopDisplay() {
    displayController.stopDisplay();
  }
}
