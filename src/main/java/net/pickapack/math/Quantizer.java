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
public class Quantizer implements Serializable {
    private int maxValue;
    private int quantum;

    /**
     *
     * @param maxValue
     * @param quantum
     */
    public Quantizer(int maxValue, int quantum) {
        this.maxValue = maxValue;
        this.quantum = quantum;
    }

    /**
     *
     * @param rawValue
     * @return
     */
    public int quantize(int rawValue) {
        return Math.min(rawValue / this.quantum, this.maxValue);
    }

    /**
     *
     * @param value
     * @return
     */
    public int unQuantize(int value) {
        return value * this.quantum;
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
    public int getQuantum() {
        return quantum;
    }
}
