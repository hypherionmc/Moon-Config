package com.hypherionmc.moonconfig.core.validation.checks;

import java.util.function.Predicate;

public class ExpectString implements Predicate<Object> {

    @Override
    public boolean test(Object o) {
        return o instanceof String;
    }
}
