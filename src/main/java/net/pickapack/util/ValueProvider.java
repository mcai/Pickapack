package net.pickapack.util;

/**
 *
 * @author Min Cai
 * @param <T>
 */
public interface ValueProvider<T> {
    /**
     *
     * @return
     */
    T get();

    /**
     *
     * @return
     */
    T getInitialValue();
}
