package com.hypherionmc.moonconfig.core.validation.correctors;

import java.lang.reflect.Field;

public interface ValueCorrector<S> {
	S correct(String f, Object value, Object defaultValue);
}
