package components.panels;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LogoPanel extends JPanel {
    public LogoPanel() {
        setBackground(new Color(40, 40, 60));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        ImageIcon icon = null;
        File logoFile = new File("resources/logo.png");

        if (logoFile.exists()) {
            icon = new ImageIcon(logoFile.getAbsolutePath());
        } else {
            try {
                icon = new ImageIcon("resources/logo.png");
            } catch (Exception e) {
                System.err.println("未找到图片: resources/logo.png");
            }
        }

        if (icon != null && icon.getImage() != null) {
            // 缩放图片
            Image scaledImage = icon.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(logoLabel, BorderLayout.CENTER);
        } else {
            // 备用文字
            JLabel textLogoLabel = new JLabel("连连看");
            textLogoLabel.setFont(new Font("微软雅黑", Font.BOLD, 42));
            textLogoLabel.setForeground(new Color(255, 200, 100));
            textLogoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(textLogoLabel, BorderLayout.CENTER);
        }
    }
}
