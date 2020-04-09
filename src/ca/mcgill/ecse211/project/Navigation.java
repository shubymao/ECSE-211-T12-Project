package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import java.util.ArrayList;

public class Navigation {

	/**
	 * The US controller thread to poll and provide information.
	 */
	private static Thread ultrasonicThread;

	/**
	 * Convert grid coordinate to actual coordinate (cm).
	 * 
	 * @param ind
	 * @return actual distance (cm)
	 */
	public static double gridToCord(int ind) {
		return ind * TILE_SIZE;
	}

	/**
	 * This function calls the a* algorithm in the PathFinder class multiple times 
	 * to visit all the grid coordinates of the search zone.
	 */
	public static void searchZoneTraversal() {
		
	}
	
	/**
	 * This functions calls the a* algorithm in the PathFinder class 3 times to go from the
	 * stranded cart discovery coordinate back to the starting corner while taking wider turns
	 * to account for the increased length of the robot with the cart.
	 * First to first travel to a buffered length away from the tunnel, secondly to traverse 
	 * the tunnel with a minimal exit distance from the tunnel to avoid the collision of the cart and 
	 * the tunnel exit, and thirdly to travel back to the starting corner.
	 * 
	 * @param pf 	the PathFinder instance with information about the Wi-Fi params
	 */
	public static void carryBackStrandedCart(PathFinder pf) {
		
	}
	
	/**
	 * This function performs a light sensor sweep above the stranded cart to figure out 
	 * where the colored dot is positioned. From that information we can infer the position
	 * of the hook.
	 */
	public static void coloredDotDetection() {
		
	}
	
	/**
	 * This function repositions the robot, after the colored dot detection, such that 
	 * the robot is now in the appropriate position to begin the hooking process. 
	 */
	public static void robotHookingPosition() {
		
	}

	/**
	 * Perform a operation to travel to a specified location based on the odometer. and user input
	 * 
	 * @param x
	 * @param y
	 */
	public static void travelTo(double x, double y, boolean blocking) {
		double[] xyt = odometer.getXyt();
		double curX = xyt[0];
		double curY = xyt[1];
		double deltaX = x - curX;
		double deltaY = y - curY;
		double angle = Math.toDegrees(Math.atan2(deltaX, deltaY));
		double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		turnTo(angle);
		setSpeed(FAST_SPEED);
		moveStraightFor(distance, blocking);
	}

	/**
	 * Rotate to the given angle base on the current odometer data
	 * 
	 * @param angle
	 */
	public static void turnTo(double angle) {
		double[] xyt = odometer.getXyt();
		double currentTheta = xyt[2];
		double angleToturn = angle - currentTheta;
		// get the minimum angle
		if (angleToturn > 180) {
			angleToturn = angleToturn - 360.0;
		} else if (angleToturn < -180.0) {
			angleToturn = 360.0 + angleToturn;
		}
		setSpeed(MEDIUM_SPEED);
		turnBy(angleToturn,true);
	}

	/**
	 * The coordinate is 0 based for easy manipulation of the 
	 * @return the current coordinate of the robot
	 */
	static short[] getCurrentCord() {
		double[] xyt = odometer.getXyt();
		short roundedX = (short)Math.round(xyt[0] / TILE_SIZE);
		short roundedY = (short)Math.round(xyt[1] / TILE_SIZE);
		return new short[] {roundedX,roundedY};
	}

	/**
	 * Round the position after a color sensor localization
	 */
	static void roundPosition() {
		double[] xyt = odometer.getXyt();
		double roundedTheta =
				(RIGHT_ANGLE_ROTATION * Math.round(xyt[2] / RIGHT_ANGLE_ROTATION)) % 360;
		double roundedX = (TILE_SIZE / 2) * Math.round(xyt[0] / (TILE_SIZE / 2));
		double roundedY = (TILE_SIZE / 2) * Math.round(xyt[1] / (TILE_SIZE / 2));
		odometer.setTheta(roundedTheta);
		if(roundedTheta == 90 || roundedTheta == 270)odometer.setX(roundedX);
		if(roundedTheta == 0 || roundedTheta == 180)odometer.setY(roundedY);
	}

	/**
	 * Localize using the light sensor
	 */
	public static void lightLocalization() {
		if(!UltraSonicController.isObstacle()) {
			LightSensorController.lineLocalization();
			moveStraightFor(-TILE_SIZE / 2 + 3.5, true);
			roundPosition();
		}
		turnBy(RIGHT_ANGLE_ROTATION, true);
		if(!UltraSonicController.isObstacle()) {
			LightSensorController.lineLocalization();
			moveStraightFor(-TILE_SIZE / 2 + 3.5, true);
			roundPosition();
		}
	}

	/**
	 * Light localization helper method for checking obstacle.
	 */
	private static void lightLocalizationHelper() {

	}

