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

import java.util.ArrayList;
import java.util.List;

public class BarPlot<ItemT> {
    private String title;
    private String titleY;
    private boolean stacked;
    private Predicate<ItemT> pred;
    private List<ItemT> items;
    private List<SubBarPlot<ItemT>> subBarPlots;

    public BarPlot(String title, String titleY, Predicate<ItemT> pred, List<ItemT> items) {
        this.title = title;
        this.titleY = titleY;
        this.pred = pred;
        this.items = items;
        this.subBarPlots = new ArrayList<SubBarPlot<ItemT>>();
    }

    public String getTitle() {
        return title;
    }

    public String getTitleY() {
        return titleY;
    }

    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    public boolean isStacked() {
        return stacked;
    }

    public Predicate<ItemT> getPred() {
        return pred;
    }

    public List<ItemT> getItems() {
        return items;
    }

    public List<SubBarPlot<ItemT>> getSubBarPlots() {
        return subBarPlots;
    }
}
