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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Min Cai
 */
public class LinePlot {
    private String title;
    private List<SubLinePlot> subLinePlots;

    /**
     *
     * @param title
     */
    public LinePlot(String title) {
        this.title = title;
        this.subLinePlots = new ArrayList<SubLinePlot>();
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return
     */
    public List<SubLinePlot> getSubLinePlots() {
        return subLinePlots;
    }

}
