package TrafficSignal;

import Animation.Animatable;
import Road.Road;
import SignalController.SignalController;
import SimulationToolbox.ScenarioHandler;
import res.SimulationGraphicConfig;
import javax.imageio.ImageIO;
import java.awt.*;

public class TrafficSignal implements Animatable {
	
	private int mode = SimulationGraphicConfig.MODE;

    public static final int STATE_GREEN = 0;
    public static final int STATE_RED = 1;
    public static final int STATE_YELLOW = 2;
    public static final int STATE_GREEN2 = 3;

    private int stateNeedle;
    //private final int[] states = {STATE_RED, STATE_YELLOW, STATE_GREEN, STATE_YELLOW};
    private final int[] states = {STATE_RED, STATE_GREEN, STATE_GREEN2, STATE_YELLOW};
    private int state;
    private Road road;
    private int posX;
    private int posY;
    private int posX_w;
    private int posY_w;
    private int restrictorPos;
    private int WrestrictorPos;
    private SignalController controller;

    public TrafficSignal() {
        this.state = STATE_RED;
        stateNeedle = 0;
    }

    public TrafficSignal(Road road){
        this();
        this.setRoad(road);
    }

    public void setController(SignalController controller){
        this.controller = controller;
    }

    public int getSignalState(){
        return this.state;
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D canvas = (Graphics2D) graphics;
        Image signal_image;
        if (mode == 1 || mode == 3) {
        	Image warn_image;
	        try{
	            switch (this.getSignalState()){
	                case TrafficSignal.STATE_GREEN:
	                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_GREEN));
	                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
	                    warn_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.WARN_OFF));
	                    canvas.drawImage(warn_image, posX_w, posY_w, SimulationGraphicConfig.WARN_WIDTH, SimulationGraphicConfig.WARN_HEIGHT, null);
	                    break;
	                    
	                case TrafficSignal.STATE_GREEN2:
	                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_GREEN));
	                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
	                    warn_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.WARN_ON));
	                    canvas.drawImage(warn_image, posX_w, posY_w, SimulationGraphicConfig.WARN_WIDTH, SimulationGraphicConfig.WARN_HEIGHT, null);
	                    break;
	
	                case TrafficSignal.STATE_RED:
	                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_RED));
	                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
	                    warn_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.WARN_ON));
	                    canvas.drawImage(warn_image, posX_w, posY_w, SimulationGraphicConfig.WARN_WIDTH, SimulationGraphicConfig.WARN_HEIGHT, null);
	                    break;
	
	                case TrafficSignal.STATE_YELLOW:
	                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_YELLOW));
	                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
	                    warn_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.WARN_ON));
	                    canvas.drawImage(warn_image, posX_w, posY_w, SimulationGraphicConfig.WARN_WIDTH, SimulationGraphicConfig.WARN_HEIGHT, null);
	                    break;
	            }
	        }
	        catch (Exception e){
	            canvas.fillOval(this.posX, this.posY, 20, 20);
	        }
        }
        else {
        	try{
	            switch (this.getSignalState()){
	                case TrafficSignal.STATE_GREEN:
	                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_GREEN));
	                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
	                    break;
	                    
	                case TrafficSignal.STATE_GREEN2:
	                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_GREEN));
	                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
	                    break;
	
	                case TrafficSignal.STATE_RED:
	                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_RED));
	                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
	                    break;
	
	                case TrafficSignal.STATE_YELLOW:
	                    signal_image = ImageIO.read(getClass().getResourceAsStream(SimulationGraphicConfig.SIGNAL_YELLOW));
	                    canvas.drawImage(signal_image, posX, posY, SimulationGraphicConfig.SIGNAL_WIDTH, SimulationGraphicConfig.SIGNAL_HEIGHT, null);
	                    break;
	            }
	        }
	        catch (Exception e){
	            canvas.fillOval(this.posX, this.posY, 20, 20);
	        }
        }
    }

    public void setRoad(Road road){
        this.road = road;
        road.setTrafficSignal(this);
        switch (road.getDirection()){
            case Road.DIRECTION_NORTH:
                this.posX = SimulationGraphicConfig.SIGNAL_VERTICAL_POS_X_NORTH;
                this.posY = SimulationGraphicConfig.SIGNAL_VERTICAL_POS_Y_NORTH;
                this.posX_w = SimulationGraphicConfig.WARN_VERTICAL_POS_X_NORTH;
                this.posY_w = SimulationGraphicConfig.WARN_VERTICAL_POS_Y_NORTH;
                this.restrictorPos = SimulationGraphicConfig.SIGNAL_NORTH_RESTRICTOR;
                this.WrestrictorPos = SimulationGraphicConfig.WARN_NORTH_RESTRICTOR;
                break;

            case Road.DIRECTION_SOUTH:
                this.posX = SimulationGraphicConfig.SIGNAL_VERTICAL_POS_X_SOUTH;
                this.posY = SimulationGraphicConfig.SIGNAL_VERTICAL_POS_Y_SOUTH;
                this.posX_w = SimulationGraphicConfig.WARN_VERTICAL_POS_X_SOUTH;
                this.posY_w = SimulationGraphicConfig.WARN_VERTICAL_POS_Y_SOUTH;
                this.restrictorPos = SimulationGraphicConfig.SIGNAL_SOUTH_RESTRICTOR;
                this.WrestrictorPos = SimulationGraphicConfig.WARN_SOUTH_RESTRICTOR;
                break;

            case Road.DIRECTION_EAST:
                this.posX = SimulationGraphicConfig.SIGNAL_HORIZONTAL_POS_X_EAST;
                this.posY = SimulationGraphicConfig.SIGNAL_HORIZONTAL_POS_Y_EAST;
                this.posX_w = SimulationGraphicConfig.WARN_HORIZONTAL_POS_X_EAST;
                this.posY_w = SimulationGraphicConfig.WARN_HORIZONTAL_POS_Y_EAST;
                this.restrictorPos = SimulationGraphicConfig.SIGNAL_EAST_RESTRICTOR;
                this.WrestrictorPos = SimulationGraphicConfig.WARN_EAST_RESTRICTOR;
                break;

            case Road.DIRECTION_WEST:
                this.posX = SimulationGraphicConfig.SIGNAL_HORIZONTAL_POS_X_WEST;
                this.posY = SimulationGraphicConfig.SIGNAL_HORIZONTAL_POS_Y_WEST;
                this.posX_w = SimulationGraphicConfig.WARN_HORIZONTAL_POS_X_WEST;
                this.posY_w = SimulationGraphicConfig.WARN_HORIZONTAL_POS_Y_WEST;
                this.restrictorPos = SimulationGraphicConfig.SIGNAL_WEST_RESTRICTOR;
                this.WrestrictorPos = SimulationGraphicConfig.WARN_WEST_RESTRICTOR;
                break;
        }
    }

    public Road getRoad(){
        return this.road;
    }

    public int getRestrictorPos() {
        return restrictorPos;
    }
    
    public int getWRestrictorPos() {
        return WrestrictorPos;
    }

    public SignalController getController() {
        return controller;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setRestrictorPos(int restrictorPos) {
        this.restrictorPos = restrictorPos;
    }
    
    public int getNeedle() {
    	return this.stateNeedle;
    }

    public void toggleState(){
        this.stateNeedle = (stateNeedle + 1)%4;
        this.state = states[stateNeedle];
    }

    public void setGreen(){
        this.state = TrafficSignal.STATE_GREEN;
        this.stateNeedle = 1; // previously 2
    }

    public void setRed(){
        this.state = TrafficSignal.STATE_RED;
        this.stateNeedle = 0;
    }
}
