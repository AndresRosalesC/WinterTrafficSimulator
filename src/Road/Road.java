package Road;

import Animation.Animatable;
import SimulationToolbox.Scenario;
import TrafficSignal.TrafficSignal;
import Vehicle.Vehicle;
import res.SimulationGraphicConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import java.util.List;
import java.util.Optional;

public abstract class Road implements Animatable {

    public static final int DIRECTION_NORTH = 0;
    public static final int DIRECTION_SOUTH = 1;
    public static final int DIRECTION_EAST = 2;
    public static final int DIRECTION_WEST = 3;
    public static final int INTENSITY_LOW = 0;
    public static final int INTENSITY_MODERATE = 1;
    public static final int INTENSITY_HIGH = 2;

    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;
    
    public static final int alpha = 50;
    public static final int m = 0;
    public static final int l = 1;

    protected String id;
    protected int orientation;
    protected int direction;
    protected int trafficIntensity;
    protected int posX;
    protected int posY;
    protected int dirID[];
    protected int vid;
    protected double paccel;
    protected double pspeed;
    protected boolean lane;
    protected boolean NSEW[];
    protected TrafficSignal trafficSignal;

    protected int[][] roadGrid;

    protected ArrayList<Vehicle> vehicles;
    protected HashMap<Integer, Integer> road_data;

    public Road(String id, int direction) {
        this.direction = direction;
        this.id = id;
        if (direction == Road.DIRECTION_NORTH | direction == Road.DIRECTION_SOUTH) {
            orientation = Road.ORIENTATION_VERTICAL;
            posY = 0;
            posX = SimulationGraphicConfig.VERTICAL_ROAD_X_POS;
        } else if (direction == Road.DIRECTION_EAST | direction == Road.DIRECTION_WEST) {
            orientation = Road.ORIENTATION_HORIZONTAL;
            posX = 0;
            posY = SimulationGraphicConfig.HORIZONTAL_ROAD_Y_POS;
        }
        roadGrid = new int[SimulationGraphicConfig.BOUNDARY_X*2][SimulationGraphicConfig.BOUNDARY_Y*2];
        this.trafficIntensity = Road.INTENSITY_MODERATE;
        vehicles = new ArrayList<>();
        lane = false;
        road_data = new HashMap<>();
        NSEW = new boolean[]{false, false, false, false};
        dirID = new int[]{0, 5000, 10000, 15000, 20000, 25000, 30000, 35000};
        vid = 0;
    }

