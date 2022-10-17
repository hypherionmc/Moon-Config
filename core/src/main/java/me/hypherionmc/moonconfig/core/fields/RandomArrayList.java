package me.hypherionmc.moonconfig.core.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * @author HypherionSA
 */
public class RandomArrayList<E> extends ArrayList<E> {

	private E lastValue;

	public RandomArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	public RandomArrayList() {
		super();
	}

	public RandomArrayList(Collection<? extends E> c) {
		super(c);
	}

	public E getNextRandom() {
		if (lastValue == null) {
			lastValue = getRandomValue();
		}
		if (new Random().nextInt(10) == 2) {
			lastValue = getRandomValue();
		}
		return lastValue;
	}

	private E getRandomValue() {
		return this.get(new Random().nextInt(this.size()));
	}

	@SafeVarargs
	public static <T> RandomArrayList<T> of(T... o) {
		RandomArrayList<T> l = new RandomArrayList<>();
		l.addAll(Arrays.asList(o));
		return l;
	}
}
