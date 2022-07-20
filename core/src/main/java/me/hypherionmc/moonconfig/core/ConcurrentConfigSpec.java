package me.hypherionmc.moonconfig.core;

/**
 * @author TheElectronWill
 */
public class ConcurrentConfigSpec extends ConfigSpec {
	public ConcurrentConfigSpec() {
		super(Config.inMemoryUniversalConcurrent());
	}
}