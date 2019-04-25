import com.sun.corba.se.impl.orbutil.ObjectStreamClassUtil_1_3;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Agent extends Occupier {

    private ArrayList<Vector2> traveledPath;
    private Vector2 destination;
    private static int agentCount;

    // Temporary occupier to claim a grid tile for the next step of this agent
    private Occupier tempOccupier;

    public Agent(Vector2 startPos) {
        super(startPos);
        this.traveledPath = new ArrayList<>();
        this.occupierColor = Color.black;
        this.name = "Agent " + agentCount;
        this.tempOccupier = new Occupier(startPos);
        setRandomDestination();
        agentCount++;
        System.out.println(this.name + " initiated at position: " + this.position + "\n------------------------");
    }

    public void advance() {

        if (destination == null | destinationReached()) {
            setRandomDestination();
        }

        traveledPath.add(this.position);
        Grid.removeOccuppierAtPos(this.position);
        Grid.removeOccuppierAtPos(tempOccupier.position);
        this.position = tempOccupier.position;
        Grid.addOccupier(this);
        System.out.println(this.name + " moving towards: " + this.position + "\n------------------------");
    }

    public void chooseNextStep() {
        // check if list has more than 0 items
        ArrayList<Vector2> tiles = Grid.getSurroundingTiles(this.position);

        if(tiles.isEmpty()){
            return;
        }

        Vector2 nextPos = tiles.get(new Random().nextInt(tiles.size()));
        tempOccupier.position = nextPos;
        Grid.addOccupier(tempOccupier);
    }

    private boolean destinationReached() {
        return position.x == destination.x && position.z == destination.z;
    }

    private void setRandomDestination() {
        destination = Grid.getRandomPosition();
    }
}
