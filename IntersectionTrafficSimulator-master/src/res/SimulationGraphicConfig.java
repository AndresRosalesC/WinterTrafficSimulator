package res;

import java.awt.*;

public final class SimulationGraphicConfig {

    private static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    // Modes: (0: Summer no-prepare-to stop, 1: Summer prepare-to-stop, 2: Winter no prepare-to-stop, 3: Winter prepare-to-stop)
    // IMPORTANT: Set initial speed in Vehicle Class according to mode
    public static final int MODE = 0;

    public static final String CAR_NORTH = "/res/VehicleGraphics/red_car_north.png";
    public static final String CAR_SOUTH = "/res/VehicleGraphics/red_car_south.png";
    public static final String CAR_EAST = "/res/VehicleGraphics/red_car_east.png";
    public static final String CAR_WEST = "/res/VehicleGraphics/red_car_west.png";
    public static final String ROAD_HORIZONTAL = "/res/RoadGraphics/road_horizontal_s.jpg";
    public static final String ROAD_VERTICAL = "/res/RoadGraphics/road_vertical_s.jpg";
    public static final String SIGNAL_RED = "/res/SignalGraphics/signal_red.png";
    public static final String SIGNAL_GREEN = "/res/SignalGraphics/signal_green.png";
    public static final String SIGNAL_YELLOW = "/res/SignalGraphics/signal_yellow.png";
    public static final String INTERSECTION = "/res/IntersectionGraphics/intersection_s.png";
    public static final String WARN_OFF = "/res/SignalGraphics/ptostop-off.jpg";
    public static final String WARN_ON = "/res/SignalGraphics/ptostop-on.jpg";
    public static final int VEHICLE_LENGTH = 25;
    public static final int VEHICLE_WIDTH = 25;
    public static final int VEHICLE_HEIGHT = 19;
    public static final int ROAD_WIDTH = 50;
    public static final int ROAD_VERTICAL_LENGTH = (int) dimension.getHeight();
    public static final int ROAD_HORIZONTAL_LENGTH = (int) dimension.getWidth();
    public static final int VERTICAL_ROAD_X_POS_SOUTH = (int) (dimension.getWidth()/2) - (ROAD_WIDTH/2);
    public static final int VERTICAL_ROAD_X_POS_NORTH = (int) (dimension.getWidth()/2) + (ROAD_WIDTH/2);
    public static final int HORIZONTAL_ROAD_Y_POS_WEST = (int) (dimension.getHeight()/2) - (ROAD_WIDTH/2);
    public static final int HORIZONTAL_ROAD_Y_POS_EAST = (int) (dimension.getHeight()/2) + (ROAD_WIDTH/2);
    public static final int LOW_TRAFFIC_THRESHOLD_INTERVAL = 8;
    public static final int MODERATE_TRAFFIC_THRESHOLD_INTERVAL = 6;
    public static final int HIGH_TRAFFIC_THRESHOLD_INTERVAL = 4;
    public static final int VEHICLE_NORTH_POS_X = VERTICAL_ROAD_X_POS_NORTH + 4;
    public static final int VEHICLE_NORTH_POS_Y = (int) dimension.getHeight();
    public static final int VEHICLE_SOUTH_POS_X = VERTICAL_ROAD_X_POS_SOUTH + 4;
    public static final int VEHICLE_SOUTH_POS_Y = 0;
    public static final int VEHICLE_EAST_POS_X = 0;
    public static final int VEHICLE_EAST_POS_Y = HORIZONTAL_ROAD_Y_POS_EAST + 4;
    public static final int VEHICLE_WEST_POS_X = (int) dimension.getWidth();
    public static final int VEHICLE_WEST_POS_Y = HORIZONTAL_ROAD_Y_POS_WEST + 4;
    public static final int BOUNDARY_X = (int) dimension .getWidth();
    public static final int BOUNDARY_Y = (int) dimension.getHeight();

    public static final int SIGNAL_WIDTH = 50;
    public static final int SIGNAL_HEIGHT = 80;
    public static final int WARN_WIDTH = 60;
    public static final int WARN_HEIGHT = 30;

