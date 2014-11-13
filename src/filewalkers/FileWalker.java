package filewalkers;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import controller.Controller;

/**
 * Abstract class that when implemented describes what to do with with files and
 * directories when iterating over them. Implements Runnable so instances of subclasses
 * can run on their own thread. This is important since it can take a considerable amount 
 * of time to complete the operation.
 * @author dsherman
 *
 */
public abstract class FileWalker implements Runnable
{
	private Path start; 
	
	/**
	 * Protected constructor invoked by subclasses
	 * @param start Directory to start walking from
	 */
	protected FileWalker(Path start) {
		this.start = start;
	}
	
	/**
	 * Returns the controller instance, controller is used for interacting with the GUI.
	 * @return
	 */
	protected Controller getController()
	{
		return Controller.getInstance();
	}
	
	/**
	 * Invokes the walkFiles() method
	 */
	@Override
	public void run() {
		try {
			walkFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Iterates over all files and directories in the given volume until a stop condition is met
	 * or all files have been iterated over.
	 * @throws IOException
	 */
	private void walkFiles() throws IOException 
	{
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				return onPreVisitDir(dir, attrs);
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				return onPostVisitDir(dir, exc);
			}
			
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				return onVisitFile(file, attrs);
			}
			
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				return onVisitFileFailed(file, exc);
			}
		});
		
		this.onComplete();
	}
	
	/**
	 * Action to take before visiting a directory
	 * @param dir
	 * @param attrs
	 * @return
	 */
	protected abstract FileVisitResult onPreVisitDir(Path dir, BasicFileAttributes attrs);
	
	/**
	 * Action to take after visiting a directory
	 * @param dir
	 * @param exc
	 * @return
	 */
	protected abstract FileVisitResult onPostVisitDir(Path dir, IOException exc);
	
	/**
	 * Action to take on visiting a file
	 * @param file
	 * @param attrs
	 * @return
	 */
	protected abstract FileVisitResult onVisitFile(Path file, BasicFileAttributes attrs);
	
	/**
	 * Action to take if onFileVisit fails
	 * @param file
	 * @param exc
	 * @return
	 */
	protected abstract FileVisitResult onVisitFileFailed(Path file, IOException exc);
	
	/**
	 * Method to be executed after filewalker is complete
	 */
	protected abstract Object onComplete();
}
