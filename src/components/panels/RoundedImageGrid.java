package components.panels;

import components.buttons.RoundedImageCell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedImageGrid extends JPanel {

    private static final int ROWS = 3;
    private static final int COLS = 3;
    private static final int CORNER_RADIUS = 30;  // 圆角半径

    public static void main(String[] args) {
        // 图片路径（改成你自己的）
        String imagePath = "./resources/1.png";  // Windows
        // String imagePath = "/Users/xxx/Pictures/test.jpg";  // Mac

        ImageIcon originalIcon = new ImageIcon(imagePath);
        if (originalIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("图片加载失败: " + imagePath);
            return;
        }

        JFrame frame = new JFrame("圆角矩形图片网格");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);

        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 10, 10));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < ROWS * COLS; i++) {
            RoundedImageCell cell = new RoundedImageCell(originalIcon.getImage(), CORNER_RADIUS);
            cell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cell.toggleRed();
                }
            });
            gridPanel.add(cell);
        }

        frame.add(gridPanel);
        frame.setVisible(true);
    }
}

