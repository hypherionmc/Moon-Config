package me.hypherionmc.moonconfig.core.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

/**
 * A FileWatcher can watch several files asynchronously.
 * <p>
 * New watches are added with the {@link #addWatch(Path, Runnable)} method, which specifies the
 * task to execute when the file is modified.
 * <p>
 * This class is thread-safe.
 *
 * @author TheElectronWill
 */
public final class FileWatcher {
	private static final long SLEEP_TIME_NANOS = 1000;
	private static volatile FileWatcher DEFAULT_INSTANCE;
	private static final ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * Gets the default, global instance of FileWatcher.
	 *
	 * @return the default FileWatcher
	 */
	public static synchronized FileWatcher defaultInstance() {
		if (DEFAULT_INSTANCE == null || !DEFAULT_INSTANCE.run) { // null or stopped FileWatcher
			DEFAULT_INSTANCE = new FileWatcher();
		}
		return DEFAULT_INSTANCE;
	}

	private final Map<Path, WatchedDir> watchedDirs = new ConcurrentHashMap<>(); //dir -> watchService & infos
	private final Map<Path, WatchedFile> watchedFiles = new ConcurrentHashMap<>(); //file -> watchKey & handler
	private final Consumer<Exception> exceptionHandler;
	private final WatchService watchService;
	private volatile boolean run = true;

	/**
	 * Creates a new FileWatcher. The watcher is immediately functional, there is no need (and no
	 * way, actually) to start it manually.
	 */
	public FileWatcher() {
		this(Throwable::printStackTrace);
	}

	/**
	 * Creates a new FileWatcher. The watcher is immediately functional, there is no need (and no
	 * way, actually) to start it manually.
	 */
	public FileWatcher(Consumer<Exception> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
		try {
			this.watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		executor.execute(new WatcherThread());
	}

	/**
	 * Watches a file, if not already watched by this FileWatcher.
	 *
	 * @param file          the file to watch
	 * @param changeHandler the handler to call when the file is modified
	 */
	public void addWatch(File file, Runnable changeHandler) throws IOException {
		addWatch(file.toPath(), changeHandler);
	}

	/**
	 * Watches a file, if not already watched by this FileWatcher.
	 *
	 * @param file          the file to watch
	 * @param changeHandler the handler to call when the file is modified
	 */
	public void addWatch(Path file, Runnable changeHandler) throws IOException {
		file = file.toAbsolutePath();// Ensures that the Path is absolute
		Path dir = file.getParent();
		watchedDirs.computeIfAbsent(dir, k -> new WatchedDir(dir, watchService));
		watchedFiles.computeIfAbsent(file, k -> new WatchedFile(changeHandler));
	}

	/**
	 * Watches a file. If the file is already watched by this FileWatcher, its changeHandler is
	 * replaced.
	 *
	 * @param file          the file to watch
	 * @param changeHandler the handler to call when the file is modified
	 */
	public void setWatch(File file, Runnable changeHandler) throws IOException {
		setWatch(file.toPath(), changeHandler);
	}

	/**
	 * Watches a file. If the file is already watched by this FileWatcher, its changeHandler is
	 * replaced.
	 *
	 * @param file          the file to watch
	 * @param changeHandler the handler to call when the file is modified
	 */
	public void setWatch(Path file, Runnable changeHandler) throws IOException {
		file = file.toAbsolutePath();// Ensures that the Path is absolute
		WatchedFile watchedFile = watchedFiles.get(file);
		if (watchedFile == null) {
			addWatch(file, changeHandler);
		} else {
			watchedFile.changeHandler = changeHandler;
		}
	}

	/**
	 * Stops watching a file.
	 *
	 * @param file the file to stop watching
	 */
	public void removeWatch(File file) {
		removeWatch(file.toPath());
	}

	/**
	 * Stops watching a file.
	 *
	 * @param file the file to stop watching
	 */
	public void removeWatch(Path file) {
		file = file.toAbsolutePath(); // Ensures that the Path is absolute
		watchedFiles.remove(file);
	}

	/**
	 * Stops this FileWatcher. The underlying ressources (ie the WatchServices) are closed, and
	 * the file modification handlers won't be called anymore.
	 */
	public void stop() throws IOException {
		run = false;
		executor.shutdown();
		watchService.close();
		watchedDirs.clear();
		watchedFiles.clear();
	}

	private final class WatcherThread extends Thread {
		{
			setDaemon(true);
			setName("Config-Watcher");
		}

		@Override
		public void run() {
			while (run) {
				boolean allNull = true;

				try {
					WatchKey key = watchService.poll(25, TimeUnit.MILLISECONDS);
					if (key == null) continue;

					allNull = false;

					for (WatchEvent<?> event : key.pollEvents()) {
						if (!run) return;
						if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY && event.kind() != StandardWatchEventKinds.ENTRY_CREATE && event.kind() != StandardWatchEventKinds.ENTRY_DELETE) continue;

						@SuppressWarnings("unchecked")
						Path childPath = ((WatchEvent<Path>) event).context();
						Path filePath = ((Path) key.watchable()).resolve(childPath);
						WatchedFile watchedFile = watchedFiles.get(filePath);

						if (watchedFile != null) {
							try {
								watchedFile.changeHandler.run();
							} catch (Exception e) {
								exceptionHandler.accept(e);
							}
						}
					}
					key.reset();
					Thread.sleep(50);
				} catch (InterruptedException ignored) {}

				if (allNull)
					LockSupport.parkNanos(SLEEP_TIME_NANOS);
			}
		}
	}

	/**
	 * Informations about a watched directory, ie a directory that contains watched files.
	 */
	private static final class WatchedDir {
		final Path dir;

		private WatchedDir(Path dir, WatchService watchService) {
			this.dir = dir;
			try {
				dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Informations about a watched file, with an associated handler.
	 */
	private static final class WatchedFile {
		volatile Runnable changeHandler;

		private WatchedFile(Runnable changeHandler) {
			this.changeHandler = changeHandler;
		}
	}
}