package byow.Core;

import java.util.Comparator;

public class RoomComparator implements Comparator<Room> {

    //Used Cantor bijection function
    //@Source: https://en.wikipedia.org/wiki/Pairing_function

    public int bijection(int a, int b) {
        int ans = ((a + b) * (a + b + 1)) / 2;
        return ans + b;
    }

    @Override
    public int compare(Room room1, Room room2) {
        int roomOneBijection = bijection(room1.getCoordinates().x(), room1.getCoordinates().y());
        int roomTwoBijection = bijection(room2.getCoordinates().x(), room2.getCoordinates().y());
        return roomOneBijection - roomTwoBijection;
    }

}
