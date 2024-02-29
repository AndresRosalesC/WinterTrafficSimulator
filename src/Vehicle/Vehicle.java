package Vehicle;

import Animation.Animatable;
import Road.Road;
import SimulationToolbox.Simulatable;
import res.SimulationGraphicConfig;
import javax.imageio.ImageIO;
import java.awt.*;

public class Vehicle implements Animatable, Simulatable {

    public static final int STATE_RUNNING = 0;
    public static final int STATE_OUT_OF_SCENE = 2;
    public static final int STATE_WAITING = 1;

    private String id;
    private int runState;
    private int speed;
    private int fspeed;
    private int posX;
    private int posY;
    private Road road;
    private int waitingTime;
    private int waitingInstances;
    private int orderID;
    private double accel;
    private int prevspeed;
    private double prevaccel;
    private int prevPosX;
    private int prevPosY;

    public Vehicle(String id, Road road) {
        this.id = id;
        this.speed = 30;
        this.fspeed = 30;
        this.road = road;
        this.waitingTime = 0;
        this.waitingInstances = 0;
        this.posX = 0;
        this.posY = 0;
        this.orderID = 0;
        this.accel = 0;
        this.prevaccel = 0;
        this.prevspeed= 30;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY){
        this.posY = posY;
    }

    private void moveAhead(){
        switch (this.getFacing()){
            case Road.DIRECTION_NORTH:
            	this.prevPosY = posY;
                this.posY--;
                break;

            case Road.DIRECTION_SOUTH:
            	this.prevPosY = posY;
                this.posY++;
                break;

            case Road.DIRECTION_EAST:
            	this.prevPosX = posX;
                this.posX++;
                break;

            case Road.DIRECTION_WEST:
            	this.prevPosX = posX;
                this.posX--;
                break;
        }
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D canvas = (Graphics2D) graphics;
        Image vehicle_image = null;
        try{
            switch (this.getFacing()){
                case Road.DIRECTION_NORTH:
                    vehicle_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.CAR_NORTH));
                    canvas.drawImage(vehicle_image, this.posX, this.posY, SimulationGraphicConfig.VEHICLE_HEIGHT, SimulationGraphicConfig.VEHICLE_WIDTH, null);
                    break;

                case Road.DIRECTION_SOUTH:
                    vehicle_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.CAR_SOUTH));
                    canvas.drawImage(vehicle_image, this.posX, this.posY, SimulationGraphicConfig.VEHICLE_HEIGHT, SimulationGraphicConfig.VEHICLE_WIDTH, null);
                    break;

                case Road.DIRECTION_EAST:
                    vehicle_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.CAR_EAST));
                    canvas.drawImage(vehicle_image, this.posX, this.posY, SimulationGraphicConfig.VEHICLE_WIDTH, SimulationGraphicConfig.VEHICLE_HEIGHT, null);
                    break;

                case Road.DIRECTION_WEST:
                    vehicle_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.CAR_WEST));
                    canvas.drawImage(vehicle_image, this.posX, this.posY, SimulationGraphicConfig.VEHICLE_WIDTH, SimulationGraphicConfig.VEHICLE_HEIGHT, null);
                    break;
            }
        }catch (Exception e){
            canvas.drawRect(this.posX, this.posY, SimulationGraphicConfig.VEHICLE_WIDTH, SimulationGraphicConfig.VEHICLE_HEIGHT);
        }
    }

    @Override
    public void init() {
        this.setRunState(STATE_RUNNING);
        this.speed = 30;
    }

    @Override
    public void simulate() {
        if (this.getRunState() == Vehicle.STATE_OUT_OF_SCENE) return;
        if (this.posX < 0 - SimulationGraphicConfig.VEHICLE_WIDTH || this.posX > SimulationGraphicConfig.BOUNDARY_X) this.setRunState(Vehicle.STATE_OUT_OF_SCENE);
        if (this.posY < 0 - SimulationGraphicConfig.VEHICLE_WIDTH || this.posY > SimulationGraphicConfig.BOUNDARY_Y) this.setRunState(Vehicle.STATE_OUT_OF_SCENE);
        int i = 0;
        road.feedback(this);
        while (i < speed){
            if (this.runState == Vehicle.STATE_RUNNING) {
                moveAhead();
            }
            else if (this.runState == Vehicle.STATE_WAITING){
                waitingTime++;
            }
            road.check(this);
            i++;
        }
    }

    public int getSafePosX(){
        switch (this.getFacing()){
            case Road.DIRECTION_NORTH:
                return posX;

            case Road.DIRECTION_SOUTH:
                return posX;

            case Road.DIRECTION_WEST:
                return posX + SimulationGraphicConfig.VEHICLE_WIDTH + 10;

            case Road.DIRECTION_EAST:
                return posX - SimulationGraphicConfig.VEHICLE_WIDTH - 10;
        }
        return posX;
    }

    public int getSafePosY(){
        switch (this.getFacing()){
            case Road.DIRECTION_NORTH:
                return posY + SimulationGraphicConfig.VEHICLE_WIDTH + 10;

            case Road.DIRECTION_SOUTH:
                return posY - SimulationGraphicConfig.VEHICLE_WIDTH - 10;

            case Road.DIRECTION_WEST:
                return posY;

            case Road.DIRECTION_EAST:
                return posY;
        }
        return posY;
    }

    public int getRunState() {
        return runState;
    }

    public void setRunState(int runState) {
        this.runState = runState;
    }

    public void waitUntilGreen(){
        this.runState = Vehicle.STATE_WAITING;
        waitingInstances++;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void goUntilRed(){
        this.runState = Vehicle.STATE_RUNNING;
    }

    public String getId() {
        return id;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getWaitingInstances() {
        return waitingInstances;
    }

    public int getAverageWaitingTime() {
        return waitingTime;
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }
    
    public void setorderID(int orderID) {
        this.orderID = orderID;
    }
    
    public int getorderID() {
        return orderID;
    }

    public int getFacing(){
        return this.road.getDirection();
    }
    
    public void setSpeed(int speed){
    	this.prevspeed = this.speed;
        this.speed = speed;
    }
    
    public int getSpeed(){
        return this.speed;
    }
    
    public int getPrevSpeed(){
        return this.prevspeed;
    }
    
    public double getAccel(){
        return this.accel;
    }
    
    public double getPrevAccel(){
        return this.prevaccel;
    }
    
    public void setAccel(double accel){
    	this.prevaccel = this.accel;
        this.accel = accel;
    }

    public int getMobilePos(){
        switch (this.getFacing()){
            case Road.DIRECTION_NORTH:
                return posY;

            case Road.DIRECTION_WEST:
                return posX;

            case Road.DIRECTION_SOUTH:
                return posY;

            case Road.DIRECTION_EAST:
                return posX;
        }
        return 0;
    }
    
    public int getPrevMobilePos(){
        switch (this.getFacing()){
            case Road.DIRECTION_NORTH:
                return prevPosY;

            case Road.DIRECTION_WEST:
                return prevPosX;

            case Road.DIRECTION_SOUTH:
                return prevPosY;

            case Road.DIRECTION_EAST:
                return prevPosX;
        }
        return 0;
    }
}
