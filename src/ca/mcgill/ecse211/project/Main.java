package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;

import lejos.hardware.Button;

/**
 * This class is the starting point of our program, but is mainly used to instantiate
 * the main controller which implements the solution to the design problem by calling
 * in sequence the high-level software components of our solution. It also creates the 
 * initial displayed menu from where we can start the main controller.
 * 
 * @author charlesbourbeau
 *
 */
public class Main {

	/**
	 * The main entry point.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		int mode = 0;
		int option = Button.ID_DOWN;
		while (option != Button.ID_ENTER) {
			Display.printOption(modes[mode]);
			option = Button.waitForAnyPress();
			if (option == Button.ID_ESCAPE)
				Main.showErrorAndExit("Program canceled by user");
			else if (option == Button.ID_LEFT)
				mode = (mode + modes.length - 1) % modes.length;
			else if (option == Button.ID_RIGHT)
				mode = (mode + 1) % modes.length;
		}
		// Initialize the controller with the desire mode.
		// 0(main), 1(init local), 2(light local), 3(odometer cal), 4 (light cal),
		// 5(color cal), More mode coming.
		mainController = new MainController(mode);
		// Start the main thread to perform the action.
		Thread mainThread = new Thread(mainController);
		mainThread.start();

		// Any key press terminate the program
		while (option != Button.ID_ESCAPE) {
			option = Button.waitForAnyPress();
		}
		System.exit(0);
	}

	/**
	 * Shows error and exits program.
	 * @param errorMessage to display.
	 */
	public static void showErrorAndExit(String errorMessage) {
		TEXT_LCD.clear();
		System.err.println(errorMessage);

		// Sleep for 2s so user can read error message
		sleepFor(2000);

		System.exit(-1);
	}

	/**
	 * Sleeps for the specified duration.
	 * 
	 * @param millis the duration in milliseconds
	 */
	public static void sleepFor(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// Nothing to do here
		}
	}

}
