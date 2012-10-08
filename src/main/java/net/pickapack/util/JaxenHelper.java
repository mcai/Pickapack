package net.pickapack.util;

import ch.lambdaj.function.convert.Converter;
import org.jaxen.JaxenException;
import org.jaxen.javabean.Element;
import org.jaxen.javabean.JavaBeanXPath;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;

/**
 *
 * @author Min Cai
 */
public class JaxenHelper {
    /**
     *
     * @param <T>
     * @param obj
     * @param expr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> selectNodes(Object obj, String expr) {
        try {
            List<Element> result = new JavaBeanXPath(expr).selectNodes(obj);
            return result != null ? convert(result, new Converter<Element, T>() {
                @Override
                public T convert(Element from) {
                    return (T) from.getObject();
                }
            }) : null;
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param <T>
     * @param obj
     * @param expr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T selectSingleNode(Object obj, String expr) {
        try {
            Element result = (Element) new JavaBeanXPath(expr).selectSingleNode(obj);
            return (T) (result != null ? result.getObject() : null);
        } catch (JaxenException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param stats
     * @param obj
     * @param expr
     */
    public static void dumpValueFromXPath(Map<String, String> stats, Object obj, String expr) {
        Object resultObj = selectSingleNode(obj, expr);
        if (resultObj != null) {
            if (resultObj instanceof Map) {
                Map resultMap = (Map) resultObj;

                for (Object key : resultMap.keySet()) {
                    stats.put(escape(expr) + "[" + key + "]", toString(resultMap.get(key)));
                }
            } else {
                stats.put(escape(expr), toString(resultObj));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param stats
     * @param obj
     * @param expr
     */
    public static void dumpValuesFromXPath(Map<String, String> stats, Object obj, String expr) {
        List<Object> result = selectNodes(obj, expr);
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                Object resultObj = result.get(i);
                stats.put(escape(expr) + "[" + i + "]", toString(resultObj));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static String escape(String str) {
        return str.replaceAll("'", "").replaceAll("\\[", "\\[").replaceAll("\\]", "\\]");
    }

    private static String toString(Object resultObj) {
        if (resultObj instanceof Integer || resultObj instanceof Long || resultObj instanceof Float || resultObj instanceof Double) {
            return MessageFormat.format("{0}", resultObj);
        }
        return escape(resultObj + "");
    }
}