    public String getId(){
        return this.id;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getDirection() {
        return direction;
    }

    public void addVehicle(Vehicle vehicle){
        this.vehicles.add(vehicle);
    }

    public int getTrafficIntensity() {
        return trafficIntensity;
    }

    public void setTrafficIntensity(int trafficIntensity) {
        this.trafficIntensity = trafficIntensity;
    }
    
    public int findIndexById(ArrayList<Vehicle> objects, int id) {
    	int i = 0;
    	for (Object o : objects) {
    		if (((Vehicle)o).getorderID()==id) {
                return i;
            }
            i++;
        }
    	return -1;
    }
    
    public double calcSpeed(int pspeed, double paccel) {
    	double spee =  pspeed + paccel;
    	return spee;
    	
    }
    
    public double calcAccel(int pspeed1, int pspeed2, int ppos1, int ppos2) {
    	double acc = ((alpha * Math.pow(pspeed2, m))/Math.pow(Math.abs(ppos1-ppos2), l))*(pspeed1-pspeed2);
    	return acc;
    }

    public void check(Vehicle vehicle){
        if (this.trafficSignal != null){
            TrafficSignal signal = this.trafficSignal;
            if (vehicle.getMobilePos() == signal.getRestrictorPos()){
                if (signal.getSignalState() != TrafficSignal.STATE_GREEN && vehicle.getRunState() == Vehicle.STATE_RUNNING){
//                	vehicle.setSpeed(0);
//                	vehicle.setAccel(0);
                    vehicle.waitUntilGreen();
                    roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 1;
                    return;
                }
                else if (signal.getSignalState() == TrafficSignal.STATE_GREEN && vehicle.getRunState() == Vehicle.STATE_WAITING){
//                	paccel = calcAccel(30, 0, 400, 0);
//                	vehicle.setAccel(paccel);
//                	vehicle.setSpeed(10);
                    vehicle.goUntilRed();
                    roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 0;
                    return;
                }
                return;
            }

            if (vehicle.getPosX() < 0 || vehicle.getPosY() < 0) return;
            if (roadGrid[vehicle.getPosX()][vehicle.getPosY()] == 1){
                vehicle.waitUntilGreen();
                roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 1;
                return;
            }
            else {
                vehicle.goUntilRed();
                roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 0;
            }
            
          int index = findIndexById(this.vehicles, vehicle.getorderID()-1);
          if (index != -1 && vehicles.get(index).getRunState() != Vehicle.STATE_OUT_OF_SCENE) {
        	  if (Math.abs(vehicles.get(index).getPrevMobilePos()-vehicle.getPrevMobilePos()) < SimulationGraphicConfig.VEHICLE_LENGTH + 10) {
                  vehicle.waitUntilGreen();
                  roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 1;
        	  }
              else {
                  vehicle.goUntilRed();
                  roadGrid[vehicle.getSafePosX()][vehicle.getSafePosY()] = 0;
              }
        	  
          }
        }
    }
    
    public void feedback(Vehicle vehicle) {
        int index = findIndexById(this.vehicles, vehicle.getorderID()-1);
        if (vehicle.getRunState()==Vehicle.STATE_WAITING) {
        	vehicle.setSpeed(0);
        	vehicle.setAccel(0);
        }
        
        if (index != -1 && vehicles.get(index).getRunState() != Vehicle.STATE_OUT_OF_SCENE) {
        	pspeed = calcSpeed(vehicle.getPrevSpeed(),vehicle.getPrevAccel());
        	if (Math.abs(vehicles.get(index).getPrevMobilePos()-vehicle.getPrevMobilePos()) < 250) {
        		paccel = calcAccel(vehicles.get(index).getPrevSpeed(),vehicle.getPrevSpeed(),vehicles.get(index).getPrevMobilePos(),vehicle.getPrevMobilePos());               
        	}
        	else {
        		paccel = calcAccel(30,vehicle.getPrevSpeed(),300,0);
        	}
            vehicle.setAccel(paccel);
            if (pspeed > vehicles.get(index).getPrevSpeed()) {
            	pspeed = vehicles.get(index).getPrevSpeed();
            }
            if (pspeed < 10 && Math.abs(vehicles.get(index).getPrevMobilePos()-vehicle.getPrevMobilePos()) > SimulationGraphicConfig.VEHICLE_LENGTH*3) {
            	pspeed = 10;
            }
            else if (pspeed < 5 && Math.abs(vehicles.get(index).getPrevMobilePos()-vehicle.getPrevMobilePos()) < SimulationGraphicConfig.VEHICLE_LENGTH*3) {
            	pspeed = 5;
            }
            vehicle.setSpeed((int)Math.round(pspeed));
        	
            if (Math.abs(vehicles.get(index).getPrevMobilePos()-vehicle.getPrevMobilePos()) < SimulationGraphicConfig.VEHICLE_LENGTH + 10) {
            	vehicle.setSpeed(0);
            	vehicle.setAccel(0);
            }
        } 
        else {
        	paccel = calcAccel(30,vehicle.getPrevSpeed(),300,0);
        	pspeed = calcSpeed(vehicle.getPrevSpeed(),vehicle.getPrevAccel());
        	vehicle.setAccel(paccel);
        	vehicle.setSpeed((int)Math.round(pspeed));
        }
    }

    public void setTrafficSignal(TrafficSignal trafficSignal) {
        this.trafficSignal = trafficSignal;
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D canvas = (Graphics2D) graphics;
        Image roadImage = null;
        try{
            switch (this.getOrientation()){
                case Road.ORIENTATION_HORIZONTAL:
                    roadImage = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.ROAD_HORIZONTAL));
                    canvas.drawImage(roadImage, posX, posY, SimulationGraphicConfig.ROAD_HORIZONTAL_LENGTH, SimulationGraphicConfig.ROAD_WIDTH, null);
                    break;

                case Road.ORIENTATION_VERTICAL:
                    roadImage = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.ROAD_VERTICAL));
                    canvas.drawImage(roadImage, posX, posY, SimulationGraphicConfig.ROAD_WIDTH, SimulationGraphicConfig.ROAD_VERTICAL_LENGTH, null);
                    break;
            }
        }
        catch (Exception e){
            switch (this.getOrientation()){
                case Road.ORIENTATION_HORIZONTAL:
                    canvas.drawRect(posX, posY, SimulationGraphicConfig.ROAD_HORIZONTAL_LENGTH, SimulationGraphicConfig.ROAD_WIDTH);
                    break;

                case Road.ORIENTATION_VERTICAL:
                    canvas.drawRect(posX, posY, SimulationGraphicConfig.ROAD_WIDTH, SimulationGraphicConfig.ROAD_VERTICAL_LENGTH);
                    break;
            }
        }
    }

    public int getWaitingCount(){
        int count = 0;
        for (Vehicle vehicle : vehicles){
            if (vehicle.getRunState() == Vehicle.STATE_WAITING) count++;
        }
        return count;
    }

    public boolean newVehicle(int flag, Scenario scenario) {
        if (flag > getTrafficThreshold()){
            boolean add = new Random().nextBoolean();
            if (add){
                Vehicle vehicle = new Vehicle(UUID.randomUUID().toString(), this);
                addVehicle(vehicle);
                switch (vehicle.getFacing()){
                    case Road.DIRECTION_EAST:
                        vehicle.setPosX(SimulationGraphicConfig.VEHICLE_EAST_POS_X);
                        vehicle.setPosY(SimulationGraphicConfig.VEHICLE_EAST_POS_Y + getLaneOffset());
                        vid = getorderID(vehicle.getFacing());
                        vehicle.setorderID(vid);
                        break;

                    case Road.DIRECTION_WEST:
                        vehicle.setPosX(SimulationGraphicConfig.VEHICLE_WEST_POS_X);
                        vehicle.setPosY(SimulationGraphicConfig.VEHICLE_WEST_POS_Y + getLaneOffset());
                        vid = getorderID(vehicle.getFacing());
                        vehicle.setorderID(vid);
                        break;

                    case Road.DIRECTION_SOUTH:
                        vehicle.setPosX(SimulationGraphicConfig.VEHICLE_SOUTH_POS_X + getLaneOffset());
                        vehicle.setPosY(SimulationGraphicConfig.VEHICLE_SOUTH_POS_Y);
                        vid = getorderID(vehicle.getFacing());
                        vehicle.setorderID(vid);
                        break;

                    case Road.DIRECTION_NORTH:
                        vehicle.setPosX(SimulationGraphicConfig.VEHICLE_NORTH_POS_X + getLaneOffset());
                        vehicle.setPosY(SimulationGraphicConfig.VEHICLE_NORTH_POS_Y);
                        vid = getorderID(vehicle.getFacing());
                        vehicle.setorderID(vid);
                        break;
                }
                scenario.addComponent(vehicle);
                this.addVehicle(vehicle);
            }
            return add;
        }
        else return false;
    }

    protected int getLaneOffset(){
        lane = !lane;
        if (lane){
            return SimulationGraphicConfig.VEHICLE_HEIGHT + 4;
        }
        else return 0;
    }
    
    protected int getorderID(int direction){
    	int localid = 0;
         switch (direction){
         case Road.DIRECTION_EAST:
             NSEW[2] = !NSEW[2];
             if (NSEW[2]) {
            	 dirID[4]++;
            	 localid = dirID[4];
             }
             else {
            	 dirID[5]++;
            	 localid = dirID[5];
             }
             break;
		case Road.DIRECTION_WEST:
             NSEW[3] = !NSEW[3];
             if (NSEW[3]) {
            	 dirID[6]++;
            	 localid = dirID[6];
             }
             else {
            	 dirID[7]++;
            	 localid = dirID[7];
             }
             break;
		case Road.DIRECTION_SOUTH:
             NSEW[1] = !NSEW[1];
             if (NSEW[1]) {
            	 dirID[2]++;
            	 localid = dirID[2];
             }
             else {
            	 dirID[3]++;
            	 localid = dirID[3];
             }
             break;
		case Road.DIRECTION_NORTH:
             NSEW[0] = !NSEW[0];
             if (NSEW[0]) {
            	 dirID[0]++;
            	 localid = dirID[0];
             }
             else {
            	 dirID[1]++;
            	 localid = dirID[1];
             }
             break;
     }
		return localid;
    }


    public TrafficSignal getTrafficSignal() {
        return trafficSignal;
    }

    private int getTrafficThreshold(){
        switch (this.getTrafficIntensity()){
            case Road.INTENSITY_LOW:
                return SimulationGraphicConfig.LOW_TRAFFIC_THRESHOLD_INTERVAL;

            case Road.INTENSITY_MODERATE:
                return SimulationGraphicConfig.MODERATE_TRAFFIC_THRESHOLD_INTERVAL;

            case Road.INTENSITY_HIGH:
                return SimulationGraphicConfig.HIGH_TRAFFIC_THRESHOLD_INTERVAL;
        }
        return 3;
    }
}
