package gui.charts;

import gui.utility.MyLogger;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class RmsChartFrame extends JFrame {

    protected final String frameName = "RMS Error with respect to stored usabilities";
    protected JFreeChart chart;

    public RmsChartFrame(ArrayList<Double[][]> usabilityHistory1, Double[][] storedUsability) {
        this.setSize(800, 600);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle(frameName);

        this.setLayout(new BorderLayout());

        XYSeriesCollection dataset = convertPolicyToSeries(usabilityHistory1, storedUsability);

        chart = ChartFactory.createXYLineChart(
                frameName, "Iteration", "RMS error", dataset,
                PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        this.add(chartPanel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    protected XYSeriesCollection convertPolicyToSeries(ArrayList<Double[][]> usabilityHistory1, Double[][] storedUsability) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        if (usabilityHistory1 == null || usabilityHistory1.isEmpty() ||
                storedUsability == null) {
            MyLogger.append(frameName + " - Error: wrong data");

            return dataset;
        }
        Double[][] temp = usabilityHistory1.get(0);
        int xMax1 = temp.length;
        int yMax1 = temp[0].length;

        int xMax2 = storedUsability.length;
        int yMax2 = storedUsability[0].length;

        if (xMax1 != xMax2 || yMax1 != yMax2) {
            MyLogger.append(frameName + " - Error: wrong data");

            return dataset;
        }

        XYSeries series = new XYSeries("RMS error");

        int iMax = usabilityHistory1.size();
        double numberOfFileds = xMax1 * yMax2;
        
        int nextI = 1;
        
        for (int i = 0; i < iMax; i += nextI) {
            if(i > 10000) {
                nextI = 100;
            }
            
            Double[][] usablity1 = usabilityHistory1.get(i);

            double value = 0;
            for (int x = 0; x < xMax1; x++) {
                for (int y = 0; y < yMax1; y++) {
                    value += Math.pow(usablity1[x][y] - storedUsability[x][y], 2);
                }
            }

            value = Math.sqrt(value/numberOfFileds);
            series.add(i, value);
        }
        
        
        dataset.addSeries(series);
        return dataset;
    }
}
