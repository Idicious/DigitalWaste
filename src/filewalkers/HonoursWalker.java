package filewalkers;

import helpsers.MySQL;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Copies files with extension contained in acceptedFormats HashMap to given Directory path. Then
 * saves the copied directory path and category to a MySQL database.
 * @author dsherman
 *
 */
public class HonoursWalker extends FileWalker {
	
	private String targetPath;
	private MySQL db;
	
	// HashMap containing accepted extensions with as Key category, Value array of extensions
	private HashMap<String, ArrayList<String>> acceptedFormats = new HashMap<String, ArrayList<String>>();
	
	/**
	 * Default constructor
	 * @param start
	 * @param target path to where files will be copied.
	 */
	public HonoursWalker(Path start, String target) {
		super(start);
		
		this.targetPath = target;
		this.db = new MySQL();
		
		this.initializeAcceptedFormats();
		getController().outputLine("\n-- starting --\n");
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
		if(category != null) {
			getController().outputLine("Copying " + file);
			try {
				Path target = new File(targetPath+file.toFile().getName()).toPath();
				Files.copy(file, target, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
				
				addToDB(target, category);
				getController().outputLine("Complete");
			} catch (IOException e) {
				getController().outputLine(e.getMessage());
			}
		}
		
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	protected FileVisitResult onVisitFileFailed(Path file, IOException e) {
		getController().outputLine(e.getMessage());
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected Object onComplete() {
		db.disconnect();
		getController().outputLine("\n-- operation complete --\n");
		return null;
	}
	
	/**
	 * Inserts path and category into the database.
	 * @param target
	 * @param category
	 */
	private void addToDB(Path target, String category) {
		String path = target.toString();
		
		String query = "INSERT INTO data (path, category) VALUES ('"+path+"', '"+category+"')";
		db.push(query);
	}
	
	/**
	 * Searches the acceptedFormat HashMap for the given Path's extension, if found it returns
	 * the key, else it returns null.
	 * @param file
	 * @return key of acceptedFormats which is the category String for database.
	 */
	private String extensionAccepted(Path file) {
		Set<String> keys = acceptedFormats.keySet();
		String path = file.toString();
		
		for(String key : keys) {
			for(String ext : acceptedFormats.get(key)) {
				if(path.endsWith(ext)) {
					return key;
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
		
		imageFormats.add(".jpg");
		imageFormats.add(".jpeg");
		imageFormats.add(".png");
		imageFormats.add(".bmp");
		imageFormats.add(".gif");
		
		acceptedFormats.put("image", imageFormats);
		
		// Audio formats
		ArrayList<String> audioFormats = new ArrayList<String>();
		
		audioFormats.add(".mp3");
		audioFormats.add(".wav");
		audioFormats.add(".mid");
		
		acceptedFormats.put("audio", audioFormats);
		
		// Video formats
		ArrayList<String> videoFormats = new ArrayList<String>();
		
		videoFormats.add(".mp4");
		
		acceptedFormats.put("video", videoFormats);
		
		// Document formats
		ArrayList<String> docFormats = new ArrayList<String>();
		
		docFormats.add(".txt");
		docFormats.add(".doc");
		docFormats.add(".pdf");
		
		acceptedFormats.put("doc", docFormats);
	}

}
