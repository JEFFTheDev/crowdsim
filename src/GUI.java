import javax.swing.*;
import java.awt.*;

public class GUI {

    private static JLabel[][] gridLabels;
    private static int gridWidth;
    private static int gridHeight;

    public static void drawGrid() {

        JPanel panel = new JPanel(new GridLayout(Grid.getWidth(), Grid.getHeight()));
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        gridWidth = Grid.getWidth();
        gridHeight = Grid.getHeight();

        gridLabels = new JLabel[gridWidth][gridHeight];

        // Add labels for each cell in the grid
        for (int i = 0; i < (gridWidth); i++) {
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

    public static void resetGridColors() {

        // Reset all grid tiles to default visualizations
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                JLabel label = gridLabels[i][j];
                label.setBackground(Color.white);
                label.setText("");
            }
        }
    }

    public static void drawOccupiers() {

        resetGridColors();

        Occupier[][] occupiers = Grid.getGrid();

        // Draw the grid's occupiers
        for (int i = 0; i < occupiers.length; i++) {
            for (int j = 0; j < occupiers[i].length; j++) {
                Occupier occupier = occupiers[i][j];

                if (occupier != null) {
                    JLabel label = gridLabels[i][j];
                    label.setBackground(occupier.occupierColor);
                    label.setText(occupier.getName());

                    // Sets the text color
                    label.setForeground(Color.white);
                }

            }
        }
    }
}
