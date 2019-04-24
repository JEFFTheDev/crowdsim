import java.util.ArrayList;
import java.util.Random;

public class Grid {

    private static Occupier[][] occupiers;
    private static Grid instance;
    private static int width;
    private static int height;

    public Grid(int width, int height) {
        occupiers = new Occupier[width][height];
        width = width;
        height = height;
    }

    public static Grid getInstance() {
        if (instance == null) {
            instance = new Grid(width, height);
        }

        return instance;
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

    public static void setWidth(int width) {
        Grid.width = width;
    }

    public static void setHeight(int height) {
        Grid.height = height;
    }

    public static void removeOccuppierAtPos(Vector2 pos) {
        occupiers[pos.x][pos.z] = null;
    }

    public static boolean isOccupied(Vector2 space) {
        if(!isOutOfBounds(space)){
            return occupiers[space.x][space.z] != null;
        }

        return true;
    }

    public static boolean isOutOfBounds(Vector2 space) {
        if (space.x > width || space.x < 0) {
            return true;
        }

        if (space.z > height || space.z < 0) {
            return true;
        }

        return false;
    }

    public static Occupier[][] getOccupiers() {
        return occupiers;
    }

    public static void addOccupier(Occupier occupier) {
        occupiers[occupier.position.x][occupier.position.z] = occupier;
    }

    public static ArrayList<Vector2> getSurroundingTiles(Vector2 fromThis) {

        ArrayList<Vector2> posList = new ArrayList<>();
        posList.add(new Vector2(fromThis.x, fromThis.z + 1));
        posList.add(new Vector2(fromThis.x - 1, fromThis.z + 1));
        posList.add(new Vector2(fromThis.x - 1, fromThis.z));
        posList.add(new Vector2(fromThis.x - 1, fromThis.z - 1));
        posList.add(new Vector2(fromThis.x, fromThis.z - 1));
        posList.add(new Vector2(fromThis.x + 1, fromThis.z - 1));
        posList.add(new Vector2(fromThis.x + 1, fromThis.z));
        posList.add(new Vector2(fromThis.x + 1, fromThis.z + 1));

        for (Vector2 pos : posList) {
            if(isOccupied(pos) | isOutOfBounds(pos)){
                posList.remove(pos);
            }
        }

        return posList;
    }
}
