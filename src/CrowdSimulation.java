import crowd.sim.*;
import crowd.sim.exceptions.*;
import se.pitch.ral.api.LogicalTime;

import javax.swing.*;
import java.util.ArrayList;

public class CrowdSimulation {

    private static ArrayList<Agent> agents;
    private static JLabel[][] gridLabels;
    private static final int amountOfAgents = 20;
    private static final int gridWidth = 25;
    private static final int gridHeight = 25;
    public static boolean isConnected;
    public static HlaWorld hlaWorldInstance;

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException {
        System.out.println("Booting crowdsim... \n------------------------");
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

        Grid.Initlialize(gridWidth, gridHeight);
        GUI.drawGrid();
        GUI.drawNodes();
        agents = createAgents();

        while(isConnected){
            advanceSimulation();
        }
    }

    private static ArrayList<Agent> createAgents() throws HlaNotConnectedException, HlaRtiException, HlaInternalException, HlaAttributeNotOwnedException {
        ArrayList<Agent> newAgents = new ArrayList<>();

        for (int i = 0; i < amountOfAgents; i++) {
            Agent newAgent = new Agent(Grid.getRandomPosition());
            newAgents.add(newAgent);
            Grid.addOccupier(newAgent);
        }

        return newAgents;
    }

    private static void advanceSimulation() throws InterruptedException, HlaAttributeNotOwnedException, HlaRtiException, HlaNotConnectedException, HlaInternalException, HlaInTimeAdvancingStateException, HlaInvalidLogicalTimeException {
        System.out.println("Advancing simulation... \n------------------------");

        for (Agent agent : agents) {
            agent.chooseNextStep();
        }

        GUI.drawNodes();
        Thread.sleep(200);

        for (Agent agent : agents) {
            agent.advance();
        }

        Thread.sleep(200);
        GUI.drawNodes();

        Thread.sleep(200);
    }
}
