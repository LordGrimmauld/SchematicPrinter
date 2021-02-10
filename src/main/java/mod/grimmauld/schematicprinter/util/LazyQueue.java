package mod.grimmauld.schematicprinter.util;

import com.google.common.collect.Iterators;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LazyQueue<T> {
	private Iterator<T> queue;

	public LazyQueue() {
		clear();
	}

	public void addAll(Stream<T> elements) {
		queue = Iterators.concat(queue, elements.iterator());
	}

	@SafeVarargs
	public final void add(T... elements) {
		addAll(Stream.of(elements));
	}

	public void runForN(Consumer<T> action, int n, Predicate<T> shouldRun) {
		for (int i = 0; i < n && queue.hasNext(); i++) {
			T next = queue.next();
			if (shouldRun.test(next))
				action.accept(next);
			else
				i--;
		}
	}

	public void runForN(Consumer<T> action, int n) {
		runForN(action, n, t -> true);
	}

	public void clear() {
		queue = Collections.emptyIterator();
	}

	public boolean isEmpty() {
		return !queue.hasNext();
	}
}
