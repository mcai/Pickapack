package net.pickapack.util;

public interface ValueProvider<T> {
    T get();

    T getInitialValue();
}
