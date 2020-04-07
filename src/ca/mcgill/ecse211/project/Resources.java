package ca.mcgill.ecse211.project;

import java.math.BigDecimal;
import java.util.Map;
import ca.mcgill.ecse211.playingfield.Point;
import ca.mcgill.ecse211.playingfield.Region;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

/**
 * This class is used to define static resources in one place for easy access and to avoid
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>
 * {@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 */
public class Resources {

    // Controller classes

    /**
     * The us sensor controller class used as thread.
     */
    public static UltraSonicController ultraSonicController;

    /**
     * The odometer class for getting the location and angle data.
     */
    public static Odometer odometer = Odometer.getOdometer();

    /**
     * The color controller for detecting color.
     */
    public static ColorDetectionController colorController;

    /**
     * The main controller used for choosing mode.
     */
    public static MainController mainController;

    /**
     * The main display controller
     */
    public static Display displayController;

    // Constants starts
    // All speed threshold start here.

    /**
     * The fast speed at which the robot moves in degrees per second.
     */
    public static final int FAST_SPEED = 200;

    /**
     * The medium fast speed which the robot moves in degrees per second.
     */
    public static final int MEDIUM_FAST_SPEED = 175;

    /**
     * The medium speed at which the robot moves in degrees per second.
     */
    public static final int MEDIUM_SPEED = 150;

    /**
     * The medium slow speed at which the robot moves in degrees per second.
     */
    public static final int MEDIUM_SLOW_SPEED = 125;

    /**
     * The slow speed at which the robot moves in degrees per second.
     */
    public static final int SLOW_SPEED = 100;

    /**
     * The slow speed at which the robot moves in degrees per second.
     */
    public static final int EXTRA_SLOW_SPEED = 75;
    
    /**
     * The speed at which the hook rotates.
     */
    public static final int HOOK_ROTATION_SPEED = 30;

    // All angle threshold start here.

    /**
     * Angle to do a full rotation.
     */
    public static final double FULL_ROTATION = 360.0;

    /**
     * U-Turn angle rotation for secondary rotation.
     */
    public static final double U_TURN_ROTATION = 180.0;

    /**
     * Right angle rotation for secondary rotation.
     */
    public static final double RIGHT_ANGLE_ROTATION = 90.0;

    /**
     * Small rotation for sweeping us data.
     */
    public static final double SMALL_ROTATION = 60.0;

    /**
     * Half small rotation for sweeping us data.
     */
    public static final double HALF_SMALL_ROTATION = 30.0;
    
    /**
     * Half small rotation for sweeping us data.
     */
    public static final double HALF_HALF_SMALL_ROTATION = 15.0;
    
    /**
     * the number of degrees the hooks needs to rotate when hooking and unhooking.
     */
    public static final int HOOK_ROTATION_DEGREES = 90;

    // All Distance threshold starts here

    /**
     * The wheel radius in centimeters.
     */
    public static final double WHEEL_RAD = 2.130;

    /**
     * The robot width in centimeters.
     */
    public static final double BASE_WIDTH = 11.5; // Increase if under turning and decrease if over turning

    /**
     * The offset between ultra sonic sensor and the wheel center of the robot width in centimeters.
     */
    public static final double US_SENSOR_CENTER_OFFSET = 9;

    /**
     * Obstacle threshold distance in cm.
     */
    public static final int OBSTACLE_THRESHOLD = 20;

    /**
     * The tile size in centimeters. Note that 30.48 cm = 1 ft.
     */
    public static final double TILE_SIZE = 30.48;// 30.48

    /**
     * The distance threshold for identifying if the sensor is facing a wall.
     */
    public static final double WALL_THRESHOLD = 50.0;

    /**
     * The offset between color sensor and the wheel center of the robot width in centimeters.
     */
    public static final double COLOR_SENSOR_CENTER_OFFSET = 13.5;


    // All polling period in ms
    /**
     * The poll sleep time, in milliseconds.
     */
    public static final int POLL_SLEEP_TIME = 30;

    /**
     * The color sensor poll sleep time, in milliseconds.
     */
    public static final int CS_POLL_SLEEP_TIME = 25;

    /**
     * The odometer sleep time in ms.
     */
    public static final long ODOMETER_PERIOD = 30;

    /**
     * The color sensor poll sleep time, in milliseconds.
     */
    public static final int CS_LINE_DELAY = 120;
    
    /**
     * The default sleep time to reset, in milliseconds.
     */
    public static final int DEFAULT_SLEEP_TIME = 1000;

    // Color constants
    /**
     * The normalized RGB of the yellow ring over 25 samples
     */
    public static final double[] YELLOW_RING_RGB_NORM = {0.863069756, 0.493870018, 0.105844231};

    /**
     * The normalized RGB of the blue ring over 25 samples
     */
    public static final double[] BLUE_RING_RGB_NORM = {0.205954751, 0.719722311, 0.663010133};

    /**
     * The normalized RGB of the orange ring over 25 samples
     */
    public static final double[] ORANGE_RING_RGB_NORM = {0.957472214, 0.276448074, 0.082604006};

    /**
     * The normalized RGB of the green ring over 25 samples
     */
    public static final double[] GREEN_RING_RGB_NORM = {0.465080244, 0.876811203, 0.122075719};

    /**
     * Minimal distance considered as the ground when measuring RGB from the rings. So any computed
     * euclidian distance above that is considered to be the ground and not a ring.
     */
    public static final double MIN_GROUND_EUCLID_DIST = 0.08;

    // Other constants.

