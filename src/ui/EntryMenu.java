package ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class EntryMenu {
    private static final int BUTTON_WIDTH = 260;
    private static final int BUTTON_HEIGHT = 60;

    // 创建用户登录之后显示的主窗口
    public static void main(String[] args) {
        showMainMenu();
    }

    public static void showMainMenu() {
        JFrame frame = createFrame();
        createButton(frame);
        createLogoPanel(frame);
        WindowManager.setMainFrame(frame);
        WindowManager.showMainWindow();
    }

    private static void createLogoPanel(JFrame frame) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 40, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // 方式1：使用文件路径（推荐，最简单）
        ImageIcon icon = null;
        ImageIcon gifIcon = null;
        File logoFile = new File("resources/logo.png");
        File gifFile = new File("resources/nailong2.gif");
        gifIcon = new ImageIcon(gifFile.getAbsolutePath());

        if (logoFile.exists()) {
            icon = new ImageIcon(logoFile.getAbsolutePath());
        } else {
            // 方式2：使用相对路径
            try {
                icon = new ImageIcon("resources/logo.png");
            } catch (Exception e) {
                System.err.println("未找到图片: resources/logo.png");
            }
        }

        if (icon != null && icon.getImage() != null) {
            // 缩放图片
            Image scaledImage = icon.getImage().getScaledInstance(
                    400, -1, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(logoLabel, BorderLayout.CENTER);
        } else {
            // 备用文字
            JLabel textLogo = new JLabel("连连看");
            textLogo.setFont(new Font("微软雅黑", Font.BOLD, 42));
            textLogo.setForeground(new Color(255, 200, 100));
            textLogo.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(textLogo, BorderLayout.CENTER);
        }

        JLabel gifLabel1 = new JLabel(gifIcon);
        JLabel gifLabel2 = new JLabel(gifIcon);
        gifLabel1.setOpaque(false);
        gifLabel2.setOpaque(false);
        gifLabel1.setBackground(new Color(0, 0, 0, 0));
        gifLabel2.setBackground(new Color(0, 0, 0, 0));

        frame.add(panel, BorderLayout.NORTH);
        frame.add(gifLabel1, BorderLayout.WEST);
        frame.add(gifLabel2, BorderLayout.EAST);
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("连连看-主菜单");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        return frame;
    }

    private static void createButton(JFrame frame) {
        // 使用自定义圆角按钮
        RoundedShadowButton guestModeButton = new RoundedShadowButton("游客模式", RoundedShadowButton.Theme.SUCCESS);
        RoundedShadowButton userModeButton = new RoundedShadowButton("注册用户模式", RoundedShadowButton.Theme.PRIMARY);
        RoundedShadowButton exitModeButton = new RoundedShadowButton("退出", RoundedShadowButton.Theme.DANGER);

        // 设置按钮固定尺寸
        Dimension buttonSize = new Dimension(280, 70);
        guestModeButton.setPreferredSize(buttonSize);
        userModeButton.setPreferredSize(buttonSize);
        exitModeButton.setPreferredSize(buttonSize);

        // 可选：调整圆角大小
        guestModeButton.setCornerRadius(35);
        userModeButton.setCornerRadius(35);
        exitModeButton.setCornerRadius(35);

        // 添加事件
        guestModeButton.addActionListener(e -> {
            Boolean hardMode = DifficultySelector.chooseDifficulty(frame);
            if (hardMode != null) {
                GameFrame gameWindow = new GameFrame(hardMode);
                WindowManager.switchTo(gameWindow);
            }
        });

        userModeButton.addActionListener(e -> {
            Integer result = UserModeSelector.chooseUserAction(frame);
            if (result == null) return;
            if (result == 0) {
                JFrame loginWindow = Login.createLoginWindow();
                WindowManager.switchTo(loginWindow);
            } else {
                JFrame registerWindow = Register.createRegisterWindow();
                WindowManager.switchTo(registerWindow);
            }
        });

        exitModeButton.addActionListener(e -> System.exit(0));

        // 按钮面板布局
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(guestModeButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(userModeButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(exitModeButton);
        buttonPanel.add(Box.createVerticalGlue());

        // 水平居中
        guestModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        userModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(buttonPanel);

        frame.add(centerWrapper, BorderLayout.CENTER);
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setFont(new Font("微软雅黑", Font.BOLD, 18));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(70, 130, 200));
        button.setFocusPainted(false);
//        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 160, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 200));
            }
        });

        return button;
    }

}
