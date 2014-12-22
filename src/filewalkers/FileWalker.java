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
	private WalkerThreadHandeler handler; // Reference to class keeping track of all active FileWalkers
	
	private boolean canceled; // If set to true stops the FileWalker at the beginning of its next action
	private boolean pauzed; // If set to true pauzes the FileWalker, resumes when set to false
	
	private Path start; // Path the FileWalker starts from
	private Path target; // Utility Path, for example can be copied to
	
	/**
	 * Invokes the walkFiles() method
	 */
	@Override
	public void run() {
		try {
			this.handler.register(this); 
			walkFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops the filewalker. It will stop at the start of it's next operation
	 */
	public void stop() {
		this.canceled = true;
	}
	
	/**
	 * Toggles pauze on the filewalker. It will pauze at the start of it's next operation
	 */
	public void pauze() {
		pauzed = pauzed ? false : true;
	}

	/**
	 * Iterates over all files and directories in the given volume until a stop condition is met
	 * or all files have been iterated over.
	 * @throws IOException
	 */
	private void walkFiles() throws IOException 
	{
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			
			/**
			 * Checks if pauzed, if so goes into a while loop untill pauzed is false
			 */
			private void checkPauzed() {
				while(pauzed) { 
					try{
						Thread.sleep(100); // Sleep thread to reduce cpu use
					} catch(Exception e) {
						// No action, will continue busy waiting
					}
				}
			}
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				if(canceled) return FileVisitResult.TERMINATE;
				checkPauzed();
				
				return onPreVisitDir(dir, attrs);
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				if(canceled) return FileVisitResult.TERMINATE;
				checkPauzed();
				
				return onPostVisitDir(dir, exc);
			}
			
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				if(canceled) return FileVisitResult.TERMINATE;
				checkPauzed();
				
				return onVisitFile(file, attrs);
			}
			
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				if(canceled) return FileVisitResult.TERMINATE;
				checkPauzed();
				
				return onVisitFileFailed(file, exc);
			}
		});
		
		this.postWalk();
	}
	
	/**
	 * Performed after walkFileTree finishes
	 */
	private void postWalk() {
		if(!canceled) {
			this.onComplete();
		} else {
			Controller.getInstance().outputLine("\n-- stopped --\n");
		}
		
		this.handler.remove(this); 
	}

	public Path getStart() {
		return this.start;
	}
	
	public Path getTarget() {
		return this.target;
	}
	
	public void setStart(Path start) {
		this.start = start;
	}
	
	public void setTarget(Path target) {
		this.target = target;
	}
	
	public void setHandler(WalkerThreadHandeler handler) {
		this.handler = handler;
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
