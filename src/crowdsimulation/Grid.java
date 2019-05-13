package crowdsimulation;

import java.util.ArrayList;
import java.util.Random;

class Grid {

    private static Occupier[][] grid;
    private static int width;
    private static int height;

    static void Initialize(int width, int height) {
        grid = new Occupier[width][height];
        Grid.width = width;
        Grid.height = height;
    }

    static Vector2 getRandomPosition() {
        //TODO check that the position is not occupied
        return new Vector2(new Random().nextInt(width), new Random().nextInt(height));
    }

    static int getWidth() {
        return width;
    }

    static int getHeight() {
        return height;
    }

    static void removeOccupierAtPos(Vector2 pos) {
        grid[pos.x][pos.z] = null;
    }

    private static boolean isAvailable(Vector2 space) {
        if (!isOutOfBounds(space)) {
            return !isOccupied(space);
        }

        return false;
    }

    private static boolean isOccupied(Vector2 space) {
        try {
            return grid[space.x][space.z] != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isOutOfBounds(Vector2 space) {
        return space.x > width - 1 || space.x < 0 || space.z > height - 1 || space.z < 0;
    }

    static Occupier[][] getGrid() {
        return grid;
    }

    static void addOccupier(Occupier occupier) {
        grid[occupier.position.x][occupier.position.z] = occupier;
    }

    static ArrayList<Vector2> getSurroundingAvailNodes(Vector2 fromThis) {

        // Make a list of all surrounding tiles
        Vector2[] posList = new Vector2[]{
                new Vector2(fromThis.x, fromThis.z + 1),
                new Vector2(fromThis.x - 1, fromThis.z + 1),
                new Vector2(fromThis.x - 1, fromThis.z),
                new Vector2(fromThis.x - 1, fromThis.z - 1),
                new Vector2(fromThis.x, fromThis.z - 1),
                new Vector2(fromThis.x + 1, fromThis.z - 1),
                new Vector2(fromThis.x + 1, fromThis.z),
                new Vector2(fromThis.x + 1, fromThis.z + 1)
        };

        ArrayList<Vector2> availablePosList = new ArrayList<>();

        // Loop through the list and if a position is available, add it to the availablePositions
        for (Vector2 pos : posList) {
            if (isAvailable(pos)) {
                availablePosList.add(pos);
            }
        }

        return availablePosList;
    }
}
