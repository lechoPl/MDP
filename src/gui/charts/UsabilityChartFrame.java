package gui.charts;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class UsabilityChartFrame extends JFrame {

    protected final String fameName = "Usability Chart";
    protected JFreeChart chart;

    public UsabilityChartFrame(ArrayList<Double[][]> usabilityHistory) {
        this.setSize(800, 600);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle(fameName);

        this.setLayout(new BorderLayout());

        XYSeriesCollection dataset = convertHistoryToSeries(usabilityHistory);
        
        chart = ChartFactory.createXYLineChart(
                    "Usability Chart", "Number of iterations", "Utility estimates", dataset,
                    PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        this.add(chartPanel, BorderLayout.CENTER);
        
        this.setVisible(true);
    }

    protected XYSeriesCollection convertHistoryToSeries(ArrayList<Double[][]> usabilityHistory) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        if(usabilityHistory == null || usabilityHistory.isEmpty())
            return dataset;
        
        Double[][] temp = usabilityHistory.get(0);
        int xMax = temp.length;
        int yMax = temp[0].length;
        
        XYSeries[] series = new XYSeries[xMax*yMax];
        for(int i=0; i<xMax; i++) {
            for(int j=0; j<yMax; j++) {
                series[i*yMax + j] = new XYSeries("( " + i + ", " + j + " )");
            }
        }
        
        
        int nextI = 1;
        
        for(int i=0; i<usabilityHistory.size(); i+= nextI) {
            if(i > 10000) {
                nextI = 100;
            }
            
            Double[][] usability = usabilityHistory.get(i);
            
            for(int x=0; x<xMax; x++) {
                for(int y=0; y<yMax; y++) {
                    series[x*yMax + y].add(i, (double)usability[x][y]);
                }
            }
        }
        
        for(XYSeries s : series) {
            dataset.addSeries(s);
        }
        
        return dataset;
    }
}
