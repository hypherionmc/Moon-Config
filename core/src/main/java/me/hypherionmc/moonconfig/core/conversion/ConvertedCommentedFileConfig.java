package me.hypherionmc.moonconfig.core.conversion;

import me.hypherionmc.moonconfig.core.file.CommentedFileConfig;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author TheElectronWill
 */
public final class ConvertedCommentedFileConfig
		extends AbstractConvertedCommentedConfig<CommentedFileConfig>
		implements CommentedFileConfig {

	public ConvertedCommentedFileConfig(CommentedFileConfig config, ConversionTable readTable,
										ConversionTable writeTable,
										Predicate<Class<?>> supportPredicate) {
		this(config, readTable::convert, writeTable::convert, supportPredicate);
	}

	public ConvertedCommentedFileConfig(CommentedFileConfig config,
										Function<Object, Object> readConversion,
										Function<Object, Object> writeConversion,
										Predicate<Class<?>> supportPredicate) {
		super(config, readConversion, writeConversion, supportPredicate);
	}

	@Override
	public File getFile() {
		return config.getFile();
	}

	@Override
	public Path getNioPath() {
		return config.getNioPath();
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
}