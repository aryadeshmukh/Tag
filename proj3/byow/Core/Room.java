package byow.Core;

public class Room {

    private final int width;
    private final int height;
    private boolean marked;
    private final Coordinates coordinates;

    public Room(int width, int height, Coordinates coordinates) {
        this.width = width;
        this.height = height;
        this.marked = false;
        this.coordinates = coordinates;
    }

    /** Returns whether the room has been marked. */
    public boolean isMarked() {
        return this.marked;
    }

    /** Marks the room. */
    public void mark() {
        this.marked = true;
    }

    /** Returns the width of the room. */
    public int getWidth() {
        return this.width;
    }

    /** Returns the height of the room. */
    public int getHeight() {
        return this.height;
    }

    /** Returns the coordinates of the bottom left corner of the room. */
    public Coordinates getCoordinates() {
        return this.coordinates;
    }

}
