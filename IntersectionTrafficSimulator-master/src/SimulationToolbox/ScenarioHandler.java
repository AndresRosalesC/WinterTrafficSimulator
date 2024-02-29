package SimulationToolbox;

import Animation.Animatable;
import Animation.AnimationWindow;
import Intersection.Intersection;
import Road.Road;
import Vehicle.Vehicle;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.statistics.HistogramDataset;
import java.util.List;
import java.util.Arrays;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.CategoryLabelPositions;

import java.awt.*;
import java.util.ArrayList;

public class ScenarioHandler {

    public static final double DURATION_DEFAULT = 300;
    public static final double DURATION_INFINITE = Double.MAX_VALUE;

    private Scenario scenario;
    private double instance;
    private double duration;
    private int[] flags;
    private ArrayList<Road> roads;
    ArrayList<ArrayList<Integer>> waitinglist;
    public double avgdelay1;
    public double avgdelay2;
    public double avgnearmiss1;
    public double avgnearmiss2;
    public double avgrunred1;
    public double avgrunred2;
    
    public int mode;

    public ScenarioHandler(Scenario scenario){
        this.scenario = scenario;
        instance = 0;
        duration = DURATION_DEFAULT;
        waitinglist = new ArrayList<>();
    }

    public ScenarioHandler(Scenario scenario, double duration){
        this(scenario);
        this.duration = duration;
    }
    
