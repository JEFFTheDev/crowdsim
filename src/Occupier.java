import java.awt.*;

public class Occupier {

    // Declare color and give a default image path
    public Color occupierColor = Color.yellow;
    protected Vector2 position;

    public Occupier(Vector2 startPos) {
        position = startPos;
    }
}
