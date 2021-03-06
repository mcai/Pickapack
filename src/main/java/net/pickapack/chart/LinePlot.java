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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Line plot.
 *
 * @author Min Cai
 */
public class LinePlot implements Serializable {
    private String title;
    private List<SubLinePlot> subLinePlots;

    /**
     * Create a line plot.
     *
     * @param title the title
     */
    public LinePlot(String title) {
        this.title = title;
        this.subLinePlots = new ArrayList<SubLinePlot>();
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
     * Get the list of sub line plots.
     *
     * @return the list of sub line plots
     */
    public List<SubLinePlot> getSubLinePlots() {
        return subLinePlots;
    }

}
