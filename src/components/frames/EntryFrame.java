package components.frames;

import components.buttons.MenuButton;
import components.panels.LogoPanel;
import ui.*;
import util.DifficultySelector;
import util.UserModeSelector;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static constants.Constants.MENU_LENGTH;
import static constants.Constants.MENU_WIDTH;

public class EntryFrame extends JFrame {
    public EntryFrame() {
        createFrame();
        createLogoPanel();
        createButton();
    }
    private void createFrame() {
        setTitle("连连看-主菜单");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MENU_LENGTH, MENU_WIDTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    private void createLogoPanel() {
        LogoPanel logoPanel = new LogoPanel();

        File gifFile = new File("resources/nailong2.gif");
        ImageIcon gifIcon = new ImageIcon(gifFile.getAbsolutePath());
        JLabel leftGifLabel = new JLabel(gifIcon);
        JLabel rightGifLabel = new JLabel(gifIcon);
        leftGifLabel.setOpaque(false);
        rightGifLabel.setOpaque(false);
        leftGifLabel.setBackground(new Color(0, 0, 0, 0));
        rightGifLabel.setBackground(new Color(0, 0, 0, 0));

        add(logoPanel, BorderLayout.NORTH);
        add(leftGifLabel, BorderLayout.WEST);
        add(rightGifLabel, BorderLayout.EAST);
    }
    private void createButton() {
        // 使用自定义圆角按钮
        MenuButton guestModeButton = new MenuButton("游客模式", MenuButton.Theme.SUCCESS);
        MenuButton userModeButton = new MenuButton("注册用户模式", MenuButton.Theme.PRIMARY);
        MenuButton exitModeButton = new MenuButton("退出", MenuButton.Theme.DANGER);

        // 设置按钮固定尺寸
        Dimension buttonSize = new Dimension(280, 70);
        guestModeButton.setPreferredSize(buttonSize);
        userModeButton.setPreferredSize(buttonSize);
        exitModeButton.setPreferredSize(buttonSize);

        // 调整圆角大小
        guestModeButton.setCornerRadius(35);
        userModeButton.setCornerRadius(35);
        exitModeButton.setCornerRadius(35);

        // 添加事件
        guestModeButton.addActionListener(e -> {
            Boolean hardMode = DifficultySelector.chooseDifficulty(this);
            if (hardMode != null) {
                GameFrame gameWindow = new GameFrame(hardMode);
                WindowManager.setMainFrame(this);
                WindowManager.switchTo(gameWindow);
            }
        });

        userModeButton.addActionListener(e -> {
            Integer result = UserModeSelector.chooseUserAction(this);
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

        add(centerWrapper, BorderLayout.CENTER);
    }
}
