package me.hypherionmc.moonconfig.core.io;

import me.hypherionmc.moonconfig.core.UnmodifiableConfig;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.nio.file.StandardOpenOption.*;

/**
 * Interface for writing configurations.
 *
 * @author TheElectronWill
 */
public interface ConfigWriter {
	/**
	 * Writes a configuration.
	 *
	 * @param config the config to write
	 * @param writer the writer to write it to
	 * @throws WritingException if an error occurs
	 */
	void write(UnmodifiableConfig config, Writer writer);

	/**
	 * Writes a configuration.
	 *
	 * @param config the config to write
	 * @param output the output to write it to
	 * @throws WritingException if an error occurs
	 */
	default void write(UnmodifiableConfig config, OutputStream output, Charset charset) {
		Writer writer = new BufferedWriter(new OutputStreamWriter(output, charset));
		write(config, writer);
		try {
			writer.flush();
		} catch (IOException e) {
			throw new WritingException("Failed to flush the writer", e);
		}
	}

	/**
	 * Writes a configuration.
	 *
	 * @param config the config to write
	 * @param output the output to write it to
	 * @throws WritingException if an error occurs
	 */
	default void write(UnmodifiableConfig config, OutputStream output) {
		write(config, output, StandardCharsets.UTF_8);
	}

	/**
	 * Writes a configuration. The content of the file is overwritten. This method is equivalent to
	 * <pre>write(config, file, false)</pre>
	 *
	 * @param config  the config to write
	 * @param file the nio Path to write it to
	 * @throws WritingException if an error occurs
	 */
	default void write(UnmodifiableConfig config, Path file, WritingMode writingMode) {
		write(config, file, writingMode, StandardCharsets.UTF_8);
	}

	/**
	 * Writes a configuration.
	 *
	 * @param config  the config to write
	 * @param file the nio Path to write it to
	 * @throws WritingException if an error occurs
	 */
	default void write(UnmodifiableConfig config, Path file, WritingMode writingMode, Charset charset) {
		StandardOpenOption[] options;
		if (writingMode == WritingMode.APPEND) {
			options = new StandardOpenOption[] { WRITE, CREATE, APPEND };
		} else {
			options = new StandardOpenOption[] { WRITE, CREATE, TRUNCATE_EXISTING };
		}
		try (OutputStream output = Files.newOutputStream(file, options)) {
			write(config, output, charset);
		} catch (IOException e) {
			throw new WritingException("An I/O error occured", e);
		}
	}

	/**
	 * Writes a configuration. The content of the file is overwritten. This method is equivalent to
	 * <pre>write(config, file, false)</pre>
	 *
	 * @param config the config to write
	 * @param file   the file to write it to
	 * @throws WritingException if an error occurs
	 */
	default void write(UnmodifiableConfig config, File file, WritingMode writingMode) {
		write(config, file, writingMode, StandardCharsets.UTF_8);
	}

	/**
	 * Writes a configuration.
	 *
	 * @param config the config to write
	 * @param file   the file to write it to
	 * @throws WritingException if an error occurs
	 */
	default void write(UnmodifiableConfig config, File file, WritingMode writingMode, Charset charset) {
		write(config, file.toPath(), writingMode, charset);
	}

	/**
	 * Writes a configuration.
	 *
	 * @param config the config to write
	 * @param url    the url to write it to
	 * @throws WritingException if an error occurs
	 */
	default void write(UnmodifiableConfig config, URL url) {
		URLConnection connection;
		try {
			connection = url.openConnection();
		} catch (IOException e) {
			throw new WritingException("Unable to connect to the URL", e);
		}
		String encoding = connection.getContentEncoding();
		Charset charset = (encoding == null) ? StandardCharsets.UTF_8 : Charset.forName(encoding);
		try (OutputStream output = connection.getOutputStream()) {
			write(config, output, charset);
		} catch (IOException e) {
			throw new WritingException("An I/O error occured", e);
		}
	}

	/**
	 * Writes a configuration to a String.
	 *
	 * @param config the config to write
	 * @return a new String
	 *
	 * @throws WritingException if an error occurs
	 */
	default String writeToString(UnmodifiableConfig config) {
		CharsWrapper.Builder builder = new CharsWrapper.Builder(64);
		write(config, builder);
		return builder.toString();
	}
}