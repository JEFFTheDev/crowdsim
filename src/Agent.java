import crowd.sim.HlaAgent;
import crowd.sim.HlaAgentUpdater;
import crowd.sim.exceptions.HlaAttributeNotOwnedException;
import crowd.sim.exceptions.HlaInternalException;
import crowd.sim.exceptions.HlaNotConnectedException;
import crowd.sim.exceptions.HlaRtiException;

import java.awt.*;
import java.util.ArrayList;

public class Agent extends Occupier {

    private ArrayList<Vector2> traveledPath;
    public Vector2 destination;
    private static int agentCount;
    private HlaAgent hlaInstance;

    // Temporary occupier to claim a grid tile for the next step of this agent
    private Occupier tempOccupier;

    public Agent(Vector2 startPos) throws HlaNotConnectedException, HlaRtiException, HlaInternalException, HlaAttributeNotOwnedException {
        super(startPos);

        this.traveledPath = new ArrayList<>();

        // The color the grid uses to visualize this agent
        this.occupierColor = Color.black;
        this.name = "Agent " + agentCount;
        this.tempOccupier = new Occupier(startPos);

        // Set a random destination and tell the Hla federation this agent exists
        setRandomDestination();
        createHlaInstance();

        agentCount++;

        System.out.println(this.name + " initiated at position: " + this.position + "\n------------------------");
    }

    public void advance() throws HlaRtiException, HlaNotConnectedException, HlaAttributeNotOwnedException, HlaInternalException {

        if (destination == null | destinationReached()) {
            System.out.println(this.name + " reached destination!");
            setRandomDestination();
        }

        // Register the current position in the travel history of this agent
        traveledPath.add(this.position);

        // Tell the grid this agent and the temporary occupier are not in the same place anymore
        Grid.removeOccupierAtPos(this.position);
        Grid.removeOccupierAtPos(tempOccupier.position);

        /*
         Set the current position to the temporary occupier's position because the agent is making
         it's next step.
          */
        this.position = tempOccupier.position;

        // Tell the Grid we're on the grid again
        Grid.addOccupier(this);

        // Update the HlaInstance to match our new attributes
        updateHlaInstance();
    }

    public void chooseNextStep() {
        // Get the surrounding available tiles around this agents' position
        ArrayList<Vector2> tiles = Grid.getSurroundingAvailNodes(this.position);

        // Do nothing if there are no available tiles
        if (tiles.isEmpty()) {
            return;
        }

        /*
        Set a temporary occupier on the closest available node to our destination.
        With the temporary occupier we claim a tile so that no other agent will
        step on it.
         */
        tempOccupier.position = closestNodeToDest(tiles);
        Grid.addOccupier(tempOccupier);
    }

    private boolean destinationReached() {
        return position.x == destination.x && position.z == destination.z;
    }

    private void setRandomDestination() {
        destination = Grid.getRandomPosition();
    }

    private Vector2 closestNodeToDest(ArrayList<Vector2> positions) {
        double shortestDist = 0;
        Vector2 bestPos = this.position;

        /*
        Loop through the positions list and check which one is closest to our
        destination.
         */
        for (int i = 0; i < positions.size(); i++) {
            Vector2 pos = positions.get(i);
            double currentDist = distanceBetweenVectors(destination, pos);

            /*
             If the current distance is shorter than assigned shortest distance
             assign the current distance to shortest distance and set the best
             position to our current position.

             If i == 0 shortest distance is not assigned yet so it's always
             the shortest distance.
              */
            if (currentDist < shortestDist || i == 0) {
                shortestDist = currentDist;
                bestPos = pos;
            }
        }

        return bestPos;
    }

    // Use pythagoras theorem to find the distance between 2 positions
    private double distanceBetweenVectors(Vector2 from, Vector2 to) {
        double a = Math.abs(from.x - to.x);
        double b = Math.abs(from.z - to.z);
        return Math.sqrt((a * a) + (b * b));
    }

    // Create a HLA instance to let the federation know this agents exists
    private void createHlaInstance() throws HlaNotConnectedException, HlaRtiException, HlaInternalException, HlaAttributeNotOwnedException {
        hlaInstance = CrowdSimulation.hlaWorldInstance.getHlaAgentManager().createLocalHlaAgent();
        updateHlaInstance();
    }

    // Update all attributes of the HlaInstance to match the attributes of this agent
    private void updateHlaInstance() throws HlaRtiException, HlaAttributeNotOwnedException, HlaNotConnectedException, HlaInternalException {
        HlaAgentUpdater updater = hlaInstance.getHlaAgentUpdater();
        updater.setX(position.x);
        updater.setY(0);
        updater.setZ(position.z);
        updater.setName(name);
        updater.sendUpdate();
    }
}
