package com.hypherionmc.moonconfig.core.validation.correctors;

public class StringCorrector implements ValueCorrector<String> {

    @Override
    public String correct(String field, Object o, Object defaultValue) {
        String object = o == null ? defaultValue.toString() : o.toString();
        System.out.println("Corrected value for " + field + " from " + o + " to \"" + object + "\"");
        return object;
    }
}
