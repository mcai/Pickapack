package net.pickapack.util;

/**
 *
 * @author Min Cai
 * @param <T>
 * @param <ValueProviderT>
 */
public interface ValueProviderFactory<T, ValueProviderT extends ValueProvider<T>> {
    /**
     *
     * @param args
     * @return
     */
    ValueProviderT createValueProvider(Object... args);
}
