package filewalkers;

import helpers.MySQL;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controller.Controller;
import errors.Error;
import errors.PathError;

/**
 * Copies files with extension contained in acceptedFormats HashMap to given
 * Directory path. Then saves the copied directory path and category to a MySQL
 * database.
 * 
 * @author dsherman
 *
 */
public class HonoursWalker extends FileWalker {

	private MySQL db;

	// HashMap containing accepted extensions with as Key category, Value array
	// of extensions
	private HashMap<String, ArrayList<String>> acceptedFormats = new HashMap<String, ArrayList<String>>();

	/**
	 * Default constructor
	 * 
	 * @param start
	 * @param target path to where files will be copied.
	 */
	public HonoursWalker(Path start, Path target) {
		setStart(start);
		setTarget(target);
	}

	@Override
	protected FileVisitResult onPreVisitDir(Path dir, BasicFileAttributes attrs) {
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected FileVisitResult onPostVisitDir(Path dir, IOException exc) {
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected FileVisitResult onVisitFile(Path file, BasicFileAttributes attrs) {
		String category = this.extensionAccepted(file);
		if (category != null) {
			Path target = new File(getTarget() + File.separator + file.toFile().getName()).toPath();
			Controller.getInstance().outputLine("Copying " + target);
			try {
				Files.copy(file, target); // Copies file to specified Path
				
				this.addToDB(target, category);
				Controller.getInstance().outputLine("Complete");
			} catch (IOException e) {
				Controller.getInstance().outputLine("\n-- copy failed --\n");
			}
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	protected FileVisitResult onVisitFileFailed(Path file, IOException e) {
		Controller.getInstance().outputLine(e.getStackTrace().toString());
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected Object onComplete() {
		db.disconnect();
		Controller.getInstance().outputLine("\n-- operation complete --\n");
		return null;
	}

	/**
	 * Inserts path and category into the database.
	 * 
	 * @param target
	 * @param category
	 */
	private void addToDB(Path target, String category) {
		String path = target.toString();
		path = path.replace("\\","\\\\"); // Escapes backslashes for windows paths;

		String query = "INSERT INTO data (path, category) VALUES ('" + path
				+ "', '" + category + "')";
		db.push(query);
	}

	/**
	 * Searches the acceptedFormat HashMap for the given Path's extension, if
	 * found it returns the key, else it returns null.
	 * 
	 * @param file
	 * @return key of acceptedFormats which is the category String for database.
	 */
	private String extensionAccepted(Path file) {
		String path = file.toString().toLowerCase();

		for (Map.Entry<String, ArrayList<String>> entry : acceptedFormats.entrySet()) {
			for (String ext : entry.getValue()) {
				if (path.endsWith(ext.toLowerCase())) {
					return entry.getKey();
				}
			}
		}

		return null;
	}

	/**
	 * Adds all accepted file format extensions to the acceptedFormats HashMap
	 */
	private void initializeAcceptedFormats() {
		// Image formats
		ArrayList<String> imageFormats = new ArrayList<String>();
		acceptedFormats.put("image", imageFormats);

		imageFormats.add(".jpg");
		imageFormats.add(".jpeg");
		imageFormats.add(".png");
		imageFormats.add(".bmp");
		imageFormats.add(".gif");

		// Audio formats
		ArrayList<String> audioFormats = new ArrayList<String>();
		acceptedFormats.put("audio", audioFormats);

		audioFormats.add(".mp3");
		audioFormats.add(".wav");
		audioFormats.add(".mid");

		// Video formats
		ArrayList<String> videoFormats = new ArrayList<String>();
		acceptedFormats.put("video", videoFormats);

		videoFormats.add(".mp4");

		// Document formats
		ArrayList<String> docFormats = new ArrayList<String>();
		acceptedFormats.put("doc", docFormats);

		docFormats.add(".txt");
		docFormats.add(".doc");
		docFormats.add(".pdf");
	}

	@Override
	protected ArrayList<Error> isValid() {
		ArrayList<Error> errors = new ArrayList<Error>();
		
		if(getStart() == null) errors.add(new PathError("You must select a start"));
		else if(!getStart().toFile().exists()) errors.add(new PathError("Start path does not exist")) ;
		
		if(getTarget() == null) errors.add(new PathError("You must select a target"));
		else if(!getTarget().toFile().exists()) errors.add(new PathError("Target path does not exist"));
		
		return errors;
	}

	@Override
	protected Object onStart() {
		this.db = new MySQL();
		this.initializeAcceptedFormats();
		Controller.getInstance().outputLine("\n-- starting --\n");
		return null;
	}

}
