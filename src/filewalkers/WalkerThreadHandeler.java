package filewalkers;

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
	private ExecutorService pool;
	
	public WalkerThreadHandeler() {
		this.pool = Executors.newFixedThreadPool(MAX_THREADS);
	}
	
	/**
	 * Starts a given FileWalker on a new thread
	 * @param walker
	 */
	public void walk(FileWalker walker) {
		pool.execute(walker);
	}
}
