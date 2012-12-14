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
public class SaturatingCounter implements Serializable {
    private int minValue;
    private int threshold;
    private int maxValue;
    private int value;
    private int initialValue;

    /**
     *
     * @param minValue
     * @param threshold
     * @param maxValue
     * @param initialValue
     */
    public SaturatingCounter(int minValue, int threshold, int maxValue, int initialValue) {
        this.minValue = minValue;
        this.threshold = threshold;
        this.maxValue = maxValue;
        this.value = initialValue;
        this.initialValue = initialValue;
    }

    /**
     *
     */
    public void reset() {
        this.value = this.initialValue;
    }

    /**
     *
     * @param direction
     */
    public void update(boolean direction) {
        if (direction) {
            this.inc();
        } else {
            this.dec();
        }
    }

    private void inc() {
        if (this.value < this.maxValue) {
            this.value++;
        }
    }

    private void dec() {
        if (this.value > this.minValue) {
            this.value--;
        }
    }

    /**
     *
     * @return
     */
    public boolean isTaken() {
        return this.getValue() >= this.threshold;
    }

    /**
     *
     * @return
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     *
     * @return
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     *
     * @return
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     *
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     *
     * @return
     */
    public int getInitialValue() {
        return initialValue;
    }
}
