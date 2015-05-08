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

import net.pickapack.action.Function1;

import java.io.Serializable;

/**
 * Sub bar plot.
 *
 * @author Min Cai
 * @param <ItemT> the type of the items
 */
public class SubBarPlot<ItemT> implements Serializable {
    private String title;
    private Function1<ItemT, Double> getValueCallback;
    private Function1<ItemT, String> getTitleCallback;

    /**
     * Create a sub bar plot.
     *
     * @param title the title
     * @param getValueCallback the callback function used to get the value.
     * @param getTitleCallback the callback function used to get the title
     */
    public SubBarPlot(String title, Function1<ItemT, Double> getValueCallback, Function1<ItemT, String> getTitleCallback) {
        this.title = title;
        this.getValueCallback = getValueCallback;
        this.getTitleCallback = getTitleCallback;
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
    public Function1<ItemT, Double> getGetValueCallback() {
        return getValueCallback;
    }

    /**
     * Get the callback function used to get the title.
     *
     * @return the callback function used to get the title
     */
    public Function1<ItemT, String> getGetTitleCallback() {
        return getTitleCallback;
    }
}
