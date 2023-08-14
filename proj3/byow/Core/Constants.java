package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;

public class Constants {

    // Engine Constants
    public static final int WIDTH = 80;
    public static final int HEIGHT = 47;
    public static final int HUD_HEIGHT = 3;
    public static final String INTEGERS = "0123456789";
    public static final int SCALE = 16;
    public static final Font RULES_FONT = new Font("Monaco", Font.BOLD, 14);
    public static final Font TITLE_FONT = new Font("Monaco", Font.BOLD, 42);
    public static final String TITLE_TEXT = "TAG";
    public static final double TITLE_OFFSET = 0.8;
    public static final int TEXT_FONT_SIZE = 18;
    public static final Font TEXT_FONT = new Font("Monaco", Font.BOLD, TEXT_FONT_SIZE);
    public static final double TEXT_OFFSET = 0.75;
    public static final int MENU_OFFSET = 300;
    public static final Font SEED_FONT = new Font("Monaco", Font.BOLD, 30);
    public static final int HUD_SIZE = (int) (WIDTH / 5.0);
    public static final TETile SPRITE0 = Tileset.LOCKED_DOOR;
    public static final TETile SPRITE1 = Tileset.AVATAR;
    public static final int VIEW_RADIUS = 3;
    public static final int MAX_HEAD_START = 15;
    public static final int HEAD_START_DISPLAY_OFFSET = (int) (0.35 * WIDTH);
    public static final int HUD_HEIGHT_OFFSET = 2;
    public static final int MOUSE_STRING_OFFSET = 4;
    public static final int IT_TEXT_OFFSET = (int) (0.1 * WIDTH);
    public static final int SCORE_OFFSET = (int) (0.8 * WIDTH);
    public static final int RULES_OFFSET = (int) ((HEIGHT + HUD_HEIGHT) * 0.25);
    public static final int RULES_OFFSET_X = (int) (WIDTH / 2.0);
    public static final int HIDDEN_TILES_OFFSET = 3;
    public static final Font CONTROLS_FONT = new Font("Monaco", Font.BOLD, 14);
    public static final int CONTROLS_OFFSET = (int) (WIDTH / 2.0);
    
    // World Constants
    public static final int EXIT_SPACE = 3;
    public static final int ROOM_SPACE = 1;
    public static final int MAX_ROOM_SIZE = 7;
    public static final int MIN_ROOM_SIZE = 3;
    public static final double CAPACITY = 0.20;
    public static final double CAPACITY_RANGE = 0.03;
    public static final int HALLWAY_FACTOR = 2;
    public static final int EDGE_DISTANCE = 1;
    public static final int MIN_NUM_ADJOINING_ROOMS = 1;
    public static final int MAX_NUM_ADJOINING_ROOMS = 3;
    public static final int HALLWAY_THICKNESS = 1;

    public static final int DELAY = 50;
    public static final TETile EMPTY = Tileset.NOTHING;
    public static final TETile WALL = Tileset.WALL;
    public static final TETile FLOOR = Tileset.FLOOR;
    
}
