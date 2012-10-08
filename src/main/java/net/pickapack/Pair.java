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

/**
 *
 * @author Min Cai
 * @param <K>
 * @param <T>
 */
public class Pair<K, T> {
    private K first;
    private T second;

    /**
     *
     * @param first
     * @param second
     */
    public Pair(K first, T second) {
        this.first = first;
        this.second = second;
    }

    /**
     *
     * @return
     */
    public K getFirst() {
        return first;
    }

    /**
     *
     * @return
     */
    public T getSecond() {
        return second;
    }

    /**
     *
     * @param first
     */
    public void setFirst(K first) {
        this.first = first;
    }

    /**
     *
     * @param second
     */
    public void setSecond(T second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (!first.equals(pair.first)) return false;
        if (!second.equals(pair.second)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("Pair{first=%s, second=%s}", first, second);
    }
}
