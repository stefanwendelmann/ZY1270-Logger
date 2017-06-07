package de.stefanwndelmann.zy1270logger.Test;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
/**
 *
 * @author swendelmann
 */
public class DomainTranslateDemo extends ApplicationFrame {
    private static class DemoPanel extends JPanel implements ChangeListener {

        private static int SLIDER_INITIAL_VALUE = 50;
        private JSlider slider;
        private DateAxis domainAxis;
        private int lastValue = SLIDER_INITIAL_VALUE;

        // one month (milliseconds, seconds, minutes, hours, days)
        private int delta = 1000 * 60 * 60 * 24 * 30;

        public DemoPanel() {
            super(new BorderLayout());
            JFreeChart chart = createChart();
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
            chartPanel.setDomainZoomable(true);
            chartPanel.setRangeZoomable(true);
            Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEtchedBorder()
            );
            chartPanel.setBorder(border);
            add(chartPanel);

            JPanel dashboard = new JPanel(new BorderLayout());
            dashboard.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));   

            this.slider = new JSlider(0, 100, SLIDER_INITIAL_VALUE);
            this.slider.addChangeListener(this);
            dashboard.add(this.slider);
            add(dashboard, BorderLayout.SOUTH);
        }

        private JFreeChart createChart() {

            TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
            TimeSeries series = createSerie(500,20);
            timeSeriesCollection.addSeries(series );

            this.domainAxis = new DateAxis("Time");
            NumberAxis rangeAxis = new NumberAxis("");
            XYBarRenderer renderer = new XYBarRenderer();
            renderer.setShadowVisible(false);
            XYPlot plot = new XYPlot(timeSeriesCollection, domainAxis, rangeAxis, renderer);

            JFreeChart chart = new JFreeChart(
                    "Title", 
                    JFreeChart.DEFAULT_TITLE_FONT,
                    plot, 
                    true);
            // performance
            chart.setAntiAlias(false);
            return chart;
        }

        private TimeSeries createSerie(int domainCount,int rangeCount) {
            TimeSeries timeSeries =  new TimeSeries("timeSeries1");
            Day d = new Day(new Date());
            RegularTimePeriod regularTimePeriod = d.next();
            for (int index = 0; index < domainCount; index++) {
                if (index % 2 == 0) {
                    double value = (Math.random() * rangeCount);
                    timeSeries.add(regularTimePeriod,value);
                }
                regularTimePeriod = regularTimePeriod.next();
            }
            return timeSeries;
        }

        @Override
        public void stateChanged(ChangeEvent event) {
            int value = this.slider.getValue();
            long minimum = domainAxis.getMinimumDate().getTime();
            long maximum = domainAxis.getMaximumDate().getTime();
            if (value<lastValue) { // left
                minimum = minimum - delta;
                maximum = maximum - delta;
            } else { // right
                minimum = minimum + delta;
                maximum = maximum + delta;
            }
            DateRange range = new DateRange(minimum,maximum);
            domainAxis.setRange(range);
            lastValue = value;
        }

    }

    public DomainTranslateDemo(String title) {
        super(title);
        setContentPane(new DemoPanel());
    }

    public static JPanel createDemoPanel() {
        return new DemoPanel();
    }

    public static void main(String[] args) {
        DomainTranslateDemo demo = new DomainTranslateDemo("Translate Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