	/**
	 * The initial Localization. Uses ultrasonic detection twice and find the min and travel
	 * distance based on sensor data.
	 */
	public static void initialLocalization() {
		// Start the us controller
		ultraSonicController = new UltraSonicController();
		ultrasonicThread = new Thread(ultraSonicController);
		ultrasonicThread.start();
		// perform the following operation 2 time. Face the closest wall and backup
		// until a suitable
		// distance based on the us sensor
		faceTheClosestWall();
		backup();
		// Now the robot is in position with respect to one direction x or y.
		// perform turn to fix the position of the other direction.
		turnBy(RIGHT_ANGLE_ROTATION,true);
		if (ultraSonicController.getCurDist() < WALL_THRESHOLD) {
			minorAngleCorrection();
			backup();
			turnBy(RIGHT_ANGLE_ROTATION,true);
		} else {
			turnBy(-U_TURN_ROTATION,true);
			minorAngleCorrection();
			backup();
			turnBy(-U_TURN_ROTATION,true);
		}
		ultraSonicController.stopController();
	}

	/**
	 * Helper method to face towards the closest wall. Perform a 360 sweep to turn to the aggregate
	 * angle and a slow 60 degree sweep to turn get a accurate turn.
	 */
	private static void faceTheClosestWall() {
		// perform 360 turn and locate the approximate angle and use minor correction to
		// determine the
		// exact angle.
		setSpeed(MEDIUM_SPEED);
		ultraSonicController.reset();
		long initialTime = System.currentTimeMillis();
		turnBy(FULL_ROTATION, true);
		long totalTic = System.currentTimeMillis() - initialTime;
		long minTic = ultraSonicController.getAvgMinDistIndex() - initialTime;
		double angleToTurn = 0;
		// locate the angle away from the start position.
		angleToTurn = ((double) minTic) * FULL_ROTATION / totalTic;
		if (angleToTurn > 180.0) {
			angleToTurn = angleToTurn - 360.0;
		} // perform the turn
		turnBy(angleToTurn, true);
		minorAngleCorrection();
	}

	/**
	 * Assume start at an angle close to the reall value. Perform us localization and face toward
	 * the closest wall.
	 */
	private static void minorAngleCorrection() {
		// perform small turn and locate the average Min distance angle.
		turnBy(-1 * HALF_SMALL_ROTATION, true);
		setSpeed(EXTRA_SLOW_SPEED);
		ultraSonicController.reset();
		long initialTime = System.currentTimeMillis();
		turnBy(SMALL_ROTATION, true);
		long totalTic = System.currentTimeMillis() - initialTime;
		long minTic = ultraSonicController.getAvgMinDistIndex() - initialTime;
		double angleToTurn = 0;
		angleToTurn = (double) (minTic - totalTic) * SMALL_ROTATION / (totalTic);
		turnBy(angleToTurn, true);
	}

	/**
	 * Assume the robot faces a wall. backup until a distance of a tile distance.
	 */
	private static void backup() {
		ultraSonicController.reset();
		Main.sleepFor(DEFAULT_SLEEP_TIME);
		double curDist = ultraSonicController.getCurDist();
		setSpeed(MEDIUM_FAST_SPEED);
		moveStraightFor(curDist + US_SENSOR_CENTER_OFFSET - TILE_SIZE/2, true);
	}

	/**
	 * Sets the speed of both motors to the same values.
	 * 
	 * @param speed the speed in degrees per second
	 */
	public static void setSpeed(int speed) {
		setSpeeds(speed, speed);
	}

	/**
	 * Sets the speed of both motors to different values.
	 * 
	 * @param leftSpeed the speed of the left motor in degrees per second
	 * @param rightSpeed the speed of the right motor in degrees per second
	 */
	public static void setSpeeds(int leftSpeed, int rightSpeed) {
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
	}

	/**
	 * Move the robot forward according to the speed set.
	 */
	public static void forward() {
		leftMotor.forward();
		rightMotor.forward();
	}

	/**
	 * Moves the robot straight for the given distance.
	 * 
	 * @param distance in feet (tile sizes), may be negative
	 */
	public static void moveStraightFor(double distance, boolean blocking) {
		leftMotor.rotate(convertDistance(distance), true);
		rightMotor.rotate(convertDistance(distance), !blocking);
	}

	/**
	 * Turns the robot by a specified angle. Positive is clockwise.
	 * 
	 * @param angle the angle by which to turn, in degrees
	 */
	public static void turnBy(double angle, boolean blocking) {
		leftMotor.rotate(convertAngle(angle), true);
		rightMotor.rotate(-convertAngle(angle), !blocking);
	}

	/**
	 * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
	 * angle.
	 * 
	 * @param angle the input angle
	 * @return the wheel rotations necessary to rotate the robot by the angle
	 */
	public static int convertAngle(double angle) {
		return convertDistance(Math.PI * BASE_WIDTH * angle / 360.0);
	}

	/**
	 * Converts input distance to the total rotation of each wheel needed to cover that distance.
	 * 
	 * @param distance the input distance
	 * @return the wheel rotations necessary to cover the distance
	 */
	public static int convertDistance(double distance) {
		return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
	}

	/**
	 * Stops both motors.
	 */
	public static void stopMotors() {
		leftMotor.stop();
		rightMotor.stop();
	}
}
