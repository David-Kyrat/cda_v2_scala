package cda.view.jfxuserform.utilities;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PPair<T> extends Pair<T, T> {
    public PPair(T t, T t2) {
        super(t, t2);
    }

    /**
     * @return Immutable List containing both elements
     */
    private List<T> ALL() {
        return List.of(super._1(), super._2());
    }

    /**
     * Returns a sequential {@code Stream} on both elements as its source.
     * <p> Code pasted from method {@code stream()} of {@link java.util.Collection}
     *
     * <p>This method should be overridden when the {@code spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@code spliterator()}
     * for details.)
     *
     * @return a sequential {@code Stream} over the elements in this collection
     */
    public Stream<T> stream() {
        return ALL().stream();
    }

    /**
     * Performs the given action for each element of the Iterable until all elements have been processed
     * or the action throws an exception.
     * Actions are performed in the order of iteration, if that order is specified.
     * Exceptions thrown by the action are relayed to the caller.
     * The behavior of this method is unspecified if the action performs side-effects
     * that modify the underlying source of elements,
     * unless an overriding class has specified a concurrent modification policy.
     *
     * @param action The action to be performed for each element
     */
    public void forEach(Consumer<? super T> action) { ALL().forEach(action); }
}
