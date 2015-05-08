/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the Archimulator multicore architectural simulator.
 *
 * Archimulator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Archimulator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Archimulator. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.pickapack.chart;

import net.pickapack.action.Function;

import java.io.Serializable;

/**
 * Sub line plot line.
 *
 * @author Min Cai
 */
public class SubLinePlotLine implements Serializable {
    private String title;
    private Function<Double> getValueCallback;

    /**
     * Create a sub line plot line.
     *
     * @param title the value
     * @param getValueCallback the callback function used to get the value
     */
    public SubLinePlotLine(String title, Function<Double> getValueCallback) {
        this.title = title;
        this.getValueCallback = getValueCallback;
    }

    /**
     * Get the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the callback function used to get the value.
     *
     * @return the callback function used to get the value
     */
    public Function<Double> getGetValueCallback() {
        return getValueCallback;
    }
}
