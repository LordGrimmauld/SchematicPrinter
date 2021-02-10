package mod.grimmauld.schematicprinter.util;

import com.google.common.collect.Iterators;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;
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

	public void runForN(Consumer<T> action, int n) {
		for (int i = 0; i < n && queue.hasNext(); i++) {
			action.accept(queue.next());
		}
	}

	public void clear() {
		queue = Collections.emptyIterator();
	}

	public boolean isEmpty() {
		return !queue.hasNext();
	}
}
