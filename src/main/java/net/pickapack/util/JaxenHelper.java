package net.pickapack.util;

import org.apache.commons.jxpath.JXPathContext;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

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
    @Deprecated
    public static <T> List<T> selectNodes(Object obj, String expr) {
        return evaluate(obj, expr);
    }

    /**
     *
     * @param <T>
     * @param obj
     * @param expr
     * @return
     */
    @Deprecated
    public static <T> T selectSingleNode(Object obj, String expr) {
        return evaluate(obj, expr);
    }

    /**
     *
     * @param <T>
     * @param obj
     * @param expr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T evaluate(Object obj, String expr) {
        return (T) JXPathContext.newContext(obj).getValue(expr);
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
                    stats.put(escape(expr) + "/" + key, toString(resultMap.get(key)));
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
                stats.put(escape(expr) + "/" + i, toString(resultObj));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static String escape(String str) {
        return str.replaceAll("'", "").replaceAll("\\[", "\\[").replaceAll("\\]", "\\]");
    }

    public static String toString(Object resultObj) {
        if (resultObj instanceof Integer || resultObj instanceof Long || resultObj instanceof Float || resultObj instanceof Double) {
            return MessageFormat.format("{0}", resultObj);
        }
        return escape(resultObj + "");
    }
}
