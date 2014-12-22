package filewalkers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class responsible for delegating requests to iterate over a volume.
 * Uses a fixed thread pool to dispatch requests to FileWalker objects.
 * 
 * Also contains methods to retrieve a list of all volumes in the system.
 * @author dsherman
 *
 */
public class WalkerThreadHandeler 
{
	public static final int MAX_THREADS = 4;
	
	private final ExecutorService pool; // Threadpool
	private final ArrayList<FileWalker> activeWalkers; // List of all active FileWalker objects
	
	public WalkerThreadHandeler() {
		this.pool = Executors.newFixedThreadPool(MAX_THREADS);
		this.activeWalkers = new ArrayList<FileWalker>();
	}
	
	/**
	 * Starts a given FileWalker on a new thread
	 * @param walker
	 */
	public void walk(FileWalker walker) {
		walker.setHandler(this);
		pool.execute(walker);
	}
	
	/**
	 * Stops given FileWalker
	 */
	public void stopWalker(FileWalker walker) {
		walker.stop();
	}
	
	/**
	 * Stops all active FileWalkers
	 */
	public void stopWalkers() {
		Iterator<FileWalker> it = activeWalkers.iterator();
		while(it.hasNext()) {
			stopWalker(it.next());
		}
	}
	
	/**
	 * pauzes given FileWalker
	 */
	public void pauzeWalker(FileWalker walker) {
		walker.pauze();
	}
	
	/**
	 * Pauzes all active FileWalkers
	 */
	public void pauzeWalkers() {
		Iterator<FileWalker> it = activeWalkers.iterator();
		while(it.hasNext()) {
			pauzeWalker(it.next());
		}
	}
	
	/**
	 * Adds a walker to the active walker list
	 * @return
	 */
	public synchronized void register(FileWalker walker) {
		activeWalkers.add(walker);
	}
	
	/**
	 * Adds a walker to the active walker list
	 * @return
	 */
	public synchronized void remove(FileWalker walker) {
		activeWalkers.remove(walker);
	}
}
