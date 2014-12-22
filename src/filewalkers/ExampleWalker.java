package filewalkers;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import controller.Controller;

/**
 * Basic implementation of FileWalker that simply prints to the console all names of 
 * files and directories it reads as well as how many files and directories it's walked.
 * @author dsherman
 *
 */
public class ExampleWalker extends FileWalker {

	private int counter = 0;
	
	public ExampleWalker(Path start) {
		setStart(start);
		
		Controller.getInstance().outputLine("\n-- starting --\n");
	}

	@Override
	protected FileVisitResult onPreVisitDir(Path dir, BasicFileAttributes attrs) {
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected FileVisitResult onPostVisitDir(Path dir, IOException exc) {
		Controller.getInstance().outputLine(++counter + ": " + dir);
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected FileVisitResult onVisitFile(Path file, BasicFileAttributes attrs) {
		Controller.getInstance().outputLine(++counter + ": " + file);
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected FileVisitResult onVisitFileFailed(Path file, IOException exc) {
		Controller.getInstance().outputLine(exc.getMessage());
		return FileVisitResult.CONTINUE;
	}

	@Override
	protected Object onComplete() {
		Controller.getInstance().outputLine("\n --operation complete-- \n");
		return null;
	}

}
