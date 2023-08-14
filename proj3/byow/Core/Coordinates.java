package byow.Core;

public class Coordinates {

    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Returns the Euclidean distance between two coordinates. */
    public static double distance(Coordinates a, Coordinates b) {
        /* Find the distance between two rooms */
        double xDistance = Math.abs(a.x() - b.x());
        double yDistance = Math.abs(a.y() - b.y());
        return Math.pow(Math.pow(xDistance, 2) + Math.pow(yDistance, 2), 0.5);
    }

    /** Returns a Coordinates object for x = 0 and y = 0. */
    public static Coordinates origin() {
        return new Coordinates(0, 0);
    }

    /** Returns the x coordinate. */
    public int x() {
        return this.x;
    }

    /** Returns the y coordinate. */
    public int y() {
        return this.y;
    }

    public static void main(String[] args) {
        Coordinates a = Coordinates.origin();
        Coordinates b = new Coordinates(1, 1);
        System.out.println(distance(a, b));
    }

}
