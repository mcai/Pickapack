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