    private CategoryDataset createDataset(String seriesKey, double[] values, double[] binEdges) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < binEdges.length - 1; i++) {
            double lowerBound = binEdges[i];
            double upperBound = binEdges[i + 1];
            String category = String.format("%.1f-%.1f", lowerBound, upperBound);

            int frequency = countValuesInInterval(values, lowerBound, upperBound);
            dataset.addValue(frequency, seriesKey, category);
        }

        return dataset;
    }

    private int countValuesInInterval(double[] values, double lowerBound, double upperBound) {
        int count = 0;
        for (double value : values) {
            if (value >= lowerBound && value < upperBound) {
                count++;
            }
        }
        return count;
    }
    
    
    public void proceedSimulation(){
        instance++;
        ArrayList<Simulatable> simulatables = scenario.getSimulatables();
        for (int i = 0; i < simulatables.size(); i++){
            Simulatable obj = simulatables.get(i);
            obj.simulate();
        }
        for(int i = 0; i < flags.length; i++){
            flags[i]++;
            if (roads.get(i).newVehicle(flags[i], scenario)){
                flags[i] = 0;
            }
        }
        if (this.getCurrentInstance() % 30 == 0){
            cleanup();
        }
        int pointer = 0;
        for (Road road : roads){
            waitinglist.get(pointer++).add(road.getWaitingCount());
        }
        System.out.println(getCurrentInstance());
    }

    public void drawAnimatables(Graphics graphics){
        ArrayList<Animatable> animatables = scenario.getAnimatables();
        for (int i = 0; i < animatables.size(); i++){
            Animatable obj = animatables.get(i);
            obj.draw(graphics);
        }
    }

    public boolean isTerminated(){
        return (instance >= duration);
    }

    public double getCurrentInstance(){
        return this.instance;
    }

    public void runSimulation(){
        scenario.buildScenario();
        AnimationWindow window = new AnimationWindow(this);
        ArrayList<Simulatable> simulatables = scenario.getSimulatables();
        for (int i = 0; i < simulatables.size(); i++){
            Simulatable obj = simulatables.get(i);
            obj.init();
        }
        Intersection intersection = scenario.getIntersection();
        roads = intersection.getRoads();
        flags = new int[roads.size()];
        for(int i = 0; i < roads.size(); i++){
            flags[i] = 0;
            waitinglist.add(new ArrayList<>());
        }
        window.start();
    }

    private void cleanup() {
        ArrayList<Simulatable> list1 = scenario.getSimulatables();
        ArrayList<Animatable> list2 = scenario.getAnimatables();
        ArrayList<Object> trash = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++){
            Simulatable o = list1.get(i);
            if (o instanceof Vehicle && ((Vehicle)o).getRunState() == Vehicle.STATE_OUT_OF_SCENE){
                trash.add(o);
            }
        }

        for (int i = 0; i < trash.size(); i++){
            list1.remove((Simulatable) trash.get(i));
            list2.remove((Animatable) trash.get(i));
        }
        trash.clear();
    }

    public double calculateAverageTime(){
        ArrayList<Object> list = scenario.getComponents();
        int count1 = 0;
        int count2 = 0;
        int nearmisses1 = 0;
        int nearmisses2 = 0;
        int runreds1 = 0;
        int runreds2 = 0;
        int count = 0;
        int time1 = 0;
        int time2 = 0;
        int time = 0;
        for(Object o : list){
            if (o instanceof Vehicle){
            	if (((Vehicle)o).getRoad()==roads.get(0)) {
            		count1++;
            		time1 += ((Vehicle)o).getAverageWaitingTime();
            		if (((Vehicle)o).getRunred()) runreds1 += 1;
            		if (((Vehicle)o).getNearmiss()) nearmisses1 += 1;
            	}
            	else if (((Vehicle)o).getRoad()==roads.get(1)) {
                    count2++;
                    time2 += ((Vehicle)o).getAverageWaitingTime();
                    if (((Vehicle)o).getRunred()) runreds2 += 1;
            		if (((Vehicle)o).getNearmiss()) nearmisses2 += 1;
            	}
            }
        }
        System.out.println("Times a vehicle had to brake aprubptly on Road 1 (WB) " + runreds1);
        System.out.println("Times a vehicle had to brake aprubptly on Road 2 (NB) " + runreds2);
        System.out.println("Near misses on Road 1 (WB) " + nearmisses1);
        System.out.println("Near misses on Road 2 (NB) " + nearmisses2);
        System.out.println("Time 1 " + time1);
        System.out.println("Time 2 " + time2);
        System.out.println("Count 1 " + count1);
        System.out.println("Count 2 " + count2);
        avgdelay1 = (double)time1/count1;
        avgdelay2 = (double)time2/count2;
        avgnearmiss1 = (double)nearmisses1/count1;
        avgnearmiss2 = (double)nearmisses2/count2;
        avgrunred1 = (double)runreds1/count1;
        avgrunred2 = (double)runreds2/count2;
        System.out.println("Average run red lights on Road 1 " + avgrunred1);
        System.out.println("Average run red lights on Road 2 " + avgrunred2);
        System.out.println("Average near misses on Road 1 " + avgnearmiss1);
        System.out.println("Average near misses on Road 2 " + avgnearmiss2);
        time = time1 + time2;
        count = count1 + count2;
        if (count == 0) return 0;
        return (double)time/count;
    }

    public void plotGraph(){
        XYSeriesCollection dataset = new XYSeriesCollection();
        int pointer = 0;
        for (Road road : roads) {
            XYSeries series = new XYSeries(road.getId());
            ArrayList<Integer> data = waitinglist.get(pointer++);
            for (int i = 0; i < data.size(); i++){
                series.add(i, data.get(i));
            }
            if (pointer ==3) {
            	break;
            }
            dataset.addSeries(series);
        }
        JFreeChart chart = ChartFactory.createXYLineChart("Waiting Time analysis", "Time instance", "Waiting Vehicles", dataset, PlotOrientation.VERTICAL, true, false, true);
        ChartFrame frame = new ChartFrame("Algorithm Analysis", chart);
        frame.setBackground(Color.WHITE);
        frame.setVisible(true);
        frame.setSize(400, 350);
        
        // Setting Y-axis
        XYSeriesCollection dataset_ = new XYSeriesCollection();
        int pointer_ = 0;
        for (Road road : roads) {
            XYSeries series_ = new XYSeries(road.getId());
            ArrayList<Integer> data_ = waitinglist.get(pointer_++);
            for (int i = 0; i < data_.size(); i++){
                series_.add(i, data_.get(i));
            }
            if (pointer_ ==3) {
            	break;
            }
            dataset_.addSeries(series_);
        }
        JFreeChart chart_ = ChartFactory.createXYLineChart("Waiting Time analysis", "Time instance", "Waiting Vehicles", dataset_, PlotOrientation.VERTICAL, true, false, true);
        ChartFrame frame_ = new ChartFrame("Algorithm Analysis", chart_);
        frame_.setBackground(Color.WHITE);
        frame_.setVisible(true);
        frame_.setSize(400, 350);
        chart_.getXYPlot().getRangeAxis().setRange(0, 55);
        
        
        // Other plots

        XYSeriesCollection waitingdataset1 = new XYSeriesCollection();
        ArrayList<Object> list = scenario.getComponents();
        int p = 0;
        XYSeries series = new XYSeries("Waiting time of vehicles on Road 1");
        for (Object o : list){
            if (o instanceof Vehicle){
            	if (((Vehicle)o).getRoad()==roads.get(0)) {
            		series.add(p++, ((Vehicle)o).getAverageWaitingTime());
            	}
            }
        }
        waitingdataset1.addSeries(series);
        JFreeChart newchart = ChartFactory.createScatterPlot("Waiting Time Analysis", "Vehicle number", "Waiting time", waitingdataset1, PlotOrientation.VERTICAL, true, false, true);
        ChartFrame mFrame = new ChartFrame("Algorithm Analysis II", newchart);
        mFrame.setBackground(Color.WHITE);
        mFrame.setVisible(true);
        mFrame.setSize(400, 350);
        
        XYSeriesCollection waitingdataset2 = new XYSeriesCollection();
        ArrayList<Object> list1 = scenario.getComponents();
        int p1 = 0;
        XYSeries series1 = new XYSeries("Waiting time of vehicles on Road 2");
        for (Object o : list1){
            if (o instanceof Vehicle){
            	if (((Vehicle)o).getRoad()==roads.get(1))
                series1.add(p1++, ((Vehicle)o).getAverageWaitingTime());
            }
        }
        waitingdataset2.addSeries(series1);
        JFreeChart newchart1 = ChartFactory.createScatterPlot("Waiting Time Analysis", "Vehicle number", "Waiting time", waitingdataset2, PlotOrientation.VERTICAL, true, false, true);
        ChartFrame mFrame1 = new ChartFrame("Algorithm Analysis II", newchart1);
        mFrame1.setBackground(Color.WHITE);
        mFrame1.setVisible(true);
        mFrame1.setSize(400, 350);
        
        // Make histograms here
        
        //double[] binRanges = {0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
        double[] binEdges = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        List<Integer> vehr1_ = new ArrayList<>();
        List<Integer> vehr2_ = new ArrayList<>();
        // Histogram 1 (Road 1 WB)
        
        //HistogramDataset histoset1 = new HistogramDataset();
        ArrayList<Object> listh1 = scenario.getComponents();
        //int p = 0;
        //XYSeries hseries1 = new XYSeries("Waiting times of vehicles on Road 1");
        for (Object o : listh1){
            if (o instanceof Vehicle){
            	if (((Vehicle)o).getRoad()==roads.get(0)) {
            		//hseries1.add(p++, ((Vehicle)o).getAverageWaitingTime());
            		vehr1_.add(((Vehicle)o).getAverageWaitingTime());
            	}
            }
        }
        int[] veh_r1 = vehr1_.stream().mapToInt(i->i).toArray();
        double[] vehr1 = Arrays.stream(veh_r1).asDoubleStream().toArray();
        
//        histoset1.addSeries("Waiting Time", vehr1, binEdges.length);
//        JFreeChart histochart1 = ChartFactory.createHistogram("Waiting Times for Vehicles", "Waiting time (in secs)", "Amount of vehicles", histoset1, PlotOrientation.VERTICAL, true, false, true);
//        ChartFrame hFrame1 = new ChartFrame("Algorithm Analysis III", histochart1);
//        hFrame1.setBackground(Color.WHITE);
//        hFrame1.setVisible(true);
//        hFrame1.setSize(400, 350);
//        histochart1.getXYPlot().getRangeAxis().setRange(0, 60);
//        XYPlot plot = histochart1.getXYPlot();
//        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
//        xAxis.setRange(0, 100); // Set the desired range for the X-axis
//        xAxis.setTickUnit(new NumberTickUnit(10));
        
        // Bar chart for histogram 1
        CategoryDataset barset1 = createDataset("Waiting Time on Road 1", vehr1, binEdges);
        
        JFreeChart bchart1 = ChartFactory.createBarChart(
                "Waiting Time Histogram",
                "Waiting Time (in secs)",
                "Amount of vehicles",
                barset1
        );
        ChartFrame bFrame1 = new ChartFrame("Algorithm Analysis III", bchart1);
        bFrame1.setBackground(Color.WHITE);
        bFrame1.setVisible(true);
        bFrame1.setSize(400, 350);

        CategoryPlot bplot1 = bchart1.getCategoryPlot();
        CategoryAxis bxAxis1 = bplot1.getDomainAxis();
        // Adjust the space between categories
        bxAxis1.setCategoryMargin(0);
        NumberAxis byAxis1 = (NumberAxis) bplot1.getRangeAxis();
        byAxis1.setTickUnit(new NumberTickUnit(1));
        byAxis1.setUpperBound(100);
        
        // Limit 2
        
        CategoryDataset barset1_2 = createDataset("Waiting Time on Road 1", vehr1, binEdges);
        
        JFreeChart bchart1_2 = ChartFactory.createBarChart(
                "Waiting Time Histogram",
                "Waiting Time (in secs)",
                "Amount of vehicles",
                barset1_2
        );
        ChartFrame bFrame1_2 = new ChartFrame("Algorithm Analysis III", bchart1_2);
        bFrame1_2.setBackground(Color.WHITE);
        bFrame1_2.setVisible(true);
        bFrame1_2.setSize(400, 350);

        CategoryPlot bplot1_2 = bchart1_2.getCategoryPlot();
        CategoryAxis bxAxis1_2 = bplot1_2.getDomainAxis();
        // Adjust the space between categories
        bxAxis1_2.setCategoryMargin(0);
        NumberAxis byAxis1_2 = (NumberAxis) bplot1_2.getRangeAxis();
        byAxis1_2.setTickUnit(new NumberTickUnit(1));
        byAxis1_2.setUpperBound(60);
        
        // Limit 3
        CategoryDataset barset1_3 = createDataset("Waiting Time on Road 1", vehr1, binEdges);
        
        JFreeChart bchart1_3 = ChartFactory.createBarChart(
                "Waiting Time Histogram",
                "Waiting Time (in secs)",
                "Amount of vehicles",
                barset1_3
        );
        ChartFrame bFrame1_3 = new ChartFrame("Algorithm Analysis III", bchart1_3);
        bFrame1_3.setBackground(Color.WHITE);
        bFrame1_3.setVisible(true);
        bFrame1_3.setSize(400, 350);

        CategoryPlot bplot1_3 = bchart1_3.getCategoryPlot();
        CategoryAxis bxAxis1_3 = bplot1_3.getDomainAxis();
        // Adjust the space between categories
        bxAxis1_3.setCategoryMargin(0);
        NumberAxis byAxis1_3 = (NumberAxis) bplot1_3.getRangeAxis();
        byAxis1_3.setTickUnit(new NumberTickUnit(1));
        byAxis1_3.setUpperBound(80);
        
        // No Limit
        CategoryDataset barset1_4 = createDataset("Waiting Time on Road 1", vehr1, binEdges);
        
        JFreeChart bchart1_4 = ChartFactory.createBarChart(
                "Waiting Time Histogram",
                "Waiting Time (in secs)",
                "Amount of vehicles",
                barset1_4
        );
        ChartFrame bFrame1_4 = new ChartFrame("Algorithm Analysis III", bchart1_4);
        bFrame1_4.setBackground(Color.WHITE);
        bFrame1_4.setVisible(true);
        bFrame1_4.setSize(400, 350);
        
        
        // Histogram 2 (Road 2 NB)
        ArrayList<Object> listh2 = scenario.getComponents();
        for (Object o : listh2){
            if (o instanceof Vehicle){
            	if (((Vehicle)o).getRoad()==roads.get(1)) {
            		vehr2_.add(((Vehicle)o).getAverageWaitingTime());
            	}
            }
        }
        int[] veh_r2 = vehr2_.stream().mapToInt(i->i).toArray();
        double[] vehr2 = Arrays.stream(veh_r2).asDoubleStream().toArray();
        
        CategoryDataset barset2 = createDataset("Waiting Time on Road 2", vehr2, binEdges);
        
        JFreeChart bchart2 = ChartFactory.createBarChart(
                "Waiting Time Histogram",
                "Waiting Time (in secs)",
                "Amount of vehicles",
                barset2
        );
        ChartFrame bFrame2 = new ChartFrame("Algorithm Analysis III", bchart2);
        bFrame2.setBackground(Color.WHITE);
        bFrame2.setVisible(true);
        bFrame2.setSize(400, 350);

        CategoryPlot bplot2 = bchart2.getCategoryPlot();
        CategoryAxis bxAxis2 = bplot2.getDomainAxis();
        // Adjust the space between categories
        bxAxis2.setCategoryMargin(0);
        NumberAxis byAxis2 = (NumberAxis) bplot2.getRangeAxis();
        byAxis2.setTickUnit(new NumberTickUnit(1));
        byAxis2.setUpperBound(60);
        
        // Limit 2
        CategoryDataset barset2_1 = createDataset("Waiting Time on Road 2", vehr2, binEdges);
        
        JFreeChart bchart2_1 = ChartFactory.createBarChart(
                "Waiting Time Histogram",
                "Waiting Time (in secs)",
                "Amount of vehicles",
                barset2_1
        );
        ChartFrame bFrame2_1 = new ChartFrame("Algorithm Analysis III", bchart2_1);
        bFrame2_1.setBackground(Color.WHITE);
        bFrame2_1.setVisible(true);
        bFrame2_1.setSize(400, 350);

        CategoryPlot bplot2_1 = bchart2_1.getCategoryPlot();
        CategoryAxis bxAxis2_1 = bplot2_1.getDomainAxis();
        // Adjust the space between categories
        bxAxis2_1.setCategoryMargin(0);
        NumberAxis byAxis2_1 = (NumberAxis) bplot2_1.getRangeAxis();
        byAxis2_1.setTickUnit(new NumberTickUnit(1));
        byAxis2_1.setUpperBound(50);
        
        // Limit 3
        CategoryDataset barset2_2 = createDataset("Waiting Time on Road 2", vehr2, binEdges);
        
        JFreeChart bchart2_2 = ChartFactory.createBarChart(
                "Waiting Time Histogram",
                "Waiting Time (in secs)",
                "Amount of vehicles",
                barset2_2
        );
        ChartFrame bFrame2_2 = new ChartFrame("Algorithm Analysis III", bchart2_2);
        bFrame2_2.setBackground(Color.WHITE);
        bFrame2_2.setVisible(true);
        bFrame2_2.setSize(400, 350);

        CategoryPlot bplot2_2 = bchart2_2.getCategoryPlot();
        CategoryAxis bxAxis2_2 = bplot2_2.getDomainAxis();
        // Adjust the space between categories
        bxAxis2_2.setCategoryMargin(0);
        NumberAxis byAxis2_2 = (NumberAxis) bplot2_2.getRangeAxis();
        byAxis2_2.setTickUnit(new NumberTickUnit(1));
        byAxis2_2.setUpperBound(40);
        
        
        // No limit
        CategoryDataset barset2_3 = createDataset("Waiting Time on Road 2", vehr2, binEdges);
        
        JFreeChart bchart2_3 = ChartFactory.createBarChart(
                "Waiting Time Histogram",
                "Waiting Time (in secs)",
                "Amount of vehicles",
                barset2_3
        );
        ChartFrame bFrame2_3 = new ChartFrame("Algorithm Analysis III", bchart2_3);
        bFrame2_3.setBackground(Color.WHITE);
        bFrame2_3.setVisible(true);
        bFrame2_3.setSize(400, 350);

        // End of Histograms
        
        XYSeriesCollection dataspeed = new XYSeriesCollection();

        XYSeries series2 = new XYSeries("Vehicle Speed");

        for (int i = 0; i < roads.get(1).speedprofile.size(); i++){
            series2.add(i, roads.get(1).speedprofile.get(i));
        }
        dataspeed.addSeries(series2);

        JFreeChart chart2 = ChartFactory.createXYLineChart("Speed analysis", "Time instance", "Speed", dataspeed, PlotOrientation.VERTICAL, true, false, true);
        ChartFrame frame2 = new ChartFrame("Speed Analysis", chart2);
        frame2.setBackground(Color.WHITE);
        frame2.setVisible(true);
        frame2.setSize(400, 350);
    }
}
