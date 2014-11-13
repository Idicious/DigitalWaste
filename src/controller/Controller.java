package controller;

import java.io.File;
import java.nio.file.Path;

import filewalkers.*;
import gui.*;

/**
 * Class which passes messages between the GUI and Model classes. Eventlisteners in the 
 * GUI classes call methods in this class. Controller then calls the appropriate method(s)
 * in WalkerThreadHandeler.
 * FileWalkers can output information to the GUI via the outputLine method.
 * Uses singleton pattern, instance of this class is gotten through the getInstance() method.
 * 
 * @author Idicious
 */
public class Controller {
	
	private static boolean initialized = false;
	
	private static GUI thisGui;
	private static WalkerThreadHandeler thisWalker;
	private static Controller controller;
	
	/**
	 * Private constructor to enforce singleton patern
	 */
	private Controller() { }
	
	/**
	 * Returns instance of the Controller
	 * @return
	 */
	public static Controller getInstance() 
	{
		if(initialized) {
			if(controller == null) {
				controller = new Controller();
			}
			
			return controller;
		} else {
			throw new IllegalStateException("Not yet initialized.");
		}
	}
	
	/**
	 * Iterates from the given Path downwards.
	 * @param path
	 */
	public void choosePath(Path path)
	{
		thisWalker.walk(new HonoursWalker(path, new File("").getAbsolutePath()+"/tmp/"));
	}
	
	/**
	 * Initializes the controller
	 * @param gui
	 * @param walker
	 */
	public static void initialize(GUI gui, WalkerThreadHandeler walker)
	{	
		if(gui != null && walker != null)
		{
			thisGui = gui;
			thisWalker = walker;
			
			initialized = true;
		} else {
			throw new IllegalStateException("Null values not allowed.");
		}
	}

	/**
	 * Outputs a string to the text display of the application, everything output with this
	 * method is appended with \n
	 * @param string
	 */
	public void outputLine(String string) {
		thisGui.displayLine(string, true);
	}
}
