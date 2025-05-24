package com.hypherionmc.moonconfig.hocon;

import com.hypherionmc.moonconfig.core.CommentedConfig;
import com.hypherionmc.moonconfig.core.ConfigFormat;
import com.hypherionmc.moonconfig.core.file.FormatDetector;
import com.hypherionmc.moonconfig.core.io.ConfigParser;
import com.hypherionmc.moonconfig.core.io.ConfigWriter;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents the Hocon config format.
 *
 * @author TheElectronWill
 */
public final class HoconFormat implements ConfigFormat<CommentedConfig> {
	private static final HoconFormat INSTANCE = new HoconFormat();

	/**
	 * @return the unique instance of HoconFormat
	 */
	public static HoconFormat instance() {
		return INSTANCE;
	}

	/**
	 * @return a new config with the hocon format
	 */
	public static CommentedConfig newConfig() {
		return INSTANCE.createConfig();
	}

	/**
	 * @return a new config with the given map creator
	 */
	public static CommentedConfig newConfig(Supplier<Map<String, Object>> s) {
		return INSTANCE.createConfig(s);
	}

	/**
	 * @return a new thread-safe config with the hocon format
	 */
	public static CommentedConfig newConcurrentConfig() {
		return INSTANCE.createConfig();
	}

	static {
		FormatDetector.registerExtension("hocon", INSTANCE);
		FormatDetector.registerExtension("conf", INSTANCE);
	}

	private HoconFormat() {}

	@Override
	public ConfigWriter createWriter() {
		return new HoconWriter();
	}

	@Override
	public ConfigParser<CommentedConfig> createParser() {
		return new HoconParser();
	}

	@Override
	public CommentedConfig createConfig(Supplier<Map<String, Object>> mapCreator) {
		return CommentedConfig.of(mapCreator, this);
	}

	@Override
	public boolean supportsComments() {
		return true;
	}
}