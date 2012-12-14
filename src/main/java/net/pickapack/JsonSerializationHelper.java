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
package net.pickapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 *
 * @author Min Cai
 */
public class JsonSerializationHelper {
    private static Gson gson;
    private static Gson gsonPrettyPrint;

    static {
        gson = new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();
        gsonPrettyPrint = new GsonBuilder().setPrettyPrinting().serializeNulls().serializeSpecialFloatingPointValues().create();
    }

    /**
     *
     * @param <T>
     * @param clz
     * @param str
     * @return
     */
    public static <T> T deserialize(Class<T> clz, String str) {
        return fromJson(clz, str);
    }

    /**
     *
     * @param <T>
     * @param clz
     * @param str
     * @return
     */
    public static <T> T deserialize(Type clz, String str) {
        return fromJson(clz, str);
    }

    /**
     *
     * @param obj
     * @return
     */
    public static String serialize(Object obj) {
        return toJson(obj);
    }

    /**
     *
     * @param obj
     * @param type
     * @return
     */
    public static String serialize(Object obj, Type type) {
        return toJson(obj, type);
    }

    /**
     *
     * @param <T>
     * @param clz
     * @param str
     * @return
     */
    public static <T> T fromJson(Class<T> clz, String str) {
        return gson.fromJson(str, clz);
    }

    /**
     *
     * @param <T>
     * @param clz
     * @param str
     * @return
     */
    public static <T> T fromJson(Type clz, String str) {
        return gson.fromJson(str, clz);
    }

    /**
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return toJson(obj, false);
    }

    /**
     *
     * @param obj
     * @param type
     * @return
     */
    public static String toJson(Object obj, Type type) {
        return toJson(obj, type, false);
    }

    /**
     *
     * @param obj
     * @param prettyPrint
     * @return
     */
    public static String toJson(Object obj, boolean prettyPrint) {
        return prettyPrint ? gsonPrettyPrint.toJson(obj) : gson.toJson(obj);
    }

    /**
     *
     * @param obj
     * @param type
     * @param prettyPrint
     * @return
     */
    public static String toJson(Object obj, Type type, boolean prettyPrint) {
        return prettyPrint ? gsonPrettyPrint.toJson(obj, type) : gson.toJson(obj, type);
    }

    /**
     *
     * @param json
     * @return
     */
    public static String prettyPrint(String json) {
        return gsonPrettyPrint.toJson(gsonPrettyPrint.fromJson(json, Object.class));
    }

    /**
     *
     */
    public static class ObjectWrapper {
        private String className;
        private String str;

        /**
         *
         * @param className
         * @param obj
         */
        public ObjectWrapper(String className, Object obj) {
            this.className = className;
            this.str = serialize(obj);
}

        /**
         *
         * @param <T>
         * @return
         */
        @SuppressWarnings("unchecked")
        public <T> T getObj() {
            try {
                return (T) deserialize(Class.forName(this.className), this.str);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}