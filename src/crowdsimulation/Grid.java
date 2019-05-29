package crowdsimulation;

import crowd.sim.HlaAgent;
import crowd.sim.HlaAgentUpdater;
import crowd.sim.HlaInteractionManager;
import crowd.sim.datatypes.TileStatus;

import java.util.*;

class Grid {

    private static Occupier[][] grid;
    private static int width;
    private static int height;
    //private static ArrayList<Tile> outGoingRequests;
    private static HashMap<Tile, Agent> outGoingRequestsByAgent;
    private static HashMap<Agent, ArrayList<Tile>> tilesByAgent;

    static void Initialize(int width, int height) throws Exception {
        grid = new Occupier[width][height];
        //outGoingRequests = new ArrayList<>();
        outGoingRequestsByAgent = new HashMap<>();
        tilesByAgent = new HashMap<>();
        Grid.width = width;
        Grid.height = height;
    }

    static Vector2 getRandomPosition() {
        //TODO check that the position is not occupied
        return new Vector2(new Random().nextInt(width), new Random().nextInt(height));
    }

    static int getWidth() {
        return width;
    }

    static int getHeight() {
        return height;
    }

    static void removeOccupierAtPos(Vector2 pos) {
        grid[(int)pos.x][(int)pos.z] = null;
    }

    public static boolean isOccupied(Tile t) {
        Vector2 space = new Vector2(t.x, t.z);
        try {
            return grid[(int)space.x][(int)space.z] != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isOutOfBounds(Tile t) {
        Vector2 space = new Vector2(t.x, t.z);
        return space.x > width - 1 || space.x < 0 || space.z > height - 1 || space.z < 0;
    }

    static Occupier[][] getGrid() {
        return grid;
    }

    static void addOccupier(Occupier occupier) {
        grid[(int)occupier.position.x][(int)occupier.position.z] = occupier;
    }

    static void findSurroundingTiles(Vector2 fromThis, Agent requester) throws Exception {

        // Make a list of all surrounding tiles
        Tile[] posList = new Tile[]{
                new Tile(fromThis.x, fromThis.z + 1),
                new Tile(fromThis.x - 1, fromThis.z + 1),
                new Tile(fromThis.x - 1, fromThis.z),
                new Tile(fromThis.x - 1, fromThis.z - 1),
                new Tile(fromThis.x, fromThis.z - 1),
                new Tile(fromThis.x + 1, fromThis.z - 1),
                new Tile(fromThis.x + 1, fromThis.z),
                new Tile(fromThis.x + 1, fromThis.z + 1)
        };

        ArrayList<Tile> availablePosList = new ArrayList<>();

        // Loop through the list and if a position is available, add it to the availablePositions
        for (Tile pos : posList) {
            if (isOutOfBounds(pos) && !isAlreadyRequested((int)pos.x, (int)pos.z)) {
                sendTileRequestForAgent(pos, requester);
            }else if(!isOccupied(pos)){
                availablePosList.add(pos);
            }
        }

        tilesByAgent.put(requester, availablePosList);

        for (HashMap.Entry<Tile, Agent> entry : outGoingRequestsByAgent.entrySet()) {

            Agent a = entry.getValue();

            if(a == requester){
                return;
            }
        }

        requester.setRequestedTiles(availablePosList);
        requester.chooseNextStep();
    }

    public static void notifyAboutTileRequest(HlaInteractionManager.HlaTileRequestParameters params) throws Exception {

        HashMap.Entry<Tile, Agent> foundEntry = null;


        System.out.println(params.getStatus());

        for (HashMap.Entry<Tile, Agent> entry : outGoingRequestsByAgent.entrySet()) {
            if(params.getX() == entry.getKey().x && params.getZ() == entry.getKey().z){

                foundEntry = entry;

                if(params.getStatus() == TileStatus.AVAILABLE){
                    Tile tile = entry.getKey();
                    tile.x -= CrowdSimulation.gridWStart;
                    tile.z -= CrowdSimulation.gridHStart;
                    tilesByAgent.get(entry.getValue()).add(entry.getKey());
                }
            }
        }

        // Remove tile request from outgoing requests because we got a result
        if(foundEntry != null){
            outGoingRequestsByAgent.remove(foundEntry.getKey());
            System.out.println("request handled");
        }

        if (outGoingRequestsByAgent.isEmpty()) {
            System.out.println("All tile requests handled \n------------------------");

            for (HashMap.Entry<Agent, ArrayList<Tile>> entry : tilesByAgent.entrySet()) {
                entry.getKey().setRequestedTiles(entry.getValue());
                entry.getKey().chooseNextStep();
            }

            tilesByAgent.clear();

            GUI.drawOccupiers();

            TimeHandler.unfreeze();
        }
    }

    public static void requestTransfer(Occupier tempOccupier) throws Exception{
        /*
         Send a temporary occupier into the federation that will be transferred
         to a different RTI
        */
        //sendAgent(tempOccupier.position.x, tempOccupier.position.z, "TEMPORARY");
        //System.out.println("AGENT TRANSFER REQUESTED \n ---------------------");
    }

    public static void sendAgent(int x, int z, String name) throws Exception{
        HlaAgent a = CrowdSimulation.world.getHlaAgentManager().createLocalHlaAgent();
        HlaAgentUpdater updater = a.getHlaAgentUpdater();
        updater.setName(name);
        updater.setX(x);
        updater.setY(0);
        updater.setZ(z);
        updater.sendUpdate();
    }

    private static void sendTileRequestForAgent(Tile tile, Agent requestedBy) throws Exception {

        //System.out.println("Sending tile request...");

        if (tile.status == TileStatus.UNKNOWN) {
            TimeHandler.freeze();
            //System.out.println("Waiting for tile result...\n------------------------");
        }


        tile.x += CrowdSimulation.gridWStart;
        tile.z += CrowdSimulation.gridHStart;

        sendTileRequest(tile);

        outGoingRequestsByAgent.put(tile, requestedBy);
    }

    public static void sendTileRequest(Tile tile) throws Exception{
        HlaInteractionManager.HlaTileRequestInteraction request =
                CrowdSimulation.world.getHlaInteractionManager().getHlaTileRequestInteraction();

        request.setX((double)tile.x); //+ CrowdSimulation.gridWStart);
        request.setY(0.0);
        request.setZ((double)tile.z); //CrowdSimulation.gridHStart);
        request.setStatus(tile.status);

        request.sendInteraction();
    }

    public static boolean isWithinJurisdiction(int x, int z){
        return x < CrowdSimulation.gridWEnd && x >= (CrowdSimulation.gridWStart)
                && z < CrowdSimulation.gridHEnd && z >= CrowdSimulation.gridHStart;
    }

    private static boolean isAlreadyRequested(int x, int z){

        for (HashMap.Entry<Tile, Agent> entry : outGoingRequestsByAgent.entrySet()) {

            if(entry.getKey().x == x && entry.getKey().z == z){
                return true;
            }
        }

        return false;
    }
}

class Tile {

    public double x;
    public double z;
    public TileStatus status;

    Tile(double x, double z, TileStatus status) {
        this.x = x;
        this.z = z;
        this.status = status;
    }

    Tile(double x, double z){
        this.x = x;
        this.z = z;
        this.status = TileStatus.UNKNOWN;
    }
}