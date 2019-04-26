import java.util.ArrayList;
import java.util.Random;

public class Grid {

    private static Occupier[][] occupiers;
    private static int width;
    private static int height;

    public static void Initlialize(int width, int height){
        occupiers = new Occupier[width][height];
        Grid.width = width;
        Grid.height = height;
    }

    public static Vector2 getRandomPosition() {
        return new Vector2(new Random().nextInt(height), new Random().nextInt(height));
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static void removeOccuppierAtPos(Vector2 pos) {
        occupiers[pos.x][pos.z] = null;
    }

    public static boolean isAvailable(Vector2 space) {
        if (!isOutOfBounds(space)) {
            return !isOccupied(space);
        }

        return false;
    }

    public static boolean isOccupied(Vector2 space) {
        try {
            return occupiers[space.x][space.z] != null;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isOutOfBounds(Vector2 space) {
        return space.x > width || space.x < 0 || space.z > height || space.z < 0;
    }

    public static Occupier[][] getOccupiers() {
        return occupiers;
    }

    public static void addOccupier(Occupier occupier) {
        occupiers[occupier.position.x][occupier.position.z] = occupier;
    }

    public static ArrayList<Vector2> getSurroundingAvailNodes(Vector2 fromThis) {

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

        for (Vector2 pos : posList) {
            if (isAvailable(pos)) {
                availablePosList.add(pos);
            }
        }

        return availablePosList;
    }
}
