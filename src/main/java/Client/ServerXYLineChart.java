package Client;

import java.awt.Color;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ServerXYLineChart extends JFrame {
    
    private final Map<Long, Long[]> databaseRequestAndResponseTimes;
    private final Map<Long, Long[]> serverRequestAndResponseTimes;
 
    public ServerXYLineChart(Map<Long, Long[]> databaseRequestAndResponseTimes, Map<Long, Long[]> serverRequestAndResponseTimes) {
        this.databaseRequestAndResponseTimes = databaseRequestAndResponseTimes;
        this.serverRequestAndResponseTimes = serverRequestAndResponseTimes;
    }
    
    private void initUI() {

        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        setSize(1080, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private XYDataset createDataset() {

        XYSeries databaseSeries = new XYSeries("Database Per Second Averages");
        for (Map.Entry<Long, Long[]> entry : this.databaseRequestAndResponseTimes.entrySet()) {
            Long key = entry.getKey();
            Long[] value = entry.getValue();
            Long avg = value[1] / value[0];
            databaseSeries.add(key, avg);
        }
        
        XYSeries serverSeries = new XYSeries("Server Per Second Averages");
        for (Map.Entry<Long, Long[]> entry : this.serverRequestAndResponseTimes.entrySet()) {
            Long key = entry.getKey();
            Long[] value = entry.getValue();
            Long avg = value[1] / value[0];
            serverSeries.add(key, avg);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(databaseSeries);
        dataset.addSeries(serverSeries);

        return dataset;
    }
    
    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Latencies Over Time", 
                "Time (seconds)", 
                "Latency (milliseconds)", 
                dataset, 
                PlotOrientation.VERTICAL,
                true, 
                false, 
                false 
        );

        return chart;

    }
    
    public void chartLatencies(){
        SwingUtilities.invokeLater(() -> {
            ServerXYLineChart chart = new ServerXYLineChart(this.databaseRequestAndResponseTimes, this.serverRequestAndResponseTimes);
            chart.setVisible(true);
            chart.initUI();
        });
    }
    
}
