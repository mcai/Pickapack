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

import net.pickapack.DateHelper;
import net.pickapack.action.Function;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class LinePlotFrame extends ApplicationFrame {
    public int numSubPlots;
    private List<TimeSeriesCollection> dataSets;
    private List<Map<SubLinePlotLine, Function<Double>>> dataSinks;
    private LinePlot linePlot;

    public LinePlotFrame(LinePlot linePlot, int width, int height) throws SQLException {
        super(linePlot.getTitle());
        this.linePlot = linePlot;

        this.numSubPlots = linePlot.getSubLinePlots().size();

        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis("Time"));
        this.dataSets = new ArrayList<TimeSeriesCollection>();
        this.dataSinks = new ArrayList<Map<SubLinePlotLine, Function<Double>>>();

        for (SubLinePlot subLinePlot : linePlot.getSubLinePlots()) {
            TimeSeriesCollection dataSetsPerSubPlot = new TimeSeriesCollection();
            this.dataSets.add(dataSetsPerSubPlot);

            HashMap<SubLinePlotLine, Function<Double>> dataSinksPerSubPlot = new HashMap<SubLinePlotLine, Function<Double>>();
            this.dataSinks.add(dataSinksPerSubPlot);

            for (SubLinePlotLine subLinePlotLine : subLinePlot.getLines()) {
                TimeSeries timeSeries = new TimeSeries(subLinePlotLine.getTitle());
                dataSetsPerSubPlot.addSeries(timeSeries);
                dataSinksPerSubPlot.put(subLinePlotLine, subLinePlotLine.getGetValueCallback());
            }

            NumberAxis rangeAxis = new NumberAxis(subLinePlot.getTitleY());
            rangeAxis.setAutoRangeIncludesZero(false);
            XYPlot subplot = new XYPlot(dataSetsPerSubPlot, null, rangeAxis, new StandardXYItemRenderer());
            subplot.setBackgroundPaint(Color.lightGray);
            subplot.setDomainGridlinePaint(Color.white);
            subplot.setRangeGridlinePaint(Color.white);
            plot.add(subplot);
        }

        JFreeChart chart = new JFreeChart(linePlot.getTitle(), plot);
        chart.setBorderPaint(Color.black);
        chart.setBorderVisible(true);
        chart.setBackgroundPaint(Color.white);

        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(3600000.0);

        JPanel content = new JPanel(new BorderLayout());

        ChartPanel chartPanel = new ChartPanel(chart);
        content.add(chartPanel);

        chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(content);

        DataSink dataSink = new DataSink();
        new Thread(dataSink).start();
    }

    private class DataSink implements Runnable {
        public void run() {
            while (true) {
                for (int i = 0; i < numSubPlots; i++) {
                    TimeSeriesCollection timeSeriesCollection = dataSets.get(i);

                    SubLinePlot subLinePlot = linePlot.getSubLinePlots().get(i);
                    for (int j = 0; j < subLinePlot.getLines().size(); j++) {
                        Object seriesObj = timeSeriesCollection.getSeries().get(j);
                        TimeSeries series = (TimeSeries) seriesObj;
                        SubLinePlotLine subLinePlotLine = subLinePlot.getLines().get(j);
                        Double value = dataSinks.get(i).get(subLinePlotLine).apply();
                        java.lang.System.out.printf("[%s] '%s'.'%s' = %s\n", DateHelper.toString(new Date()), subLinePlot.getTitleY(), subLinePlotLine.getTitle(), value);
                        series.addOrUpdate(new Millisecond(), value);
                    }
                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}