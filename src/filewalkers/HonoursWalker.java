package filewalkers;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
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
	
	/**
	 * 
	 * @param start Start path
	 * @param chance Double between 0.0 and 1.0 indicating % of files you want. 0.01 equals 1%, 1.0 equals 100%
	 */
	public HonoursWalker(Path start, String target, double chance) {
		super(start);
		
		this.chance = chance;
		targetPath = new File("").getAbsolutePath() + target;
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

			try {
				Path target = new File(targetPath+file.toFile().getName()).toPath();
				Files.copy(file, target, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
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
