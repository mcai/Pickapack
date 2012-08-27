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

public class JsonSerializationHelper {
    private static Gson gson;
    private static Gson gsonPrettyPrint;

    static {
        gson = new Gson();
        gsonPrettyPrint = new GsonBuilder().setPrettyPrinting().serializeNulls().serializeSpecialFloatingPointValues().create();
    }

    public static <T> T deserialize(Class<T> clz, String str) {
        return fromJson(clz, str);
    }

    public static String serialize(Object obj) {
        return toJson(obj);
    }

    public static <T> T fromJson(Class<T> clz, String str) {
        return gson.fromJson(str, clz);
    }

    public static String toJson(Object obj) {
        return toJson(obj, false);
    }

    public static String toJson(Object obj, boolean prettyPrint) {
        return prettyPrint ? gsonPrettyPrint.toJson(obj) : gson.toJson(obj);
    }

    public static String prettyPrint(String json) {
        return gsonPrettyPrint.toJson(gsonPrettyPrint.fromJson(json, Object.class));
    }

    public static class ObjectWrapper {
        private String className;
        private String str;

        public ObjectWrapper(String className, Object obj) {
            this.className = className;
            this.str = serialize(obj);
        }

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