import crowd.sim.HlaAgent;
import crowd.sim.HlaAgentManager;
import crowd.sim.HlaAgentManagerListener;
import crowd.sim.HlaAgentUpdater;
import crowd.sim.exceptions.HlaAttributeNotOwnedException;
import crowd.sim.exceptions.HlaInternalException;
import crowd.sim.exceptions.HlaNotConnectedException;
import crowd.sim.exceptions.HlaRtiException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

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
        this.occupierColor = Color.black;
        this.name = "Agent " + agentCount;
        this.tempOccupier = new Occupier(startPos);
        setRandomDestination();
        agentCount++;
        createHLAInstance();
        System.out.println(this.name + " initiated at position: " + this.position + "\n------------------------");
    }

    public void advance() throws HlaRtiException, HlaNotConnectedException, HlaAttributeNotOwnedException, HlaInternalException {

        //maybe move to chooseNextStep()
        if (destination == null | destinationReached()) {
            System.out.println(this.name + " reached destination! \n------------------------");
            setRandomDestination();
        }

        traveledPath.add(this.position);
        Grid.removeOccuppierAtPos(this.position);
        Grid.removeOccuppierAtPos(tempOccupier.position);
        this.position = tempOccupier.position;
        Grid.addOccupier(this);
        updateHLAInstance();
        //System.out.println(this.name + " moving towards: " + this.position + "\n------------------------");
    }

    public void chooseNextStep() {
        // check if list has more than 0 items
        ArrayList<Vector2> tiles = Grid.getSurroundingAvailNodes(this.position);

        if (tiles.isEmpty()) {
            return;
        }

        //tiles.get(new Random().nextInt(tiles.size()));
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

        for (int i = 0; i < positions.size(); i++) {
            Vector2 pos = positions.get(i);
            double currentDist = distanceBetweenVectors(destination, pos);

            if (currentDist < shortestDist || i == 0) {
                shortestDist = currentDist;
                bestPos = pos;
            }
        }

        return bestPos;
    }

    private double distanceBetweenVectors(Vector2 from, Vector2 to) {
        double a = Math.abs(from.x - to.x);
        double b = Math.abs(from.z - to.z);
        return Math.sqrt((a * a) + (b * b));
    }

    private void createHLAInstance() throws HlaNotConnectedException, HlaRtiException, HlaInternalException, HlaAttributeNotOwnedException {
        hlaInstance = CrowdSimulation.hlaWorldInstance.getHlaAgentManager().createLocalHlaAgent();
        updateHLAInstance();
        //agent.getHlaAgentUpdater().
    }

    private void updateHLAInstance() throws HlaRtiException, HlaAttributeNotOwnedException, HlaNotConnectedException, HlaInternalException {
        HlaAgentUpdater updater = hlaInstance.getHlaAgentUpdater();
        updater.setX(position.x);
        updater.setY(0);
        updater.setZ(position.z);
        updater.setName(name);
        updater.sendUpdate();
    }
}
