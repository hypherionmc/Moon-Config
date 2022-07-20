package me.hypherionmc.moonconfig.core;

public enum TestEnum {
	A {
		@Override
		int id() {
			return 1;
		}
	},

	B {
		@Override
		int id() {
			return 2;
		}
	},

	C {
		@Override
		int id() {
			return 3;
		}
	};

	abstract int id();
}
