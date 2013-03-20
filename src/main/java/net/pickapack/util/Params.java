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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Min Cai
 */
public class Params implements Serializable {
    private Map<Object, Object> properties;

    /**
     *
     */
    public Params() {
        this.properties = new HashMap<Object, Object>();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void put(Object key, Object value) {
        if(key == null || value == null) {
            throw new IllegalArgumentException();
        }

        this.properties.put(key, value);
    }

    /**
     *
     * @param <T>
     * @param clz
     * @param key
     * @param defaultValue
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clz, Object key, T defaultValue) {
        if(clz == null || key == null) {
            throw new IllegalArgumentException();
        }

        return this.properties.containsKey (key) ? (T) this.properties.get(key) : defaultValue;
    }

    /**
     *
     * @param <T>
     * @param clz
     * @param key
     * @return
     */
    public <T> T get(Class<T> clz, Object key) {
        return this.get(clz, key, null);
    }

    /**
     *
     * @return
     */
    public int size() {
        return this.properties.size();
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }
}
