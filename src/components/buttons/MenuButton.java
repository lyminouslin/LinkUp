package components.buttons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 大圆角 + 阴影效果 + 固定尺寸的按钮
 */
public class MenuButton extends JButton {

    private int cornerRadius;
    private final Color shadowColor;
    private int shadowOffsetX;
    private int shadowOffsetY;

    // 预设颜色主题
    public enum Theme {
        PRIMARY(new Color(70, 130, 200), new Color(50, 100, 170)),
        SUCCESS(new Color(76, 175, 80), new Color(56, 155, 60)),
        WARNING(new Color(255, 152, 0), new Color(235, 132, 0)),
        DANGER(new Color(244, 67, 54), new Color(224, 47, 34)),
        PURPLE(new Color(156, 39, 176), new Color(136, 19, 156));

        final Color normal;
        final Color hover;

        Theme(Color normal, Color hover) {
            this.normal = normal;
            this.hover = hover;
        }
    }

    public MenuButton(String text, Theme theme) {
        super(text);
        this.cornerRadius = 30;
        this.shadowColor = new Color(0, 0, 0, 80);
        this.shadowOffsetX = 3;
        this.shadowOffsetY = 3;

        // 设置固定尺寸
        // 按钮固定尺寸
        int fixedWidth = 280;
        int fixedHeight = 60;
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
        setMaximumSize(new Dimension(fixedWidth, fixedHeight));

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setFont(new Font("微软雅黑", Font.BOLD, 18));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 设置默认背景色
        setBackground(theme.normal);

        // 悬停效果
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(theme.hover);
                shadowOffsetX = 2;
                shadowOffsetY = 2;
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(theme.normal);
                shadowOffsetX = 3;
                shadowOffsetY = 3;
                repaint();
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                shadowOffsetX = 1;
                shadowOffsetY = 1;
                repaint();
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                shadowOffsetX = 3;
                shadowOffsetY = 3;
                repaint();
            }
        });
    }

    /**
     * 设置圆角大小
     */
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 绘制阴影
        g2d.setColor(shadowColor);
        g2d.fillRoundRect(shadowOffsetX, shadowOffsetY,
                w - shadowOffsetX, h - shadowOffsetY,
                cornerRadius, cornerRadius);

        // 绘制按钮背景
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, w - shadowOffsetX, h - shadowOffsetY,
                cornerRadius, cornerRadius);

        // 绘制高光效果
        GradientPaint highlight = new GradientPaint(
                0, 0, new Color(255, 255, 255, 80),
                0, (float) h / 3, new Color(255, 255, 255, 20)
        );
        g2d.setPaint(highlight);
        g2d.fillRoundRect(0, 0, w - shadowOffsetX, h - shadowOffsetY,
                cornerRadius, cornerRadius);

        g2d.dispose();

        // 绘制文字
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // 不绘制默认边框
    }
}