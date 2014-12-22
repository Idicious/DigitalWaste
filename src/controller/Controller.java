package controller;

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
	
	protected static GUI thisGui;
	protected static WalkerThreadHandeler thisWalker;
	private static Controller controller;
	
	private Path startPath;
	private Path targetPath;
	
	/**
	 * Private constructor to enforce singleton patern
	 */
	protected Controller() { }
	
	/**
	 * Returns instance of the Controller
	 * @return
	 */
	public static synchronized Controller getInstance() 
	{
		if(initialized) {
			if(controller == null) {
				controller = new HonoursController();
			}
			
			return controller;
		} else {
			throw new IllegalStateException("Not yet initialized.");
		}
	}
	
	/**
	 * Starts the walker
	 */
	public void start() throws InvalidPathException 
	{
		this.validate();
		thisWalker.walk(new HonoursWalker(startPath, targetPath));
	}
	
	protected void validate() throws InvalidPathException
	{
		if(startPath == null) throw new InvalidPathException("You must select a start location");
		if(!startPath.toFile().exists()) throw new InvalidPathException("Start path does not exist");
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
			throw new IllegalStateException("Both GUI and Walker handler must be set");
		}
	}
	
	public Path getStart() {
		return this.startPath;
	}
	
	public Path getTarget() {
		return this.targetPath;
	}
	
	public void setStart(Path p) {
		this.startPath = p;
	}
	
	public void setTarget(Path p) {
		this.targetPath = p;
	}

	/**
	 * Outputs a string to the text display of the application, everything output with this
	 * method is appended with \n
	 * @param string
	 */
	public void outputLine(String string) {
		thisGui.displayLine(string, true);
	}

	/**
	 * Stops what the application is doing
	 */
	public void stop() {
		thisWalker.stopWalkers();
	}
}
