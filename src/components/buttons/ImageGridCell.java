package components.buttons;

import javax.swing.*;
import java.awt.*;

public class ImageGridCell extends JPanel {
    private final int row;
    private final int col;
    private final JLabel imageLabel;

    public ImageGridCell(ImageIcon icon) {
        this(0, 0, icon);
    }

    public ImageGridCell(int row, int col, ImageIcon icon) {
        this.row = row;
        this.col = col;

        imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        if (icon == null) {
            Color emptyColor = new Color(245, 245, 245);
            setBackground(emptyColor);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            return;
        }

        setLayout(new BorderLayout());
//        Color normalColor = new Color(240, 240, 240);
        Color normalColor = new Color(245, 245, 245);
        setBackground(normalColor);

//        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setOpaque(true);

        add(imageLabel, BorderLayout.CENTER);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setChosen(boolean chosen) {
        if (chosen) setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
//        else if (imageLabel != null) setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        else setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        repaint();
    }

    public void toggleRed() {
        setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        repaint();
    }

    public void resetValue() {
        Color emptyColor = new Color(245, 245, 245);
        setBackground(emptyColor);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        imageLabel.setIcon(null);
        repaint();
    }
}
