import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Main {

    private static Grid grid;
    private static ArrayList<Agent> agents;
    private static JLabel[][] gridLabels;
    private static final int amountOfAgents = 30;
    private static final int gridWith = 50;
    private static final int gridHeight = 50;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Booting crowdsim... \n------------------------");

        // Configure grid
        Grid.setWidth(gridWith);
        Grid.setHeight(gridHeight);
        grid = Grid.getInstance();

        System.out.println("Creating agents... \n------------------------");
        agents = createAgents(amountOfAgents);

        // Show the grid on a UI and start the simulation
        buildGrid();
        drawOccupiers();

        while (true) {
            advanceSimulation();
        }
    }

    private static void buildGrid() {

        JPanel panel = new JPanel(new GridLayout(Grid.getWidth(), Grid.getHeight()));
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        int gridWith = Grid.getWidth();
        int gridHeight = Grid.getHeight();

        gridLabels = new JLabel[gridWith][gridHeight];

        for (int i = 0; i < (gridWith); i++) {
            for (int j = 0; j < gridHeight; j++) {
                final JLabel label = new JLabel();
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setBackground(Color.white);
                label.setOpaque(true);
                panel.add(label);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setVerticalAlignment(JLabel.CENTER);
                gridLabels[i][j] = label;
            }
        }

        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setMinimumSize(new Dimension(400, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void drawOccupiers() {

        resetGridColors();

        Occupier[][] occupiers = Grid.getOccupiers();

        for (int i = 0; i < occupiers.length; i++) {
            for (int j = 0; j < occupiers[i].length; j++) {
                Occupier occupier = occupiers[i][j];

                if (occupier != null) {
                    // Draw occupier colors unto grid
                    JLabel label = gridLabels[i][j];
                    label.setBackground(occupier.occupierColor);
                    label.setText(occupier.getName());
                    label.setForeground(Color.white);

                    if (occupier.getClass() == Agent.class) {
                        //Agent a = (Agent)occupier;
                        //JLabel destinationlabel = gridLabels[a.destination.x][a.destination.z];
                        //destinationlabel.setBackground(Color.blue);
                        //destinationlabel.setText(a.getName() + " dest");
                    }

                }

            }
        }
    }

    private static void resetGridColors() {

        for (int i = 0; i < gridWith; i++) {
            for (int j = 0; j < gridHeight; j++) {
                JLabel label = gridLabels[i][j];
                label.setBackground(Color.white);
                label.setText("");
            }
        }
    }

    private static ArrayList<Agent> createAgents(int amount) {
        ArrayList<Agent> newAgents = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
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

        drawOccupiers();

        Thread.sleep(200);

        for (Agent agent : agents) {
            agent.advance();
        }

        Thread.sleep(200);

        drawOccupiers();

        Thread.sleep(200);
    }
}
