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

import java.io.Serializable;

/**
 *
 * @author Min Cai
 * @param <K>
 * @param <T>
 * @param <P>
 */
public class Triple<K, T, P> implements Serializable {
    private K first;
    private T second;
    private P third;

    /**
     *
     * @param first
     * @param second
     * @param third
     */
    public Triple(K first, T second, P third) {
        this.first = first;
        this.second = second;
        this.third = third;
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
     * @return
     */
    public P getThird() {
        return third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        if (!first.equals(triple.first)) return false;
        if (!second.equals(triple.second)) return false;
        if (!third.equals(triple.third)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        result = 31 * result + third.hashCode();
        return result;
    }
}
