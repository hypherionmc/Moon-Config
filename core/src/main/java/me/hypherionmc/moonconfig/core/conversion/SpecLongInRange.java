package me.hypherionmc.moonconfig.core.conversion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the value of a field must be in a certain range (inclusive).
 *
 * @author TheElectronWill
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpecLongInRange {
	/** @return the minimum possible value, inclusive */
	long min();

	/** @return the maximum possible value, inclusive */
	long max();
}