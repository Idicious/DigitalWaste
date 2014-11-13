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
import java.util.Random;

/**
 * Filewalker that randomly chooses files to display.
 * @author dsherman
 *
 */
public class HonoursWalker extends FileWalker {

	private Random rand = new Random();
	private double chance;
	
	private String targetPath;
	private MySQL db;
	
	private ArrayList<String> acceptedFormats = new ArrayList<>();
	
	/**
	 * 
	 * @param start Start path
	 * @param chance Double between 0.0 and 1.0 indicating % of files you want. 0.01 equals 1%, 1.0 equals 100%
	 */
	public HonoursWalker(Path start, String target, double chance) {
		super(start);
		
		this.chance = chance;
		this.targetPath = new File("").getAbsolutePath() + target;
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
		if(rand.nextDouble() < chance) {
			getController().outputLine("Copying " + file);
			try {
				Path target = new File(targetPath+file.toFile().getName()).toPath();
				Files.copy(file, target, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
				
				addToDB(target);
				getController().outputLine("Complete");
			} catch (IOException e) {
				getController().outputLine(e.getMessage());
			}
		}
		
		return FileVisitResult.CONTINUE;
	}

	private void addToDB(Path target) {
		String path = target.toString();
		String category = getCategory(path);
		
		String query = "INSERT INTO data (path, category) VALUES ("+path+","+category+")";
		db.push(query);
	}

	private String getCategory(String path) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void initializeAcceptedFormats() {
		
		// Image formats
		this.acceptedFormats.add(".jpg");
		this.acceptedFormats.add(".png");
		this.acceptedFormats.add(".bmp");
		
		// Audio formats
		this.acceptedFormats.add(".mp3");
		this.acceptedFormats.add(".wav");
		
		// Video formats
		this.acceptedFormats.add(".mp4");
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

}
