import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Agent extends Occupier {

    private ArrayList<Vector2> traveledPath;
    private Vector2 destination;
    private String name;
    private static int agentCount;

    // Temporary occupier to claim a grid tile for the next step of this agent
    private Occupier tempOccupier;

    public Agent(Vector2 startPos) {
        super(startPos);
        this.traveledPath = new ArrayList<>();
        this.occupierColor = Color.black;
        this.name = "Agent " + agentCount;
        agentCount++;
        System.out.println(this.name + " initiated at position: " + this.position + "\n------------------------");
    }

    public void advance(Vector2 newPos) {

        if (destinationReached()) {
            setRandomDestination();
        }

        traveledPath.add(this.position);
        this.position = newPos;
        System.out.println(this.name + " moving towards: " + this.position + "\n------------------------");
    }

    public void chooseNextStep() {
        tempOccupier = new Occupier(new Vector2(0, 0));
    }

    private boolean destinationReached() {
        return position.x == destination.x && position.z == destination.z;
    }

    private void setRandomDestination() {
        destination = Grid.getRandomPosition();
    }

    private String getName() {
        return this.name;
    }
}
