import java.awt.*;

public class Occupier {

    // Set default occupier color to yellow
    public Color occupierColor = Color.green;
    protected Vector2 position;
    protected String name = "Occupied";

    public Occupier(Vector2 startPos) {
        position = startPos;
    }

    public String getName() {
        return this.name;
    }
}
