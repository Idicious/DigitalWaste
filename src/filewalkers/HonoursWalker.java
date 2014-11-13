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
import java.util.Iterator;
import java.util.Set;

/**
 * Filewalker that randomly chooses files to display.
 * @author dsherman
 *
 */
public class HonoursWalker extends FileWalker {
	
	private String targetPath;
	private MySQL db;
	
	private HashMap<String, ArrayList<String>> acceptedFormats = new HashMap<String, ArrayList<String>>();
	
	/**
	 * 
	 * @param start
	 * @param target path to where files will be copied.
	 */
	public HonoursWalker(Path start, String target) {
		super(start);
		
		this.targetPath = target;
		this.db = new MySQL();
		this.initializeAcceptedFormats();
		
	}

	@Override
	protected FileVisitResult onPreVisitDir(Path dir, BasicFileAttributes attrs) {
		// TODO Auto-generated method stub
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected FileVisitResult onPostVisitDir(Path dir, IOException exc) {
		// TODO Auto-generated method stub
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
	protected FileVisitResult onVisitFileFailed(Path file, IOException exc) {
		// TODO Auto-generated method stub
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected Object onComplete() {
		db.disconnect();
		getController().outputLine("Operation complete.");
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
	 * the key which is the category string. Else it it returns null.
	 * @param file
	 * @return key of acceptedFormats which is the category String for database.
	 */
	private String extensionAccepted(Path file) {
		String path = file.toString();
		Set<String> keys = acceptedFormats.keySet();
		
		for(String key : keys) {
			Iterator<String> it = acceptedFormats.get(key).iterator();
			
			while(it.hasNext()) {
				String ext = it.next();
				
				if(path.contains(ext)) {
					return key;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Adds all accepted file format extensions to the acceptedFormats array
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
		
		acceptedFormats.put("doc", docFormats);
	}

}
