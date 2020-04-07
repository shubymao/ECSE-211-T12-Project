package ca.mcgill.ecse211.project;
import static ca.mcgill.ecse211.project.Resources.*;

/**
 * This class contains the method controlling the movement of the hook by acting on the 
 * EV3 motor attached to it. 
 * 
 * @author charlesbourbeau
 *
 */
public class HookController {
	
	/**
	 * Boolean that keeps track of the hook's state. Either hooked or not hooked.
	 */
	public static boolean isHooked = false;
	
	/**
	 * Function to lower the hook by rotating the hook motor by 90 degrees.
	 * @return if the cart is hooked
	 */
	public static boolean hookCart() {
		if(isHooked) {
			return isHooked;			// return immediately if the cart is already hooked
		}
		
		// now we lower the hook by 90 degrees
		hookMotor.setSpeed(HOOK_ROTATION_SPEED);
		hookMotor.rotate(HOOK_ROTATION_DEGREES);
	
		isHooked = true;
		return isHooked;
	}
	
	/**
	 * Function that elevated the hook by rotatining the hook by -90 degrees. 
	 * @return if the cart is hooked
	 */
	public static boolean unhookCart() {
		if(!isHooked) {
			return isHooked;			// return immediately if the cart is not hooked
		}
		
		// now we elevate the hook by 90 degrees
		hookMotor.setSpeed(HOOK_ROTATION_SPEED);
		hookMotor.rotate(-HOOK_ROTATION_DEGREES);
		
		isHooked = false;
		return isHooked;
	}
	
	
}
