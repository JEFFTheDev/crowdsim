import crowd.sim.*;
import crowd.sim.exceptions.*;

import java.util.ArrayList;

public class CrowdSimulation {

    private static ArrayList<Agent> agents;
    private static final int amountOfAgents = 15;
    private static final int gridWidth = 20;
    private static final int gridHeight = 20;
    public static boolean isConnected;
    public static HlaWorld hlaWorldInstance;

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException {
        System.out.println("Booting crowdsim... \n------------------------");

        // Create a HlaWorld instance and use it to connect to the federation
        hlaWorldInstance = HlaWorld.Factory.create();
        hlaWorldInstance.addHlaWorldListener(new WorldListener());
        hlaWorldInstance.connect();
    }

    private static class WorldListener extends HlaWorldListener.Adapter {
        public void connected(HlaTimeStamp timeStamp) {
            System.out.println("Connected to Federation...\n------------------------");
            CrowdSimulation.isConnected = true;
            try {
                CrowdSimulation.startSimulation();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (HlaAttributeNotOwnedException e) {
                e.printStackTrace();
            } catch (HlaNotConnectedException e) {
                e.printStackTrace();
            } catch (HlaRtiException e) {
                e.printStackTrace();
            } catch (HlaInternalException e) {
                e.printStackTrace();
            } catch (HlaInTimeAdvancingStateException e) {
                e.printStackTrace();
            } catch (HlaInvalidLogicalTimeException e) {
                e.printStackTrace();
            }
        }

        public void disconnected(HlaTimeStamp timeStamp) {
            System.out.println("Disconnected from Federation...\n------------------------");
            CrowdSimulation.isConnected = false;
        }
    }

    public static void startSimulation() throws InterruptedException, HlaAttributeNotOwnedException, HlaNotConnectedException, HlaRtiException, HlaInternalException, HlaInTimeAdvancingStateException, HlaInvalidLogicalTimeException {

        Grid.Initialize(gridWidth, gridHeight);
        GUI.drawGrid();
        GUI.drawOccupiers();
        agents = createAgents();

        while (isConnected) {
            advanceSimulation();
        }
    }

    // Creates the agents in our simulation and places them on a random position on the grid
    private static ArrayList<Agent> createAgents() throws HlaNotConnectedException, HlaRtiException, HlaInternalException, HlaAttributeNotOwnedException {
        ArrayList<Agent> newAgents = new ArrayList<>();

        for (int i = 0; i < amountOfAgents; i++) {
            Agent newAgent = new Agent(Grid.getRandomPosition());
            newAgents.add(newAgent);

            // Tell the grid we placed an agent on it
            Grid.addOccupier(newAgent);
        }

        return newAgents;
    }

    private static void advanceSimulation() throws InterruptedException, HlaAttributeNotOwnedException, HlaRtiException, HlaNotConnectedException, HlaInternalException, HlaInTimeAdvancingStateException, HlaInvalidLogicalTimeException {
        System.out.println("Advancing simulation... \n------------------------");

        // Make every agent decide where it wants to go next
        for (Agent agent : agents) {
            agent.chooseNextStep();
        }

        // Draw all the occupiers on the grid
        GUI.drawOccupiers();
        Thread.sleep(200);

        // Make every agent set there previously chosen step
        for (Agent agent : agents) {
            agent.advance();
        }

        Thread.sleep(200);
        GUI.drawOccupiers();

        Thread.sleep(200);
    }
}
