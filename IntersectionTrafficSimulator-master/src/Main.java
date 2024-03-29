import Intersection.Intersection;
import Road.OneWayRoad;
import Road.Road;
import SignalController.SignalController;
import SimulationToolbox.Scenario;
import SimulationToolbox.ScenarioHandler;
import TrafficSignal.TrafficSignal;
import res.SimulationGraphicConfig;

import java.util.ArrayList;

public class Main extends Scenario {

    public static void main(String[] args) {
        Scenario scenario = new Main();
        ScenarioHandler handler = new ScenarioHandler(scenario, 100);
        handler.runSimulation();
    }

    @Override
    public void buildScenario() {
        Road road = new OneWayRoad("Road1", Road.DIRECTION_WEST);
        Road road1 = new OneWayRoad("Road2", Road.DIRECTION_NORTH);
        Road road2 = new OneWayRoad("Road3", Road.DIRECTION_EAST);
        Road road3 = new OneWayRoad("Road4", Road.DIRECTION_SOUTH);
        road.setTrafficIntensity(Road.INTENSITY_HIGH);
        road1.setTrafficIntensity(Road.INTENSITY_LOW);
        road2.setTrafficIntensity(Road.INTENSITY_HIGH);
        road3.setTrafficIntensity(Road.INTENSITY_LOW);
        Intersection intersection = new Intersection("Intersection1");
        intersection.addRoad(road);
        intersection.addRoad(road1);
        intersection.addRoad(road2);
        intersection.addRoad(road3);
        TrafficSignal signal = new TrafficSignal(road);
        TrafficSignal signal1 = new TrafficSignal(road1);
        TrafficSignal signal2 = new TrafficSignal(road2);
        TrafficSignal signal3 = new TrafficSignal(road3);
        // Comment signal controller type not to be used, use adaptive only in mode 0 for optimal performance
        SimpleSignalController controller = new SimpleSignalController(intersection);
        //AdaptiveSignalController controller = new AdaptiveSignalController(intersection);
        intersection.setSignalController(controller);
        addComponent(road);
        addComponent(road1);
        addComponent(road2);
        addComponent(road3);
        addComponent(signal);
        addComponent(signal1);
        addComponent(signal2);
        addComponent(signal3);
        addComponent(intersection);
        addComponent(controller);
    }

    public class SimpleSignalController extends SignalController{
        int flag;
        int timeout;
        boolean toggle;
        ArrayList<TrafficSignal> signals;

        public SimpleSignalController(Intersection intersection) {
            flag = 0;
            timeout = 18; // previously 35
            signals = new ArrayList<TrafficSignal>();
            this.setIntersection(intersection);
            if (intersection != null) {
                ArrayList<Road> list = this.intersection.getRoads();
                for (Road road : list) {
                    signals.add(road.getTrafficSignal());
                }
                toggle = false;
            }
        }

        @Override
        public void init() {
            if (getIntersection() == null) return;
            signals.get(0).setGreen();
            signals.get(2).setGreen();
        }

        @Override
        public void simulate() {
            if (getIntersection() == null) return;
            flag++;
            if (flag == timeout){
            	// 20 + 7 + 8 = 35 red time
            	if (timeout == 18) {
            		timeout = 9;
            		if (!toggle) {
            			signals.get(0).toggleState();
                        signals.get(2).toggleState();
            		}
            		else {
            			signals.get(1).toggleState();
                        signals.get(3).toggleState();
            		}
            	}
            	else if (timeout == 9) {
            		timeout = 8;
            		if (!toggle) {
            			signals.get(0).toggleState();
                        signals.get(2).toggleState();
            		}
            		else {
            			signals.get(1).toggleState();
                        signals.get(3).toggleState();
            		}
            	}
            	else if (timeout == 8) {
            		timeout = 18;
            		for (TrafficSignal signal : signals) signal.toggleState();
            		toggle = !toggle;
            	}
            	flag = 0;
            }
        }
//        public void simulate() {
//            if (getIntersection() == null) return;
//            flag++;
//            if (flag == timeout){
//                for (TrafficSignal signal : signals) signal.toggleState();
//                flag = 0;
//                toggle = !toggle;
//                if (toggle) timeout = 8;
//                else timeout = 35;
//            }
//        }
    }

    public class AdaptiveSignalController extends SignalController{

        private int flag = 0;
        private int timeout = 30;
        private final int fixed_timeout = 6;
        private int next_timeout = 0;
        private TrafficSignal signal1;
        private TrafficSignal signal2;
        private TrafficSignal signal3;
        private TrafficSignal signal4;
        private Road road1;
        private Road road2;
        private Road road3;
        private Road road4;
        private boolean decision_phase;

        AdaptiveSignalController(Intersection intersection){
            setIntersection(intersection);
            ArrayList<Road> roads = intersection.getRoads();
            road1 = roads.get(0);
            road2 = roads.get(1);
            road3 = roads.get(2);
            road4 = roads.get(3);
            signal1 = road1.getTrafficSignal();
            signal2 = road2.getTrafficSignal();
            signal3 = road3.getTrafficSignal();
            signal4 = road4.getTrafficSignal();
            decision_phase = false;
        }

        @Override
        public void init() {
            signal1.setGreen();
            signal2.setRed();
            signal3.setGreen();
            signal4.setRed();
        }

        @Override
        public void simulate() {
            flag++;
            if (flag >= timeout){
                if (!decision_phase) {
                    next_timeout = signal1.getSignalState() == TrafficSignal.STATE_RED ? setGreenInterval(road1)+3 : setGreenInterval(road2)+3;
                }
                signal1.toggleState();
                signal2.toggleState();
                signal3.toggleState();
                signal4.toggleState();
                flag = 0;
                decision_phase = !decision_phase;
                if (decision_phase) timeout = fixed_timeout;
                else timeout = next_timeout;
            }
        }

        private int setGreenInterval(Road road){
            int count = road.getWaitingCount();
            return (int) (count * SimulationGraphicConfig.VEHICLE_LENGTH)/60;
        }
    }
}
