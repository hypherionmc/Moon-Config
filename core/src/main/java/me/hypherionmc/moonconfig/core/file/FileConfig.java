package me.hypherionmc.moonconfig.core.file;

import me.hypherionmc.moonconfig.core.Config;
import me.hypherionmc.moonconfig.core.ConfigFormat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author TheElectronWill
 */
public interface FileConfig extends Config, AutoCloseable {
	/**
	 * @return the config's file, as a classic File object
	 */
	File getFile();

	/**
	 * @return the config's file, as a NIO Path object
	 */
	Path getNioPath();

	/**
	 * Saves this config as soon as possible. This method may return quickly and perform the IO
	 * operations in background, or it may block until the operations are done.
	 */
	void save();

	/**
	 * (Re)loads this config from the file. This method blocks until the read operation completes.
	 */
	void load();

	/**
	 * Closes this FileConfig, releases its associated resources (if any), and ensure that the
	 * ongoing saving operations complete.
	 * <p>
	 * A closed FileConfig can still be used via the Config's methods, but {@link #save()} and
	 * {@link #load()} will throw an IllegalStateException. Closing an aleady closed FileConfig has
	 * no effect.
	 */
	@Override
	void close();

	@Override
	default FileConfig checked() {
		return new CheckedFileConfig(this);
	}

	/**
	 * Creates a new FileConfig based on the specified file. The format is detected automatically.
	 *
	 * @param file the file to use to save and load the config
	 * @return a new FileConfig associated to the specified file
	 *
	 * @throws NoFormatFoundException if the format detection fails
	 */
	static FileConfig of(File file) {
		return of(file.toPath());
	}

	/**
	 * Creates a new FileConfig based on the specified file and format.
	 *
	 * @param file   the file to use to save and load the config
	 * @param format the config's format
	 * @return a new FileConfig associated to the specified file
	 */
	static FileConfig of(File file, ConfigFormat<? extends Config> format) {
		return of(file.toPath(), format);
	}

	/**
	 * Creates a new FileConfig based on the specified file. The format is detected automatically.
	 *
	 * @param file the file to use to save and load the config
	 * @return a new FileConfig associated to the specified file
	 *
	 * @throws NoFormatFoundException if the format detection fails
	 */
	static FileConfig of(Path file) {
		ConfigFormat<?> format = FormatDetector.detect(file);
		if (format == null) {
			throw new NoFormatFoundException("No suitable format for " + file.getFileName());
		}
		return of(file, format);
	}

	/**
	 * Creates a new FileConfig based on the specified file and format.
	 *
	 * @param file   the file to use to save and load the config
	 * @param format the config's format
	 * @return a new FileConfig associated to the specified file
	 */
	static FileConfig of(Path file, ConfigFormat<? extends Config> format) {
		return builder(file, format).build();
	}

	/**
	 * Creates a new FileConfig based on the specified file. The format is detected automatically.
	 *
	 * @param filePath the file's path
	 * @return a new FileConfig associated to the specified file
	 *
	 * @throws NoFormatFoundException if the format detection fails
	 */
	static FileConfig of(String filePath) {
		return of(Paths.get(filePath));
	}

	/**
	 * Creates a new FileConfig based on the specified file and format.
	 *
	 * @param filePath the file's path
	 * @param format   the config's format
	 * @return a new FileConfig associated to the specified file
	 */
	static FileConfig of(String filePath, ConfigFormat<?> format) {
		return of(Paths.get(filePath), format);
	}

	/**
	 * Creates a new thread-safe FileConfig based on the specified file. The format is detected
	 * automatically.
	 *
	 * @param file the file to use to save and load the config
	 * @return a new thread-safe FileConfig associated to the specified file
	 *
	 * @throws NoFormatFoundException if the format detection fails
	 */
	static FileConfig ofConcurrent(File file) {
		return ofConcurrent(file.toPath());
	}

	/**
	 * Creates a new thread-safe FileConfig based on the specified file and format.
	 *
	 * @param file   the file to use to save and load the config
	 * @param format the config's format
	 * @return a new thread-safe FileConfig associated to the specified file
	 */
	static FileConfig ofConcurrent(File file, ConfigFormat<?> format) {
		return ofConcurrent(file.toPath(), format);
	}

