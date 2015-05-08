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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Bar plot frame.
 *
 * @author Min Cai
 * @param <ItemT> the type of items
 */
public class BarPlotFrame<ItemT> extends ApplicationFrame {
    /**
     * Create a bar plot frame.
     *
     * @param barPlot the bar plot
     * @param domainAxisLabel the domain axis label
     * @param width the width
     * @param height the height
     */
    public BarPlotFrame(BarPlot<ItemT> barPlot, String domainAxisLabel, int width, int height) {
        super(barPlot.getTitle());

        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        for (ItemT item : barPlot.getItems()) {
            if (barPlot.getPredicate().apply(item)) {
                for (SubBarPlot<ItemT> subBarPlot : barPlot.getSubBarPlots()) {
                    dataSet.addValue(
                            subBarPlot.getGetValueCallback().apply(item), subBarPlot.getTitle(),
                            subBarPlot.getGetTitleCallback().apply(item)
                    );
                }
            }
        }

        JFreeChart chart = barPlot.isStacked() ?
                ChartFactory.createStackedBarChart(
                        barPlot.getTitle(),
                        domainAxisLabel,
                        barPlot.getTitleY(),
                        dataSet,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                ) :
                ChartFactory.createBarChart(
                        barPlot.getTitle(),
                        domainAxisLabel,
                        barPlot.getTitleY(),
                        dataSet,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );

        CategoryPlot plot = chart.getCategoryPlot();

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(width, height));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(chartPanel);
    }
}