    public static final int SIGNAL_VERTICAL_POS_X_NORTH = VERTICAL_ROAD_X_POS_NORTH + ROAD_WIDTH + 15;
    public static final int SIGNAL_VERTICAL_POS_X_SOUTH = VERTICAL_ROAD_X_POS_SOUTH - ROAD_WIDTH - 15;
    public static final int SIGNAL_VERTICAL_POS_Y_NORTH = HORIZONTAL_ROAD_Y_POS_WEST - SIGNAL_HEIGHT - 15;
    public static final int SIGNAL_VERTICAL_POS_Y_SOUTH = HORIZONTAL_ROAD_Y_POS_EAST + ROAD_WIDTH + 15;

    public static final int SIGNAL_HORIZONTAL_POS_X_WEST = VERTICAL_ROAD_X_POS_SOUTH - 15 - SIGNAL_WIDTH;
    public static final int SIGNAL_HORIZONTAL_POS_X_EAST = VERTICAL_ROAD_X_POS_NORTH + 15 + SIGNAL_WIDTH;
    public static final int SIGNAL_HORIZONTAL_POS_Y_WEST = HORIZONTAL_ROAD_Y_POS_WEST - SIGNAL_HEIGHT - 15;
    public static final int SIGNAL_HORIZONTAL_POS_Y_EAST = HORIZONTAL_ROAD_Y_POS_EAST + ROAD_WIDTH + 15;
    
    public static final int WARN_VERTICAL_POS_X_NORTH = VERTICAL_ROAD_X_POS_NORTH + 15 + SIGNAL_WIDTH;
    public static final int WARN_VERTICAL_POS_X_SOUTH = VERTICAL_ROAD_X_POS_SOUTH - 15 - SIGNAL_WIDTH;
    public static final int WARN_VERTICAL_POS_Y_NORTH = HORIZONTAL_ROAD_Y_POS_EAST + ROAD_WIDTH + 120;
    public static final int WARN_VERTICAL_POS_Y_SOUTH = HORIZONTAL_ROAD_Y_POS_WEST - SIGNAL_HEIGHT - 100;

    public static final int WARN_HORIZONTAL_POS_X_WEST = VERTICAL_ROAD_X_POS_NORTH + ROAD_WIDTH + 100;
    public static final int WARN_HORIZONTAL_POS_X_EAST = VERTICAL_ROAD_X_POS_SOUTH - ROAD_WIDTH - 120;
    public static final int WARN_HORIZONTAL_POS_Y_WEST = HORIZONTAL_ROAD_Y_POS_WEST - SIGNAL_HEIGHT - 15;
    public static final int WARN_HORIZONTAL_POS_Y_EAST = HORIZONTAL_ROAD_Y_POS_EAST + ROAD_WIDTH + 15;

    public static final int SIGNAL_NORTH_RESTRICTOR = HORIZONTAL_ROAD_Y_POS_EAST + ROAD_WIDTH;
    public static final int SIGNAL_SOUTH_RESTRICTOR = HORIZONTAL_ROAD_Y_POS_WEST - (ROAD_WIDTH/2);
    public static final int SIGNAL_EAST_RESTRICTOR = VERTICAL_ROAD_X_POS_SOUTH - (ROAD_WIDTH/2);
    public static final int SIGNAL_WEST_RESTRICTOR = VERTICAL_ROAD_X_POS_NORTH + ROAD_WIDTH;
    
    public static final int WARN_NORTH_RESTRICTOR = WARN_VERTICAL_POS_Y_NORTH + WARN_HEIGHT + 30;
    public static final int WARN_SOUTH_RESTRICTOR = WARN_VERTICAL_POS_Y_SOUTH - 30;
    public static final int WARN_EAST_RESTRICTOR = WARN_HORIZONTAL_POS_X_EAST - 30;
    public static final int WARN_WEST_RESTRICTOR = WARN_HORIZONTAL_POS_X_WEST + WARN_WIDTH + 30;

    private SimulationGraphicConfig(){

    }
}
