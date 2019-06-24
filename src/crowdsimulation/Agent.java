package crowdsimulation;

import crowd.sim.HlaAgent;
import crowd.sim.HlaAgentUpdater;

import java.awt.*;
import java.util.ArrayList;

public class Agent extends Occupier {

    private ArrayList<Vector2> traveledPath;
    public Vector2 destination;
    private static int agentCount;
    public HlaAgent hlaInstance;
    private ArrayList<Tile> tilesAroundMe;
    public enum State{TRANSFERRING, TRANSFERRED, INSIMULATION}
    public State state;
    private int currentDestIndex = -1;
    public static ArrayList<Vector2> definedPath = new ArrayList<>();

    // Temporary occupier to claim a grid tile for the next step of this agent
    public Occupier tempOccupier;

    public Agent(Vector2 startPos, boolean createHlaInstance) throws Exception {
        super(startPos);
        this.state = State.INSIMULATION;
        this.traveledPath = new ArrayList<>();

        // The color the grid uses to visualize this agent
        this.occupierColor = Color.black;
        this.name = "A" + agentCount + "-" + CrowdSimulation.world.getSettings().getCrcPort();
        this.tempOccupier = new Occupier(startPos);

        // Set a random destination and tell the Hla federation this agent exists
        destination = getNextDestination();

        if(createHlaInstance){
            createHlaInstance();
        }

        agentCount++;

        System.out.println(this.name + " initiated at position: " + this.position + "\n------------------------");
    }

    public Vector2 getNextDestination(){
        int index = currentDestIndex;

        if(index + 1 > definedPath.size() - 1) {
            currentDestIndex = 0;
        }else{
            currentDestIndex++;
        }

        return definedPath.get(currentDestIndex);
    }

    public void advance() throws Exception {

        // Register the current position in the travel history of this agent
        traveledPath.add(this.position);

        // Tell the grid this agent and the temporary occupier are not in the same place anymore
        Grid.removeOccupierAtPos(this.position);

        if (state == State.TRANSFERRING) {
            agentCount--;
            /*
             When the position is updated to be outside of this simulation. The bridge will automatically transfer
             the agent.
              */
            this.position = tempOccupier.position;
            updateHlaInstance();
            state = State.TRANSFERRED;


            return;
        }

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

    public void chooseNextStep() throws Exception {

        // Do nothing if there are no available tiles
        if (tilesAroundMe.isEmpty()) {
            return;
        }

        if (destination == null | destinationReached()) {
            System.out.println(this.name + " reached destination!");
            destination = getNextDestination();
        }

        Vector2 bestStep = closestNodeToDest(tilesAroundMe);

        /*
        Set a temporary occupier on the closest available node to our destination.
        With the temporary occupier we claim a tile so that no other agent will
        step on it.
         */
        tempOccupier.position = bestStep;

        /*
        If the value of best step is true, this agent can stay in
        this federate. Otherwise it will request a transfer to a
        different federate.
         */
        if (!Grid.isOutOfBounds(new Tile(bestStep.x, bestStep.z))) {
            Grid.addOccupier(tempOccupier);
        } else {
            /*
             Tell the grid this agents wants to transfer to a different federate
             adds a temporary occupier in another federate to let that federate know
             this agent will transfer in the next time phase
             */
            state = State.TRANSFERRING;
            Grid.requestTransfer(this);
        }
    }

    public void prepare() throws Exception {
        Grid.findSurroundingTiles(this.position, this);
    }

    public void setRequestedTiles(ArrayList<Tile> tiles) {
        tilesAroundMe = tiles;
    }

    private boolean destinationReached() {
        return position.x == destination.x && position.z == destination.z;
    }

    /*private void setRandomDestination() {
        destination = Grid.getRandomPosition();
    }*/

    private Vector2 closestNodeToDest(ArrayList<Tile> positions) {
        double shortestDist = 0;

        Vector2 bestPos = this.position;

        /*
        Loop through the positions map and check which one is closest to our
        destination.
         */
        int i = 0;

        for (Tile pos : positions) {

            double currentDist = distanceBetweenVectors(destination, new Vector2(pos.x, pos.z));

            /*
             If the current distance is shorter than assigned shortest distance
             assign the current distance to shortest distance and set the best
             position to our current position.

             If i == 0 shortest distance is not assigned yet so it's always
             the shortest distance.
              */
            if (currentDist < shortestDist || i == 0) {
                shortestDist = currentDist;
                bestPos = new Vector2(pos.x, pos.z);
            }

            i++;
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
    public void createHlaInstance() throws Exception {
        hlaInstance = CrowdSimulation.world.getHlaAgentManager().createLocalHlaAgent();
        updateHlaInstance();
    }

    // Update all attributes of the HlaInstance to match the attributes of this agent
    public void updateHlaInstance() throws Exception {
        HlaAgentUpdater updater = hlaInstance.getHlaAgentUpdater();
        updater.setX(position.x + CrowdSimulation.gridWStart);
        updater.setY(0);
        updater.setZ(position.z + CrowdSimulation.gridHStart);
        updater.setName(name);
        updater.sendUpdate();
    }
}
