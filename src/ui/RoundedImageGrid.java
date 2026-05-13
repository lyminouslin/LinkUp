package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundedImageGrid {

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

/**
 * 自定义圆角图片单元格，支持点击变红
 */
class RoundedImageCell extends JPanel {
    private Image originalImage;
    private int cornerRadius;
    private boolean isRed = false;
    private Color normalColor = new Color(240, 240, 240);

    public RoundedImageCell(Image image, int radius) {
        this.originalImage = image;
        this.cornerRadius = radius;
        setBackground(normalColor);
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        // 开启抗锯齿（让圆角边缘更平滑）
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 如果处于点击红色状态，先绘制红色背景
        if (isRed) {
            g2d.setColor(Color.RED);
            g2d.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        // 绘制圆角矩形图片
        if (originalImage != null) {
            // 创建圆角裁剪区域
            RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius);
            g2d.setClip(roundRect);

            // 缩放图片以适应面板大小并居中绘制
            Image scaledImage = originalImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            g2d.drawImage(scaledImage, 0, 0, this);

            // 恢复裁剪区域（重要！否则会影响后续绘制）
            g2d.setClip(null);
        }

        // 绘制边框（可选，让圆角边界更明显）
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        g2d.dispose();
    }

    /**
     * 切换红色状态（点击变红）
     */
    public void toggleRed() {
        isRed = !isRed;
        repaint();
    }
}