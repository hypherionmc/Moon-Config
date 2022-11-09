package me.hypherionmc.moonconfig.core.fields;

import java.util.*;

/**
 * @author HypherionSA
 */
public class RandomArrayList<E> extends ArrayList<E> {

	private Optional<E> lastValue = Optional.empty();

	public RandomArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	public RandomArrayList() {
		super();
	}

	public RandomArrayList(Collection<? extends E> c) {
		super(c);
	}

	public Optional<E> getNextRandom() {
		if (!lastValue.isPresent()) {
			lastValue = Optional.ofNullable(getRandomValue());
		}
		if (new Random().nextInt(10) == 2) {
			lastValue = Optional.ofNullable(getRandomValue());
		}
		return lastValue;
	}

	private E getRandomValue() {
		if (!this.isEmpty()) {
			return this.get(new Random().nextInt(this.size()));
		}
		return null;
	}

	@SafeVarargs
	public static <T> RandomArrayList<T> of(T... o) {
		RandomArrayList<T> l = new RandomArrayList<>();
		l.addAll(Arrays.asList(o));
		return l;
	}
}
