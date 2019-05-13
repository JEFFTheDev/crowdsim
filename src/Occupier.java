import java.awt.*;

public class Occupier {

    // The default color the grid will use to visualize this occupier
    public Color occupierColor = Color.green;
    protected Vector2 position;

    // Default name of a standard occupier
    protected String name = "Occupied";

    public Occupier(Vector2 startPos) {
        position = startPos;
    }

    public String getName() {
        return this.name;
    }
}
