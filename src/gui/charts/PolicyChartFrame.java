package gui.charts;

import enums.Action;
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

public class PolicyChartFrame extends JFrame {

    protected final String fameName = "Policy Differences Chart";
    protected JFreeChart chart;

    public PolicyChartFrame(ArrayList<Action[][]> policyHistory1, Action[][] storedPolicy) {
        this.setSize(800, 600);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle(fameName);

        this.setLayout(new BorderLayout());

        XYSeriesCollection dataset = convertPolicyToSeries(policyHistory1, storedPolicy);

        chart = ChartFactory.createXYLineChart(
                "Policy Differences Chart", "Iteration", "Number of States", dataset,
                PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        this.add(chartPanel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    protected XYSeriesCollection convertPolicyToSeries(ArrayList<Action[][]> policyHistory1, Action[][] storedPolicy) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        if (policyHistory1 == null || policyHistory1.isEmpty()
                || storedPolicy == null) {
            MyLogger.append("Policy Chart - Error: wrong data");

            return dataset;
        }
        Action[][] temp = policyHistory1.get(0);
        int xMax1 = temp.length;
        int yMax1 = temp[0].length;

        int xMax2 = storedPolicy.length;
        int yMax2 = storedPolicy[0].length;

        if (xMax1 != xMax2 || yMax1 != yMax2) {
            MyLogger.append("Policy Chart - Error: wrong data");

            return dataset;
        }

        XYSeries[] series = new XYSeries[2];
        series[0] = new XYSeries("Different");
        series[1] = new XYSeries("Same");

        int iMax = policyHistory1.size();

        int numberOfFileds = xMax1 * yMax2;

        for (int i = 0; i < iMax; i++) {
            int diffrent = 0;

            for (int x = 0; x < xMax1; x++) {
                for (int y = 0; y < yMax1; y++) {
                    if (policyHistory1.get(i)[x][y] != storedPolicy[x][y]) {
                        diffrent++;
                    }
                }
            }
            int same = numberOfFileds - diffrent;
            series[0].add(i, diffrent);
            series[1].add(i, same);

        }

        for (XYSeries s : series) {
            dataset.addSeries(s);
        }

        return dataset;
    }
}
