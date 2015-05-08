/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.pickapack.util;

import org.apache.commons.jxpath.JXPathContext;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Jaxen based XPath helper.
 *
 * @author Min Cai
 */
public class JaxenHelper {
    /**
     * Select the list of nodes under the specified object using the specified expression.
     *
     * @param <T> the type of the nodes
     * @param object the parent object
     * @param expression the selection expression
     * @return the list of nodes under the specified object using the specified expression
     */
    @Deprecated
    public static <T> List<T> selectNodes(Object object, String expression) {
        return evaluate(object, expression);
    }

    /**
     * Select a single node under the specified object using the specified expression.
     *
     * @param <T> the type of the node
     * @param object the parent object
     * @param expression the selection expression
     * @return the result node under the specified object using the specified expression
     */
    @Deprecated
    public static <T> T selectSingleNode(Object object, String expression) {
        return evaluate(object, expression);
    }

    /**
     * Evaluate the specified expression under the specified object.
     *
     * @param <T> the type of the node
     * @param object the parent object
     * @param expression the selection expression
     * @return the evaluation result under the specified object using the specified expression
     */
    @SuppressWarnings("unchecked")
    public static <T> T evaluate(Object object, String expression) {
        return (T) JXPathContext.newContext(object).getValue(expression);
    }

    /**
     * Dump the value under the specified object using the specified expression.
     *
     * @param stats the map of statistics
     * @param object the parent object
     * @param expression the selection expression
     */
    public static void dumpValueFromXPath(Map<String, String> stats, Object object, String expression) {
        Object resultObj = selectSingleNode(object, expression);
        if (resultObj != null) {
            if (resultObj instanceof Map) {
                Map resultMap = (Map) resultObj;

                for (Object key : resultMap.keySet()) {
                    stats.put(escape(expression) + "/" + key, toString(resultMap.get(key)));
                }
            } else {
                stats.put(escape(expression), toString(resultObj));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Dump the values under the specified object using the specified expression.
     *
     * @param stats the map of statistics
     * @param object the parent object
     * @param expression the selection expression
     */
    public static void dumpValuesFromXPath(Map<String, String> stats, Object object, String expression) {
        List<Object> result = selectNodes(object, expression);
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                Object resultObj = result.get(i);
                stats.put(escape(expression) + "/" + i, toString(resultObj));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Escape the specified string.
     *
     * @param str the string that is to be escaped
     * @return the escaped string
     */
    public static String escape(String str) {
        return str.replaceAll("'", "").replaceAll("\\[", "\\[").replaceAll("\\]", "\\]");
    }

    /**
     * Get a text representation of the specified result object.
     *
     * @param resultObject the result object
     * @return the text representation of the specified result object
     */
    public static String toString(Object resultObject) {
        if (resultObject instanceof Integer || resultObject instanceof Long || resultObject instanceof Float || resultObject instanceof Double) {
            return MessageFormat.format("{0}", resultObject);
        }
        return escape(resultObject + "");
    }
}
