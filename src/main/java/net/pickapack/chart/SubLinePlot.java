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
 * Sub line plot.
 *
 * @author Min Cai
 */
public class SubLinePlot implements Serializable {
    private String titleY;
    private List<SubLinePlotLine> lines;

    /**
     * Create a sub line plot.
     *
     * @param titleY the y title
     */
    public SubLinePlot(String titleY) {
        this.titleY = titleY;
        this.lines = new ArrayList<SubLinePlotLine>();
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
     * Get the list of lines.
     *
     * @return the list of lines
     */
    public List<SubLinePlotLine> getLines() {
        return lines;
    }
}
