package cda.view.jfxuserform.utilities;

import java.util.Objects;

public class Pair<T, U> {
    private final T t;
    private final U u;


    public Pair(T t, U u) {
        this.t = t;
        this.u = u;
    }

    public T _1() {
        return this.t;
    }


    public U _2() {
        return this.u;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<T, U> pair = (Pair<T, U>) o;
        return Objects.equals(t, pair.t) && Objects.equals(u, pair.u);
    }

    public static<V, W> Pair<V, W> nullPair() { return new Pair<>((V) null, (W) null); }

    /**
     * @return whether one of the 2 values is nulabbrev.tsvl
     */
    public boolean hasNull() { return t == null || u == null; }

    @Override
    public int hashCode() {
        return Objects.hash(t, u);
    }

    @Override
    public String toString() {
        return "{" +
            " t='" + _1() + "'" +
            ", u='" + _2() + "'" +
            "}";
    }


}