	/**
	 * Creates a new thread-safe FileConfig based on the specified file. The format is detected
	 * automatically.
	 *
	 * @param file the file to use to save and load the config
	 * @return a new thread-safe FileConfig associated to the specified file
	 *
	 * @throws NoFormatFoundException if the format detection fails
	 */
	static FileConfig ofConcurrent(Path file) {
		return builder(file).concurrent().build();
	}

	/**
	 * Creates a new thread-safe FileConfig based on the specified file and format.
	 *
	 * @param file   the file to use to save and load the config
	 * @param format the config's format
	 * @return a new thread-safe FileConfig associated to the specified file
	 */
	static FileConfig ofConcurrent(Path file, ConfigFormat<?> format) {
		return builder(file, format).concurrent().build();
	}

	/**
	 * Creates a new thread-safe FileConfig based on the specified file. The format is detected
	 * automatically.
	 *
	 * @param filePath the file's path
	 * @return a new thread-safe FileConfig associated to the specified file
	 *
	 * @throws NoFormatFoundException if the format detection fails
	 */
	static FileConfig ofConcurrent(String filePath) {
		return ofConcurrent(Paths.get(filePath));
	}

	/**
	 * Creates a new thread-safe FileConfig based on the specified file and format.
	 *
	 * @param filePath the file's path
	 * @param format   the config's format
	 * @return a new thread-safe FileConfig associated to the specified file
	 */
	static FileConfig ofConcurrent(String filePath, ConfigFormat<?> format) {
		return ofConcurrent(Paths.get(filePath), format);
	}

	/**
	 * Returns a FileConfigBuilder to create a FileConfig with many options. The format is detected
	 * automatically.
	 *
	 * @param file the file to use to save and load the config
	 * @return a new FileConfigBuilder that will build a FileConfig associated to the specified file
	 *
	 * @throws NoFormatFoundException if the format detection fails
	 */
	static FileConfigBuilder builder(File file) {
		return builder(file.toPath());
	}

	/**
	 * Returns a FileConfigBuilder to create a FileConfig with many options.
	 *
	 * @param file   the file to use to save and load the config
	 * @param format the config's format
	 * @return a new FileConfigBuilder that will build a FileConfig associated to the specified file
	 */
	static FileConfigBuilder builder(File file, ConfigFormat<?> format) {
		return builder(file.toPath(), format);
	}

	/**
	 * Returns a FileConfigBuilder to create a FileConfig with many options. The format is detected
	 * automatically.
	 *
	 * @param file the file to use to save and load the config
	 * @return a new FileConfigBuilder that will build a FileConfig associated to the specified file
	 *
	 * @throws NoFormatFoundException if the format detection fails
	 */
	static FileConfigBuilder builder(Path file) {
		ConfigFormat<?> format = FormatDetector.detect(file);
		if (format == null) {
			throw new NoFormatFoundException("No suitable format for " + file.getFileName());
		}
		return builder(file, format);
	}

	/**
	 * Returns a FileConfigBuilder to create a FileConfig with many options.
	 *
	 * @param file   the file to use to save and load the config
	 * @param format the config's format
	 * @return a new FileConfigBuilder that will build a FileConfig associated to the specified file
	 */
	static FileConfigBuilder builder(Path file, ConfigFormat<?> format) {
		return new FileConfigBuilder(file, format);
	}

	/**
	 * Returns a FileConfigBuilder to create a FileConfig with many options. The format is detected
	 * automatically.
	 *
	 * @param filePath the file's path
	 * @return a new FileConfigBuilder that will build a FileConfig associated to the specified file
	 *
	 * @throws NoFormatFoundException if the format detection fails
	 */
	static FileConfigBuilder builder(String filePath) {
		return builder(Paths.get(filePath));
	}

	/**
	 * Returns a FileConfigBuilder to create a FileConfig with many options.
	 *
	 * @param filePath the file's path
	 * @param format   the config's format
	 * @return a new FileConfigBuilder that will build a FileConfig associated to the specified file
	 */
	static FileConfigBuilder builder(String filePath, ConfigFormat<?> format) {
		return builder(Paths.get(filePath), format);
	}
}