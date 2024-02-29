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
        int count = 0;
        int time1 = 0;
        int time2 = 0;
        int time = 0;
        for(Object o : list){
            if (o instanceof Vehicle){
            	if (((Vehicle)o).getRoad()==roads.get(0)) {
            		count1++;
            		time1 += ((Vehicle)o).getAverageWaitingTime()/30;
            	}
            	else {
                    count2++;
                    time2 += ((Vehicle)o).getAverageWaitingTime()/30;
            	}
            }
        }
        System.out.println("Time 1 " + time1);
        System.out.println("Time 2 " + time2);
        System.out.println("Count 1 " + count1);
        System.out.println("Count 2 " + count2);
        avgdelay1 = (double)time1/count1;
        avgdelay2 = (double)time2/count2;
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
            dataset.addSeries(series);
        }
        JFreeChart chart = ChartFactory.createXYLineChart("Waiting Time analysis", "Time instance", "Waiting Vehicles", dataset, PlotOrientation.VERTICAL, true, false, true);
        ChartFrame frame = new ChartFrame("Algorithm Analysis", chart);
        frame.setBackground(Color.WHITE);
        frame.setVisible(true);
        frame.setSize(400, 350);

        XYSeriesCollection waitingdataset1 = new XYSeriesCollection();
        ArrayList<Object> list = scenario.getComponents();
        int p = 0;
        XYSeries series = new XYSeries("Waiting time of vehicles on Road 1");
        for (Object o : list){
            if (o instanceof Vehicle){
            	if (((Vehicle)o).getRoad()==roads.get(0)) {
            		series.add(p++, ((Vehicle)o).getAverageWaitingTime()/30);
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
                series1.add(p1++, ((Vehicle)o).getAverageWaitingTime()/30);
            }
        }
        waitingdataset2.addSeries(series1);
        JFreeChart newchart1 = ChartFactory.createScatterPlot("Waiting Time Analysis", "Vehicle number", "Waiting time", waitingdataset2, PlotOrientation.VERTICAL, true, false, true);
        ChartFrame mFrame1 = new ChartFrame("Algorithm Analysis II", newchart1);
        mFrame1.setBackground(Color.WHITE);
        mFrame1.setVisible(true);
        mFrame1.setSize(400, 350);
    }
}
