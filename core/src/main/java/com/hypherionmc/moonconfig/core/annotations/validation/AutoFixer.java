package com.hypherionmc.moonconfig.core.annotations.validation;

import com.hypherionmc.moonconfig.core.validation.correctors.ValueCorrector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoFixer {
	Class<? extends Predicate<Object>> test();
	Class<? extends ValueCorrector<?>> corrector();
	String defaultValue();
}