    /**
     * Number of sample to perform the calibration.
     */
    public static int CALIBRATION_SAMPLE = 25;

    /**
     * The threshold in which a delta is considered a floor to line delta.
     */
    public static final float LINE_DELTA_THRESHOLD = -10;

    /**
     * The limit of invalid samples that we read from the US sensor before assuming no obstacle.
     */
    public static final int INVALID_SAMPLE_LIMIT = 20;

    /**
     * GREEN Start position and angle after localizing.
     */
    public static final short[] GREEN_START = {14, 1, -90};

    /**
     * GREEN Start position and angle after localizing.
     */
    public static final short[] RED_START = {1, 8, 90};

    /**
     * Mode for selecting operation in the main menu.
     */
    public static final String[] modes = {"Main Mode", "Initial LocalTest", "Color Local Test",
            "Angle Test", "Distance Test", "Front CS Test", "Back CS Test", "US Sensor Test"};

    // Hardware resources

    /**
     * The LCD screen used for displaying text.
     */
    public static final TextLCD TEXT_LCD = LocalEV3.get().getTextLCD();

    /**
     * The ultrasonic sensor hardware class.
     */
    public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);

    /**
     * The light sensor hardware class.
     */

    public static final EV3ColorSensor leftLightSensor = new EV3ColorSensor(SensorPort.S2);

    /**
     * The right light sensor hardware class.
     */
    public static final EV3ColorSensor rightLightSensor = new EV3ColorSensor(SensorPort.S3);
    /**
     * The color sensor to detect rings.
     */
    public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);

    /**
     * Sample provider instance associated with the RGB mode of the color sensor.
     */
    public static final SampleProvider myColorSample = colorSensor.getRGBMode();

    /**
     * The left motor hardware class.
     */
    public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);

    /**
     * The right motor hardware class.
     */
    public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);

    /**
     * The left motor hardware class.
     */
    public static final EV3LargeRegulatedMotor hookMotor = new EV3LargeRegulatedMotor(MotorPort.D);
    
    /**
     * The default server IP used by the profs and TA's.
     */
    public static final String DEFAULT_SERVER_IP = "192.168.2.3";
    
    /**
     * The IP address of the server that transmits data to the robot. For the beta demo and
     * competition, replace this line with
     * 
     * <p>{@code public static final String SERVER_IP = DEFAULT_SERVER_IP;}
     */
    public static final String SERVER_IP = "192.168.2.3"; // = DEFAULT_SERVER_IP;
    
    /**
     * Your team number.
     */
    public static final int TEAM_NUMBER = 12;
    
    /** 
     * Enables printing of debug info from the WiFi class. 
     */
    public static final boolean ENABLE_DEBUG_WIFI_PRINT = true;
    
    /**
     * Enable this to attempt to receive Wi-Fi parameters at the start of the program.
     */
    public static final boolean RECEIVE_WIFI_PARAMS = true;
    
    /**
     * Container for the Wi-Fi parameters.
     */
    public static Map<String, Object> wifiParameters;
    
    // This static initializer MUST be declared before any Wi-Fi parameters.
    static {
      receiveWifiParameters();
    }
    
    /** Red team number. */
    public static int redTeam = getWP("RedTeam");

    /** Red team's starting corner. */
    public static int redCorner = getWP("RedCorner");

    /** Green team number. */
    public static int greenTeam = getWP("GreenTeam");

    /** Green team's starting corner. */
    public static int greenCorner = getWP("GreenCorner");

    /** The Red Zone. */
    public static Region red = makeRegion("Red");

    /** The Green Zone. */
    public static Region green = makeRegion("Green");

    /** The Island. */
    public static Region island = makeRegion("Island");

    /** The red tunnel footprint. */
    public static Region tnr = makeRegion("TNR");

    /** The green tunnel footprint. */
    public static Region tng = makeRegion("TNG");

    /** The red search zone. */
    public static Region szr = makeRegion("SZR");

    /** The green search zone. */
    public static Region szg = makeRegion("SZG");
    
    /**
     * Receives Wi-Fi parameters from the server program.
     */
    public static void receiveWifiParameters() {
      // Only initialize the parameters if needed
      if (!RECEIVE_WIFI_PARAMS || wifiParameters != null) {
        return;
      }
      System.out.println("Waiting to receive Wi-Fi parameters.");

      // Connect to server and get the data, catching any errors that might occur
      try (WifiConnection conn =
          new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT)) {
        /*
         * getData() will connect to the server and wait until the user/TA presses the "Start" button
         * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
         * pressing the back/escape button on the EV3. getData() will throw exceptions if something
         * goes wrong.
         */
        wifiParameters = conn.getData();
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
    
    /**
     * Returns the Wi-Fi parameter int value associated with the given key.
     * 
     * @param key the Wi-Fi parameter key
     * @return the Wi-Fi parameter int value associated with the given key
     */
    public static int getWP(String key) {
      if (wifiParameters != null) {
        return ((BigDecimal) wifiParameters.get(key)).intValue();
      } else {
        return 0;
      }
    }
    
    /** 
     * Makes a point given a Wi-Fi parameter prefix.
     */
    public static Point makePoint(String paramPrefix) {
      return new Point(getWP(paramPrefix + "_x"), getWP(paramPrefix + "_y"));
    }
    
    /**
     * Makes a region given a Wi-Fi parameter prefix.
     */
    public static Region makeRegion(String paramPrefix) {
      return new Region(makePoint(paramPrefix + "_LL"), makePoint(paramPrefix + "_UR"));
    }

}
