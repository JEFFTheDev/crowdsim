package crowdsimulation;

import crowd.sim.*;
import crowd.sim.datatypes.TileStatus;
import crowd.sim.datatypes.TimeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class CrowdSimulation {

    public static ArrayList<Agent> agents;
    private static int amountOfAgents = 15;
    public static int gridWStart = 0;
    public static int gridWEnd = 10;
    public static int gridHStart = 0;
    public static int gridHEnd = 10;
    private static int gridWidth = gridWEnd - gridWStart;
    private static int gridHeight = gridHEnd - gridHStart;
    private static boolean isConnected;
    private static String fileName = "src/sim.config";
    static HlaWorld world;

    public static void main(String[] args) throws Exception {
        System.out.println("Booting crowdsim... \n------------------------");

        if (args.length > 0) {
            fileName = args[0];
        }

        TimeHandler.instance = new TimeHandler();
        TimeHandler.instance.start();

        InitializeSettings();

        world = HlaWorld.Factory.create();
        world.addHlaWorldListener(new WorldListener());
        world.getHlaAgentManager().addHlaAgentManagerListener(new AgentListener());
        world.getHlaInteractionManager().addHlaInteractionListener(new InteractionListener());
        world.connect();
    }

    private static void InitializeSettings() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(fileName)));

        // Get all settings from the config file and set them for this federate
        System.setProperty("crowd.sim.federateName", properties.getProperty("federateName"));
        System.setProperty("crowd.sim.crcHost", properties.getProperty("crcHost"));
        System.setProperty("crowd.sim.crcPort", properties.getProperty("crcPort"));
        System.setProperty("crowd.sim.evolvedFomURL", properties.getProperty("fom"));

        // Get all settings specific for the semantic crowd simulation and set them to according attributes
        gridWStart = Integer.parseInt(properties.getProperty("gridWStart"));
        gridWEnd = Integer.parseInt(properties.getProperty("gridWEnd"));
        gridHStart = Integer.parseInt(properties.getProperty("gridHStart"));
        gridHEnd = Integer.parseInt(properties.getProperty("gridHEnd"));
        amountOfAgents = Integer.parseInt(properties.getProperty("amountOfAgents"));

        String path = properties.getProperty("path");

        for(String p : path.split(",")){
            String[] nodes = p.split(":");
            Agent.definedPath.add(new Vector2(Integer.parseInt(nodes[0]), Integer.parseInt(nodes[1])));
        }

        gridWidth = gridWEnd - gridWStart;
        gridHeight = gridHEnd - gridHStart;
    }

    private static class WorldListener extends HlaWorldListener.Adapter {
        public void connected(HlaTimeStamp timeStamp) {
            System.out.println("Connected to Federation...\n------------------------");

            try {
                createHlaGrid();
                CrowdSimulation.isConnected = true;
                CrowdSimulation.startSimulation();

                // Let the federation know we exist by sending a time tick
                TimeHandler.instance.sendTimeGrantCallback(TimeType.PREPARE, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void disconnected(HlaTimeStamp timeStamp) {
            System.out.println("Disconnected from Federation...\n------------------------");
            CrowdSimulation.isConnected = false;
        }
    }

    private static class AgentListener extends HlaAgentManagerListener.Adapter{
        public void hlaAgentInitialized(HlaAgent agent, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime) {

            /*
             If the agent is made by this federate, ignore it
             also check if the agent's position is within this simulation's grid
            */
            if((agent.getProducingFederate().getFederateName() == world.getFederateId().getFederateName()) || !Grid.isWithinJurisdiction((int)agent.getX(), (int)agent.getZ())){
                return;
            }

            try {
                System.out.println(agent.getX() + " / " + agent.getZ());

                // Adopt the agent from another federation
                int x = (int) agent.getX() - gridWStart;
                int z = (int) agent.getZ() - gridHStart;
                Vector2 agentPosition = new Vector2(x, z);
                Agent simAgent = new Agent(agentPosition, false);
                simAgent.name = agent.getName();
                simAgent.createHlaInstance();
                agents.add(simAgent);
                Grid.addOccupier(simAgent);
                GUI.drawOccupiers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class InteractionListener extends HlaInteractionListener.Adapter {
        public void timeGrant(boolean local, HlaInteractionManager.HlaTimeGrantParameters parameters, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime) {
            if (!local && !parameters.getReady()) {
                try {
                    //advanceSimulation(parameters.getType());
                    TimeHandler.instance.advanceSimulation(parameters.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void tileRequest(boolean local, HlaInteractionManager.HlaTileRequestParameters parameters, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime){

            /*
             Return if the tile request was sent by this federate or if the status is unknown
              */
            if (parameters.getProducingFederate().getFederateName() == world.getFederateId().getFederateName()) {
                return;
            }

            System.out.println("received tile request");

            /*
            if the tile is outside of this federate's boundaries notify the grid about it's existence
             */
            if(!Grid.isWithinJurisdiction((int)parameters.getX(), (int)parameters.getZ())){
                System.out.println(parameters.getX() + " / " + parameters.getZ());
                try {
                    Grid.notifyAboutTileRequest(parameters);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                // If the requested tile is within this federate, return a request with the status of the tile
                try {
                    Tile requestedTile = new Tile((int)parameters.getX() -gridWStart, (int)parameters.getZ() -gridHStart);
                    boolean isOccupied = Grid.isOccupied(requestedTile);
                    requestedTile.status = isOccupied ? TileStatus.OCCUPIED : TileStatus.AVAILABLE;
                    requestedTile.x = (int)parameters.getX();
                    requestedTile.z = (int)parameters.getZ();
                    Grid.sendTileRequest(requestedTile);
                    System.out.println("Returning: " + requestedTile.status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void startSimulation() throws Exception {
        Grid.Initialize(gridWidth, gridHeight);
        GUI.drawGrid();
        agents = createAgents();
        GUI.drawOccupiers();
    }

    // Creates the agents in our simulation and places them on a random position on the grid
    private static ArrayList<Agent> createAgents() throws Exception {
        ArrayList<Agent> newAgents = new ArrayList<>();

        for (int i = 0; i < amountOfAgents; i++) {
            //Grid.getRandomPosition()
            Agent newAgent = new Agent(new Vector2(0,0), true);
            //newAgent.destination = new Vector2(15, 5);
            newAgents.add(newAgent);

            // Tell the grid we placed an agent on it
            Grid.addOccupier(newAgent);
        }

        return newAgents;
    }


    // Tells the HLA world which part of the grid this simulation is responsible for
    private static void createHlaGrid() throws Exception {
        HlaGrid grid = world.getHlaGridManager().createLocalHlaGrid();
        HlaGridUpdater updater = grid.getHlaGridUpdater();
        updater.setGridWStart(gridWStart);
        updater.setGridWEnd(gridWEnd);
        updater.setGridHStart(gridHStart);
        updater.setGridHEnd(gridHEnd);
        updater.sendUpdate();
    }
}
