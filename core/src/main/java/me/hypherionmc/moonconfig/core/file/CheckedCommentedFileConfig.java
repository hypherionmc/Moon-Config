package me.hypherionmc.moonconfig.core.file;

import me.hypherionmc.moonconfig.core.CommentedConfig;
import me.hypherionmc.moonconfig.core.Config;
import me.hypherionmc.moonconfig.core.ConfigFormat;
import me.hypherionmc.moonconfig.core.utils.CommentedConfigWrapper;
import me.hypherionmc.moonconfig.core.utils.TransformingMap;
import me.hypherionmc.moonconfig.core.utils.TransformingSet;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author TheElectronWill
 */
class CheckedCommentedFileConfig extends CommentedConfigWrapper<CommentedFileConfig>
		implements CommentedFileConfig {
	/**
	 * Creates a new CheckedConfig around a commented configuration.
	 * <p>
	 * The values that are in the config when this method is called are also checked.
	 *
	 * @param config the configuration to wrap
	 */
	CheckedCommentedFileConfig(CommentedFileConfig config) {
		super(config);
	}

	@Override
	public Path getNioPath() {
		return config.getNioPath();
	}

	@Override
	public File getFile() {
		return config.getFile();
	}

	@Override
	public void save() {
		config.save();
	}

	@Override
	public void load() {
		config.load();
	}

	@Override
	public void close() {
		config.close();
	}

	@Override
	public CommentedFileConfig checked() {
		return this;
	}

	@Override
	public <T> T set(List<String> path, Object value) {
		return super.set(path, checkedValue(value));
	}

	@Override
	public boolean add(List<String> path, Object value) {
		return super.add(path, checkedValue(value));
	}

	@Override
	public Map<String, Object> valueMap() {
		return new TransformingMap<>(super.valueMap(), v -> v, this::checkedValue, o -> o);
	}

	@Override
	public Set<? extends CommentedConfig.Entry> entrySet() {
		return new TransformingSet<>((Set<CommentedConfig.Entry>)super.entrySet(), v -> v, this::checkedValue, o -> o);
	}

	@Override
	public String toString() {
		return "checked of " + config;
	}

	/**
	 * Checks that a value is supported by the config. Throws an unchecked exception if the value
	 * isn't supported.
	 */
	private void checkValue(Object value) {
		ConfigFormat<?> format = configFormat();
		if (value != null && !format.supportsType(value.getClass())) {
			throw new IllegalArgumentException(
					"Unsupported value type: " + value.getClass().getTypeName());
		} else if (value == null && !format.supportsType(null)) {
			throw new IllegalArgumentException(
					"Null values aren't supported by this configuration.");
		}
		if (value instanceof Config) {
			((Config)value).valueMap().forEach((k, v) -> checkValue(v));
		}
	}

	/**
	 * Checks that a value is supported by the config, and returns it if it's supported. Throws an
	 * unchecked exception if the value isn't supported.
	 */
	private <T> T checkedValue(T value) {
		checkValue(value);
		return value;
	}
}