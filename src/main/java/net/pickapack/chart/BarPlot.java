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

import net.pickapack.action.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bar plot.
 *
 * @author Min Cai
 * @param <ItemT> the type of items
 */
public class BarPlot<ItemT> implements Serializable {
    private String title;
    private String titleY;
    private boolean stacked;
    private Predicate<ItemT> predicate;
    private List<ItemT> items;
    private List<SubBarPlot<ItemT>> subBarPlots;

    /**
     * Create a bar plot.
     *
     * @param title the title
     * @param titleY the y title
     * @param predicate the predicate
     * @param items the list of items
     */
    public BarPlot(String title, String titleY, Predicate<ItemT> predicate, List<ItemT> items) {
        this.title = title;
        this.titleY = titleY;
        this.predicate = predicate;
        this.items = items;
        this.subBarPlots = new ArrayList<SubBarPlot<ItemT>>();
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
     * Get the y title.
     *
     * @return the y title
     */
    public String getTitleY() {
        return titleY;
    }

    /**
     * Get a value indicating whether the bar plot is stacked or not.
     *
     * @return a value indicating whether the bar plot is stacked or not
     */
    public boolean isStacked() {
        return stacked;
    }

    /**
     * Set a value indicating whether the bar plot is stacked or not.
     *
     * @param stacked a value indicating whether the bar plot is stacked or not
     */
    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    /**
     * Get the predicate.
     *
     * @return the predicate
     */
    public Predicate<ItemT> getPredicate() {
        return predicate;
    }

    /**
     * Get the list of items.
     *
     * @return the list of items
     */
    public List<ItemT> getItems() {
        return items;
    }

    /**
     * Get the list of sub bar plots.
     *
     * @return the list of sub bar plots
     */
    public List<SubBarPlot<ItemT>> getSubBarPlots() {
        return subBarPlots;
    }
}
