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
package net.pickapack.math;

import java.io.Serializable;

/**
 *
 * @author Min Cai
 */
public class Counter implements Serializable {
    private long initialValue;
    private long value;

    /**
     *
     * @param initialValue
     */
    public Counter(long initialValue) {
        this.initialValue = initialValue;
        this.value = initialValue;
    }

    /**
     *
     * @return
     */
    public Counter inc() {
        this.value++;
        return this;
    }

    /**
     *
     * @return
     */
    public Counter dec() {
        this.value--;
        return this;
    }

    /**
     *
     * @return
     */
    public Counter reset() {
        this.value = this.initialValue;
        return this;
    }

    /**
     *
     * @return
     */
    public long getValue() {
        return value;
    }
}
