import javax.swing.*;
import java.util.ArrayList;

public class CrowdSimulation {

    private static ArrayList<Agent> agents;
    private static JLabel[][] gridLabels;
    private static final int amountOfAgents = 40;
    private static final int gridWidth = 50;
    private static final int gridHeight = 50;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Booting crowdsim... \n------------------------");

        Grid.Initlialize(gridWidth, gridHeight);

        System.out.println("Creating agents... \n------------------------");
        agents = createAgents();

        // Draw the grid and its agents
        GUI.drawGrid();
        GUI.drawNodes();

        // Start the simulation
        while (true) {
            advanceSimulation();
        }
    }

    private static ArrayList<Agent> createAgents() {
        ArrayList<Agent> newAgents = new ArrayList<>();

        for (int i = 0; i < amountOfAgents; i++) {
            Agent newAgent = new Agent(Grid.getRandomPosition());
            newAgents.add(newAgent);
            Grid.addOccupier(newAgent);
        }

        return newAgents;
    }

    private static void advanceSimulation() throws InterruptedException {
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
