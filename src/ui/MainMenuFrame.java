// ui/MainMenuFrame.java
package ui;

import data.GlobalData;

import javax.swing.*;
import java.awt.*;

import static constants.Constants.MENU_LENGTH;
import static constants.Constants.MENU_WIDTH;

public class MainMenuFrame extends JFrame {

    public MainMenuFrame() {
        setTitle("游戏大厅 - 连连看");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MENU_LENGTH, MENU_WIDTH);
        setLocationRelativeTo(null);

        // 主布局：BorderLayout
        setLayout(new BorderLayout(10, 10));

        // 添加各个区域
        add(createTopPanel(), BorderLayout.NORTH);      // 用户信息
        add(createLeftPanel(), BorderLayout.WEST);      // 排行榜
        add(createCenterPanel(), BorderLayout.CENTER);  // 游戏选择
        add(createRightPanel(), BorderLayout.EAST);     // 存档

        WindowManager.setMainFrame(this);
    }

    /**
     * 顶部：用户信息面板
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setBackground(new Color(240, 248, 255));  // 浅蓝色背景

        // 左侧：欢迎语
        JLabel welcomeLabel = new JLabel("欢迎回来，玩家：");
        welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JLabel usernameLabel = new JLabel(GlobalData.currentUsername);
        usernameLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        usernameLabel.setForeground(new Color(0, 100, 200));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(welcomeLabel);
        leftPanel.add(usernameLabel);

        // 右侧：头像和设置
        JButton avatarBtn = new JButton("👤 更换头像");
        avatarBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JButton settingsBtn = new JButton("⚙️ 设置");
        settingsBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(avatarBtn);
        rightPanel.add(settingsBtn);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * 左侧：排行榜面板
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(200, 0));

        // 标题
        JLabel titleLabel = new JLabel("🏆 积分排行榜");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 排行榜列表
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("🥇 张三 - 1280分");
        listModel.addElement("🥈 李四 - 950分");
        listModel.addElement("🥉 王五 - 870分");
        listModel.addElement("   赵六 - 620分");
        listModel.addElement("   小明 - 450分");

        JList<String> rankList = new JList<>(listModel);
        rankList.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        rankList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        rankList.setFixedCellHeight(35);

        JScrollPane scrollPane = new JScrollPane(rankList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 中间：游戏选择面板
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 250, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 标题
        JLabel titleLabel = new JLabel("选择游戏模式");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 150));
        panel.add(titleLabel, gbc);

        // 模式选项
        gbc.gridy++;
        RoundedShadowButton classicBtn = new RoundedShadowButton("\uD83C\uDFAE 经典模式", RoundedShadowButton.Theme.SUCCESS);
        panel.add(classicBtn, gbc);
        classicBtn.addActionListener(e -> {
            GameFrame gameWindow = new GameFrame(false);
            WindowManager.switchTo(gameWindow);
        });


        gbc.gridy++;
        RoundedShadowButton hardBtn = new RoundedShadowButton("\uD83C\uDFAE 困难模式", RoundedShadowButton.Theme.DANGER);
        panel.add(hardBtn, gbc);
        hardBtn.addActionListener(e -> {
            GameFrame gameWindow = new GameFrame(true);
            WindowManager.switchTo(gameWindow);
        });

        return panel;
    }

    private JButton createGameModeButton(String title, String desc, Color color) {
        // 使用 BoxLayout 垂直排列文本
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(color);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);

        btnPanel.add(titleLabel);
        btnPanel.add(Box.createVerticalStrut(5));
        btnPanel.add(descLabel);

        // 包装成 JButton
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.add(btnPanel, BorderLayout.CENTER);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);

        // 悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        // 点击事件
        button.addActionListener(e -> {
            System.out.println("启动游戏模式: " + title);
            // 启动对应游戏
            // new GameFrame(new GameCore(4, 4));
        });

        return button;
    }

    /**
     * 右侧：存档面板
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(220, 0));

        // 标题
        JLabel titleLabel = new JLabel("💾 存档列表");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 存档列表
        DefaultListModel<String> saveModel = new DefaultListModel<>();
        saveModel.addElement("📁 自动存档 - 2025-05-19");
        saveModel.addElement("📁 存档1 - 4x4 经典");
        saveModel.addElement("📁 存档2 - 6x6 挑战");
        saveModel.addElement("📁 云端存档 - 未同步");

        JList<String> saveList = new JList<>(saveModel);
        saveList.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        saveList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        saveList.setFixedCellHeight(40);

        JScrollPane scrollPane = new JScrollPane(saveList);

        // 操作按钮
        JButton loadBtn = new JButton("加载存档");
        loadBtn.setBackground(new Color(76, 175, 80));
        loadBtn.setForeground(Color.WHITE);

        JButton saveBtn = new JButton("保存当前游戏");
        saveBtn.setBackground(new Color(33, 150, 243));
        saveBtn.setForeground(Color.WHITE);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnPanel.add(loadBtn);
        btnPanel.add(saveBtn);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenuFrame();
        });
    }
}