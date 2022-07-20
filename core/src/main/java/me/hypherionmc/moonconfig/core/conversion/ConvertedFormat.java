package me.hypherionmc.moonconfig.core.conversion;

import me.hypherionmc.moonconfig.core.Config;
import me.hypherionmc.moonconfig.core.ConfigFormat;
import me.hypherionmc.moonconfig.core.io.ConfigParser;
import me.hypherionmc.moonconfig.core.io.ConfigWriter;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author TheElectronWill
 */
public final class ConvertedFormat<C extends Config, F extends ConfigFormat<C>> implements ConfigFormat<C> {
	private final F initialFormat;
	private final Predicate<Class<?>> supportPredicate;

	public ConvertedFormat(F initialFormat, Predicate<Class<?>> supportPredicate) {
		this.initialFormat = initialFormat;
		this.supportPredicate = supportPredicate;
	}

	@Override
	public ConfigWriter createWriter() {
		return initialFormat.createWriter();
	}

	@Override
	public ConfigParser<C> createParser() {
		return initialFormat.createParser();
	}

	@Override
	public C createConfig() {
		return initialFormat.createConfig();
	}

	@Override
	public C createConcurrentConfig() {
		return initialFormat.createConcurrentConfig();
	}

	@Override
	public C createConfig(Supplier<Map<String, Object>> mapCreator) {
		return initialFormat.createConfig(mapCreator);
	}

	@Override
	public boolean supportsComments() {
		return initialFormat.supportsComments();
	}

	@Override
	public boolean supportsType(Class<?> type) {
		return supportPredicate.test(type);
	}
}