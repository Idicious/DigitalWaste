package filewalkers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;

/**
 * Filewalker that randomly chooses files to display.
 * @author dsherman
 *
 */
public class RandomWalker extends FileWalker {

	private Random rand = new Random();
	private double chance;
	
	private String targetPath = new File("").getAbsolutePath() + "/tmp/";
	
	/**
	 * 
	 * @param start Start path
	 * @param chance Double between 0.0 and 1.0 indicating % of files you want. 0.01 equals 1%, 1.0 equals 100%
	 */
	public RandomWalker(Path start, double chance) {
		super(start);
		
		this.chance = chance;
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
			getController().outputLine("Copying "+file);
			try {
				Files.copy(file, new File(targetPath+file.toFile().getName()).toPath());
			} catch (IOException e) {
				getController().outputLine(e.getMessage());
			}
			getController().outputLine("Complete");
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
		getController().outputLine("Operation complete.");
		return null;
	}

}
