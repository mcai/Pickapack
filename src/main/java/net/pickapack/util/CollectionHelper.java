package net.pickapack.util;

import net.pickapack.Pair;
import net.pickapack.action.Function1;
import net.pickapack.action.Function2;
import net.pickapack.action.Function3;
import net.pickapack.action.Predicate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Min Cai
 */
public class CollectionHelper {
    /**
     *
     * @param <E>
     * @param elements
     * @param predicate
     * @return
     */
    public static  <E> boolean allMatch(List<E> elements, Predicate<? super E> predicate) {
        for(E element : elements) {
            if(!predicate.apply(element)) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param <E>
     * @param elements
     * @param predicate
     * @return
     */
    public static  <E> boolean anyMatch(List<E> elements, Predicate<? super E> predicate) {
        for(E element : elements) {
            if(predicate.apply(element)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param <E>
     * @param elements
     * @param predicate
     * @return
     */
    public static <E> List<E> filter(List<E> elements, Predicate<? super E> predicate) {
        List<E> results = new ArrayList<E>();

        for(E element : elements) {
            if(predicate.apply(element)) {
                results.add(element);
            }
        }

        return results;
    }

    /**
     *
     * @param <E>
     * @param <T>
     * @param elements
     * @param clz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E, T> List<T> transform(List<E> elements, final Class<T> clz) {
        return transform(elements, new Function1<E, T>() {
            @Override
            public T apply(E param) {
                return (T)param;
            }
        });
    }

    /**
     *
     * @param <E>
     * @param <T>
     * @param elements
     * @param function
     * @return
     */
    public static <E, T> List<T> transform(List<E> elements, final Function1<? super E, T> function) {
        return transform(elements, new Function2<Integer, E, T>() {
            @Override
            public T apply(Integer index, E element) {
                return function.apply(element);
            }
        });
    }

    /**
     *
     * @param <E>
     * @param <T>
     * @param elements
     * @param function
     * @return
     */
    public static <E, T> List<T> transform(List<E> elements, Function2<Integer, ? super E, T> function) {
        List<T> results = new ArrayList<T>();

        int i = 0;
        for(E element : elements) {
            results.add(function.apply(i++, element));
        }

        return results;
    }

    /**
     *
     * @param <K>
     * @param <V>
     * @param <T>
     * @param elements
     * @param function
     * @return
     */
    public static <K, V, T> Map<K, T> transform(Map<K, V> elements, final Function2<? super K, V, T> function) {
        return transform(elements, new Function3<Integer, K, V, T>() {
            @Override
            public T apply(Integer index, K key, V value) {
                return function.apply(key, value);
            }
        });
    }

    /**
     *
     * @param <K>
     * @param <V>
     * @param <T>
     * @param elements
     * @param function
     * @return
     */
    public static <K, V, T> Map<K, T> transform(Map<K, V> elements, final Function3<Integer, ? super K, V, T> function) {
        Map<K, T> results = new LinkedHashMap<K, T>();

        int i = 0;
        for(K key : elements.keySet()) {
            results.put(key, function.apply(i++, key, elements.get(key)));
        }

        return results;
    }

    /**
     *
     * @param <K>
     * @param <V>
     * @param elements
     * @return
     */
    public static <K, V> Map<K, V> toMap(List<Pair<K, V>> elements) {
        Map<K, V> result = new LinkedHashMap<K, V>();
        for(Pair<K, V> element : elements) {
            result.put(element.getFirst(), element.getSecond());
        }
        return result;
    }
}
