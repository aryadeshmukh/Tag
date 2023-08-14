package byow.Core;
// import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
// import org.checkerframework.checker.units.qual.C;

import java.util.*;

import static byow.Core.Coordinates.distance;

public class World {

    // private static final int LOOPS = 200;

    private TETile[][] tiles;
    private TreeMap<Room, Coordinates> roomCoordinates;
    private int worldWidth;
    private int worldHeight;
    private final Random random;
    private double roomPercentage;
    private int totalTiles;

    /** Fills the 2D TETile Array with tiles. */
    private void generateWorld(int width, int height) {

        this.totalTiles = width * height;
        this.tiles = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.tiles[x][y] = Constants.EMPTY;
            }
        }

        this.worldWidth = width;
        this.worldHeight = height;
        this.roomPercentage = 0;
        this.roomCoordinates = new TreeMap<>(new RoomComparator());

        fillGridWithRooms();
        fillGridWithHallways();

    }

    public World(int width, int height, Random random) {
        this.random = random;
        generateWorld(width, height);
    }

    public World(int width, int height) {
        this.random = new Random();
        generateWorld(width, height);
    }

    /** Returns the 2D TETile array. */
    public TETile[][] getTiles() {
        TETile[][] returnTiles = new TETile[this.worldWidth][this.worldHeight + Constants.HUD_HEIGHT];
        for (int x = 0; x < this.worldWidth; x++) {
            for (int y = 0; y < this.worldHeight + Constants.HUD_HEIGHT; y++) {
                if (y >= this.worldHeight) {
                    returnTiles[x][y] = Constants.EMPTY;
                } else {
                    returnTiles[x][y] = this.tiles[x][y];
                }
            }
        }
        return returnTiles;
    }

    /** Fills tiles 2D array with rooms. */
    private void fillGridWithRooms() {
        while (this.roomPercentage < Constants.CAPACITY - Constants.CAPACITY_RANGE) {
            generateRoom();
        }
    }

    /** Fills tiles 2D array with hallways. */
    private void fillGridWithHallways() {
//        int n = 0;
        for (Room room : this.roomCoordinates.keySet()) {
            room.mark();
            int numRooms = RandomUtils.uniform(this.random, Constants.MIN_NUM_ADJOINING_ROOMS,
                    Constants.MAX_NUM_ADJOINING_ROOMS);
            List<Room> roomList = closestRooms(room, numRooms);
            for (Room adjacentRoom : roomList) {
//                if (n >= LOOPS) {
//                    break;
//                }
                addHallway(room, adjacentRoom);
//                n++;
//                if (!adjacentRoom.isMarked()) {
//                    addHallway(room, adjacentRoom);
//                }
//                TERenderer ter = new TERenderer();
//                ter.initialize(worldWidth, worldHeight);
//                ter.renderFrame(tiles);

            }
        }
    }

    /** Generates a new room and puts it in the room map. */
    private void generateRoom() {
        int width = RandomUtils.uniform(this.random, Constants.MIN_ROOM_SIZE, Constants.MAX_ROOM_SIZE) + 1;
        int height = RandomUtils.uniform(this.random, Constants.MIN_ROOM_SIZE, Constants.MAX_ROOM_SIZE) + 1;

        // Do not create room if there are already enough rooms
        if (this.roomPercentage + width * height / (double) this.totalTiles >
                Constants.CAPACITY + Constants.CAPACITY_RANGE) {
            return;
        }

        // bottom left coordinates
        int xcoor = RandomUtils.uniform(this.random, Constants.EXIT_SPACE,
                this.worldWidth - width - Constants.EXIT_SPACE + 1);
        int ycoor = RandomUtils.uniform(this.random, Constants.EXIT_SPACE,
                this.worldHeight - height - Constants.EXIT_SPACE + 1);

        Coordinates coords = new Coordinates(xcoor, ycoor);

        // Check to make sure new room does not overlap
        if (!overlaps(xcoor, ycoor, width, height)) {
            this.roomPercentage += width * height / (double) this.totalTiles;
            for (int x = xcoor; x <= xcoor + width; x++) {
                for (int y = ycoor; y <= ycoor + height; y++) {
                    if (x == xcoor || x == xcoor + width || y == ycoor || y == ycoor + height) {
                        this.tiles[x][y] = Constants.WALL;
                    } else {
                        this.tiles[x][y] = Constants.FLOOR;
                    }
                }
            }
            this.roomCoordinates.put(new Room(width, height, coords), coords);
        }

    }

    /** Checks whether the created room overlaps with any existing rooms */
    private boolean overlaps(int xcoor, int ycoor, int width, int height) {
        for (int i = xcoor - Constants.ROOM_SPACE; i <= xcoor + width + Constants.ROOM_SPACE; i++) {
            for (int j = ycoor - Constants.ROOM_SPACE; j <= ycoor + height + Constants.ROOM_SPACE; j++) {
                if (this.tiles[i][j] != Constants.EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Returns a list with the numberOfRooms closest rooms to the input room. */
    private List<Room> closestRooms(Room room, int numberOfRooms) {
        List<Room> roomList = new ArrayList<>();
        Map<Room, Double> distanceMap = new TreeMap<>(new RoomComparator());

        // int numberOfRooms = RandomUtils.uniform(random, 1, 4);

        for (Room uddaRoom : this.roomCoordinates.keySet()) {
            distanceMap.put(uddaRoom, distance(this.roomCoordinates.get(room), this.roomCoordinates.get(uddaRoom)));
        }

        for (int i = 0; i < numberOfRooms; i++) {
            Room closestRoom = room;
            double distance = Integer.MAX_VALUE;

            for (Room uddaRoom: distanceMap.keySet()) {
                if (!uddaRoom.isMarked() && distanceMap.get(uddaRoom) < distance) {
                    closestRoom = uddaRoom;
                    distance = distanceMap.get(uddaRoom);
                }
            }

            roomList.add(closestRoom);
            distanceMap.remove(closestRoom);
        }

        return roomList;
    }

    /** Returns a string describing the location of room2 relative to room1. */
    private String approximateLocation(Room room1, Room room2) {
        HashMap<String, Coordinates> room1Corners = roomCorners(room1);
        HashMap<String, Coordinates> room2Corners = roomCorners(room2);
        if (room2Corners.get("top right").x() < room1Corners.get("top left").x() + Constants.HALLWAY_FACTOR) {
            // room2 is to the left of room1
            if (room2Corners.get("top right").y() < room1Corners.get("bottom left").y() + Constants.HALLWAY_FACTOR) {
                return "bottom left";
            } else if (room2Corners.get("bottom right").y() >
                    room1Corners.get("top left").y() - Constants.HALLWAY_FACTOR) {
                return "top left";
            } else {
                return "middle left";
            }
        } else if (room2Corners.get("top left").x() > room1Corners.get("top right").x() - Constants.HALLWAY_FACTOR) {
            // room2 is to the right of room1
            if (room2Corners.get("top right").y() < room1Corners.get("bottom left").y() + Constants.HALLWAY_FACTOR) {
                return "bottom right";
            } else if (room2Corners.get("bottom right").y() >
                    room1Corners.get("top left").y() - Constants.HALLWAY_FACTOR) {
                return "top right";
            } else {
                return "middle right";
            }
        } else {
            if (room2Corners.get("bottom left").y() > room1Corners.get("top left").y()) {
                return "top";
            } else {
                return "bottom";
            }
        }
    }

    /** Returns a map containing the coordinates for the corners of a room as values. */
    private HashMap<String, Coordinates> roomCorners(Room room) {
        HashMap<String, Coordinates> corners = new HashMap<>();
        corners.put("top left", new Coordinates(this.roomCoordinates.get(room).x(),
                this.roomCoordinates.get(room).y() + room.getHeight()));
        corners.put("top right", new Coordinates(this.roomCoordinates.get(room).x() + room.getWidth(),
                this.roomCoordinates.get(room).y() + room.getHeight()));
        corners.put("bottom left", this.roomCoordinates.get(room));
        corners.put("bottom right", new Coordinates(this.roomCoordinates.get(room).x() + room.getWidth(),
                this.roomCoordinates.get(room).y()));
        return corners;
    }

    /** Returns a random number within the range. If the two bounds of the range are the same it returns the bound. */
    private int randomRange(int min, int max) {
        if (min == max) {
//            System.out.println(min);
            return min;
        } else {
//            int val = RandomUtils.uniform(random, min, max);
//            System.out.println(val);
//            return val;
            return RandomUtils.uniform(this.random, min, max);
        }
    }

    /** Adds a hallway going up from room1 to room2. */
    private void upHallway(Room room1, Room room2) {
        HashMap<String, Coordinates> room1Corners = roomCorners(room1);
        HashMap<String, Coordinates> room2Corners = roomCorners(room2);

        int minX = Math.max(room1Corners.get("top left").x(), room2Corners.get("bottom left").x());
        int maxX = Math.min(room1Corners.get("top right").x(),
                room2Corners.get("bottom right").x());
        int x = randomRange(minX + Constants.EDGE_DISTANCE, maxX);
        Coordinates start1 = new Coordinates(x, room1Corners.get("top left").y());
        Coordinates end1 = new Coordinates(x + Constants.HALLWAY_THICKNESS - 1, room2Corners.get("bottom left").y());

        fillInHallway(start1, end1);
    }

    /** Adds a hallway going down from room1 to room2. */
    private void downHallway(Room room1, Room room2) {
        HashMap<String, Coordinates> room1Corners = roomCorners(room1);
        HashMap<String, Coordinates> room2Corners = roomCorners(room2);

        int minX = Math.max(room1Corners.get("bottom left").x(), room2Corners.get("top left").x());
        int maxX = Math.min(room1Corners.get("bottom right").x(),
                room2Corners.get("top right").x());
        int x = randomRange(minX + Constants.EDGE_DISTANCE, maxX);
        Coordinates start1 = new Coordinates(x, room2Corners.get("top left").y());
        Coordinates end1 = new Coordinates(x + Constants.HALLWAY_THICKNESS - 1, room1Corners.get("bottom left").y());

        fillInHallway(start1, end1);
    }

    /** Adds a hallway going left from room1 to room2. */
    private void leftHallway(Room room1, Room room2) {
        HashMap<String, Coordinates> room1Corners = roomCorners(room1);
        HashMap<String, Coordinates> room2Corners = roomCorners(room2);

        int minY = Math.max(room1Corners.get("bottom left").y(),
                room2Corners.get("bottom right").y());
        int maxY = Math.min(room1Corners.get("top left").y(), room2Corners.get("top right").y());
        int y = randomRange(minY + Constants.EDGE_DISTANCE, maxY);
        Coordinates start1 = new Coordinates(room2Corners.get("top right").x(), y);
        Coordinates end1 = new Coordinates(room1Corners.get("top left").x(), y + Constants.HALLWAY_THICKNESS - 1);

        fillInHallway(start1, end1);
    }

    /** Adds a hallway going right from room1 to room2. */
    private void rightHallway(Room room1, Room room2) {
        HashMap<String, Coordinates> room1Corners = roomCorners(room1);
        HashMap<String, Coordinates> room2Corners = roomCorners(room2);

        int minY = Math.max(room1Corners.get("bottom right").y(),
                room2Corners.get("bottom left").y());
        int maxY = Math.min(room1Corners.get("top right").y(), room2Corners.get("top left").y());
        int y = randomRange(minY + Constants.EDGE_DISTANCE, maxY);
        Coordinates start1 = new Coordinates(room1Corners.get("top right").x(), y);
        Coordinates end1 = new Coordinates(room2Corners.get("top left").x(), y + Constants.HALLWAY_THICKNESS - 1);

        fillInHallway(start1, end1);
    }

    /** Adds a hallway going left and up from room1 to room2. */
    private void leftUpHallway(Room room1, Room room2) {
        HashMap<String, Coordinates> room1Corners = roomCorners(room1);
        HashMap<String, Coordinates> room2Corners = roomCorners(room2);

        int minY = room1Corners.get("bottom left").y();
        int maxY = Math.min(room1Corners.get("top left").y(), room2Corners.get("bottom right").y());
        int y = randomRange(minY + Constants.EDGE_DISTANCE, maxY);
        Coordinates start1 = new Coordinates(room1Corners.get("top left").x(), y);
        int minX = room2Corners.get("bottom left").x();
        int maxX = Math.min(room2Corners.get("bottom right").x(), room1Corners.get("top left").x());
        int x = randomRange(minX + Constants.EDGE_DISTANCE, maxX);
        Coordinates end1 = new Coordinates(x, y + Constants.HALLWAY_THICKNESS - 1);
        Coordinates end2 = new Coordinates(x + Constants.HALLWAY_THICKNESS - 1,
                room2Corners.get("bottom right").y());

        fillInHallway(start1, end1);
        fillInHallway(end1, end2);
    }

    /** Adds a hallway going right and up from room1 to room2. */
    private void rightUpHallway(Room room1, Room room2) {
        HashMap<String, Coordinates> room1Corners = roomCorners(room1);
        HashMap<String, Coordinates> room2Corners = roomCorners(room2);

        int minY = room1Corners.get("bottom right").y();
        int maxY = Math.min(room1Corners.get("top right").y(), room2Corners.get("bottom left").y());
        int y = randomRange(minY + Constants.EDGE_DISTANCE, maxY);
        Coordinates start1 = new Coordinates(room1Corners.get("bottom right").x(), y);
        int minX = Math.max(room1Corners.get("top right").x(), room2Corners.get("bottom left").x());
        int maxX = room2Corners.get("bottom right").x();
        int x = randomRange(minX + Constants.EDGE_DISTANCE, maxX);
        Coordinates end1 = new Coordinates(x, y + Constants.HALLWAY_THICKNESS - 1);
        Coordinates start2 = new Coordinates(x, start1.y());
        Coordinates end2 = new Coordinates(x + Constants.HALLWAY_THICKNESS - 1,
                room2Corners.get("bottom right").y());

        fillInHallway(start1, end1);
        fillInHallway(start2, end2);
    }

    /** Adds a hallway going left and down from room1 to room2. */
    private void leftDownHallway(Room room1, Room room2) {
        HashMap<String, Coordinates> room1Corners = roomCorners(room1);
        HashMap<String, Coordinates> room2Corners = roomCorners(room2);

        int minY = Math.max(room1Corners.get("bottom left").y(), room2Corners.get("top right").y());
        int maxY = room1Corners.get("top left").y();
        int y = randomRange(minY + Constants.EDGE_DISTANCE, maxY);
        Coordinates start1 = new Coordinates(room1Corners.get("top left").x(), y);
        int minX = room2Corners.get("top left").x();
        int maxX = Math.min(room2Corners.get("top right").x(), room1Corners.get("bottom left").x());
        int x = randomRange(minX + Constants.EDGE_DISTANCE, maxX);
        Coordinates end1 = new Coordinates(x, y + Constants.HALLWAY_THICKNESS - 1);

        Coordinates end2 = new Coordinates(x + Constants.HALLWAY_THICKNESS - 1, room2Corners.get("top right").y());

        fillInHallway(start1, end1);
        fillInHallway(end1, end2);
    }

    /** Adds a hallway going right and down from room1 to room2. */
    private void rightDownHallway(Room room1, Room room2) {
        HashMap<String, Coordinates> room1Corners = roomCorners(room1);
        HashMap<String, Coordinates> room2Corners = roomCorners(room2);

        int  minY = Math.max(room1Corners.get("bottom right").y(), room2Corners.get("top left").y());
        int maxY = room1Corners.get("top right").y();
        int y = randomRange(minY + Constants.EDGE_DISTANCE, maxY);
        Coordinates start1 = new Coordinates(room1Corners.get("top right").x(), y);
        int minX = Math.max(room1Corners.get("bottom right").x(), room2Corners.get("top left").x());
        int maxX = room2Corners.get("top right").x();
        int x = randomRange(minX + Constants.EDGE_DISTANCE, maxX);
        Coordinates end1 = new Coordinates(x, y + Constants.HALLWAY_THICKNESS - 1);

        Coordinates end2 = new Coordinates(x + Constants.HALLWAY_THICKNESS - 1, room2Corners.get("top left").y());

        fillInHallway(start1, end1);
        fillInHallway(end1, end2);
    }

    /** Adds a hallway in the 2D TETile array connecting two rooms. */
    private void addHallway(Room room1, Room room2) {
        // Writing a switch statement:
        // @Source https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
        // Enhanced switch statment:
        // @Source https://www.youtube.com/watch?v=AZb1wLIQ1R4
        switch (approximateLocation(room1, room2)) {
            case "top" ->
                upHallway(room1, room2);
            case "bottom" ->
                downHallway(room1, room2);
            case "middle left" ->
                leftHallway(room1, room2);
            case "middle right" ->
                rightHallway(room1, room2);
            case "top left" ->
                leftUpHallway(room1, room2);
            case "top right" ->
                rightUpHallway(room1, room2);
            case "bottom left" ->
                leftDownHallway(room1, room2);
            case "bottom right" ->
                rightDownHallway(room1, room2);
            default ->
                fillInHallway(null, null);
        }
    }

    /** Fills in the appropriate tiles in the 2D TETile array to make a hallway. */
    private void fillInHallway(Coordinates start, Coordinates end) {
        if (start == null || end == null) {
            return;
        }
        for (int x = Math.min(start.x(), end.x()) - 1; x <= Math.max(start.x(), end.x()) + 1; x++) {
            for (int y = Math.min(start.y(), end.y()) - 1; y <= Math.max(start.y(), end.y()) + 1; y++) {
                // tile is on the border of the hallway
                if (x < Math.min(start.x(), end.x()) || x > Math.max(start.x(), end.x())
                || y < Math.min(start.y(), end.y()) || y > Math.max(start.y(), end.y())) {
                    if (this.tiles[x][y] == Constants.EMPTY) {
                        // tile is not inside a room
                        this.tiles[x][y] = Constants.WALL;
                    }
                } else {
                    this.tiles[x][y] = Constants.FLOOR;
                }
            }
        }
    }

}
