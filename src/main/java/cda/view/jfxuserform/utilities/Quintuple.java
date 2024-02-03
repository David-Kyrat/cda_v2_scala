package cda.view.jfxuserform.utilities;

public final class Quintuple<T, U, V, W, X> {

    private final T t;
    private final U u;
    private final V v;
    private final W w;
    private final X x;


    public Quintuple(T t, U u, V v, W w, X x) {
        this.t = t;
        this.u = u;
        this.v = v;
        this.w = w;
        this.x = x;
    }

    public T _1() {
        return this.t;
    }


    public U _2() {
        return this.u;
    }


    public V _3() {
        return this.v;
    }

    public W _4() {
        return this.w;
    }

    public X _5() {
        return this.x;
    }


    @Override
    public String toString() {
        return "{" +
            " t='" + _1() + "'" +
            ", u='" + _2() + "'" +
            ", v='" + _3() + "'" +
            ", w='" + _4() + "'" +
            ", x='" + _5() + "'" +
            "}";
    }


}
