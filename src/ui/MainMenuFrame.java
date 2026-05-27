// ui/MainMenuFrame.java
package ui;

import data.GlobalData;
import storage.SaveStorage;
import storage.SaveStorage.SaveData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static constants.Constants.MENU_LENGTH;
import static constants.Constants.MENU_WIDTH;

public class MainMenuFrame extends JFrame {
    private DefaultListModel<String> saveModel;

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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                refreshSaveList();
            }
        });

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
        avatarBtn.setFont(new Font("Dialog", Font.PLAIN, 12));

        JButton settingsBtn = new JButton("⚙ 设置");
        settingsBtn.setFont(new Font("Dialog", Font.PLAIN, 12));

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
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
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
        rankList.setFont(new Font("Dialog", Font.PLAIN, 13));
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
        RoundedShadowButton classicBtn = new RoundedShadowButton("🎮 经典模式", RoundedShadowButton.Theme.SUCCESS);
        classicBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        panel.add(classicBtn, gbc);
        classicBtn.addActionListener(e -> {
            GameFrame gameWindow = new GameFrame(false);
            WindowManager.switchTo(gameWindow);
        });


        gbc.gridy++;
        RoundedShadowButton hardBtn = new RoundedShadowButton("🔥 困难模式", RoundedShadowButton.Theme.DANGER);
        hardBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        panel.add(hardBtn, gbc);
        hardBtn.addActionListener(e -> {
            GameFrame gameWindow = new GameFrame(true);
            WindowManager.switchTo(gameWindow);
        });

        gbc.gridy++;
        RoundedShadowButton loadSaveBtn = new RoundedShadowButton("💾 加载存档", RoundedShadowButton.Theme.PRIMARY);
        loadSaveBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        panel.add(loadSaveBtn, gbc);
        loadSaveBtn.addActionListener(e -> loadSelectedSave());

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
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 存档列表
        saveModel = new DefaultListModel<>();
        refreshSaveList();

        JList<String> saveList = new JList<>(saveModel);
        saveList.setFont(new Font("Dialog", Font.PLAIN, 12));
        saveList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        saveList.setFixedCellHeight(40);

        JScrollPane scrollPane = new JScrollPane(saveList);

        // 操作按钮
        JButton loadBtn = new JButton("加载存档");
        loadBtn.setBackground(new Color(76, 175, 80));
        loadBtn.setForeground(Color.WHITE);
        loadBtn.addActionListener(e -> loadSelectedSave());

        JPanel btnPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnPanel.add(loadBtn);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    // 负责主菜单右边的存档区域
    private void refreshSaveList() {
        if (saveModel == null) {
            return;
        }

        // 清空列表，检查登录状态，如果未登录，显示提示；如果已登录，尝试加载存档并显示信息
        saveModel.clear();
        if (!GlobalData.isLoggedIn || GlobalData.currentUsername == null) {
            saveModel.addElement("📁 简单模式存档 - 请先登录");
            saveModel.addElement("📁 困难模式存档 - 请先登录");
            return;
        }

        addSaveInfo(false);
        addSaveInfo(true);
    }

    //负责加载选中的存档
    private void loadSelectedSave() {
        if (!GlobalData.isLoggedIn || GlobalData.currentUsername == null) {
            JOptionPane.showMessageDialog(this, "只有注册并登录的用户可以加载存档。");
            return;
        }

        String[] options = {"简单模式存档", "困难模式存档"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "请选择要加载的存档",
                "加载存档",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice == -1) {
            return;
        }

        boolean mode = choice == 1;
        SaveData saveData;
        try {
            saveData = SaveStorage.loadSave(GlobalData.currentUsername, mode);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(this, "存档被篡改过");
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "存档无效");
            return;
        }
        if (saveData == null) {
            JOptionPane.showMessageDialog(this, "当前用户还没有" + (mode ? "困难模式" : "简单模式") + "存档。");
            return;
        }

        GameFrame gameWindow = new GameFrame(saveData);
        WindowManager.switchTo(gameWindow);
    }

    private void addSaveInfo(boolean mode) {
        SaveData saveData;
        try {
            saveData = SaveStorage.loadSave(GlobalData.currentUsername, mode);
        } catch (SecurityException e) {
            saveModel.addElement("存档被篡改过");
            return;
        } catch (Exception e) {
            saveModel.addElement("存档无效");
            return;
        }
        String modeName = mode ? "困难模式" : "简单模式";
        if (saveData == null) {
            saveModel.addElement("📁 " + modeName + "存档 - 空");
        } else {
            saveModel.addElement("📁 " + modeName + "存档 - " + saveData.score + "分，剩余" + saveData.leftSeconds + "秒");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenuFrame();
        });
    }
}
