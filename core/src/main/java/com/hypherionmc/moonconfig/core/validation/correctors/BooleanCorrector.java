package com.hypherionmc.moonconfig.core.validation.correctors;

import java.util.concurrent.atomic.AtomicBoolean;

public final class BooleanCorrector implements ValueCorrector<Boolean> {

	@Override
	public Boolean correct(String f, Object vv, Object defaultValue) {
		Object value = vv == null ? defaultValue : vv;
		AtomicBoolean corrected = new AtomicBoolean(false);

		try {
			corrected.set(Boolean.parseBoolean(value.toString()));
		} catch (Exception e) {
			String v = value.toString();
			corrected.set(v.toLowerCase().startsWith("t"));
		}

		System.out.println("Corrected value for " + f + " from " + vv + " to " + corrected.get());
		return corrected.get();
	}
}
