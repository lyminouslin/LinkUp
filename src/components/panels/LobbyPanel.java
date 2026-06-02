package components.panels;

import components.buttons.MenuButton;
import components.frames.GameFrame;
import data.GlobalData;
import storage.RankStorage;
import storage.SaveStorage;
import ui.WindowManager;
import util.Encryptor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.TreeMap;

public class LobbyPanel extends JPanel {
    private final TopPanel topPanel;
    private final LeftPanel leftPanel;
    private final RightPanel rightPanel;
    private final CenterPanel centerPanel;

    public LobbyPanel() {
        setLayout(new BorderLayout());
        topPanel = new TopPanel();
        leftPanel = new LeftPanel();
        rightPanel = new RightPanel();
        centerPanel = new CenterPanel();
        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshSaveList() {
        rightPanel.refreshSaveList();
    }

    public void refreshRankList() {
        leftPanel.refreshRankList();
    }
}
class TopPanel extends JPanel {
    /**
     * 顶部：用户信息面板
     */
    public TopPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setBackground(new Color(240, 248, 255));  // 浅蓝色背景
        // 左侧：欢迎语
        JPanel leftPanel = getLeftPanel();

        // 右侧：头像和设置
        JButton avatarBtn = new JButton("👤 更换头像");
        avatarBtn.setFont(new Font("Dialog", Font.PLAIN, 12));

        JButton settingsBtn = new JButton("⚙ 设置");
        settingsBtn.setFont(new Font("Dialog", Font.PLAIN, 12));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(avatarBtn);
        rightPanel.add(settingsBtn);

        add(leftPanel, BorderLayout.WEST);
//        add(rightPanel, BorderLayout.EAST); // 右侧功能待实现
    }

    private static JPanel getLeftPanel() {
        JLabel welcomeLabel = new JLabel("欢迎回来，玩家：");
        welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JLabel usernameLabel = new JLabel(Objects.requireNonNullElse(GlobalData.currentUsername, "游客"));
        usernameLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        usernameLabel.setForeground(new Color(0, 100, 200));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(welcomeLabel);
        leftPanel.add(usernameLabel);
        return leftPanel;
    }
}
class LeftPanel extends JPanel {
    private final DefaultListModel<String> listModel;
    public LeftPanel() {
        listModel = new DefaultListModel<>();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(200, 0));
        refreshRankBoard();
    }
    private void refreshRankBoard() {
        removeAll();
        JLabel titleLabel = new JLabel("🏆 积分排行榜");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        refreshRankList();

        JList<String> rankList = new JList<>(listModel);
        rankList.setFont(new Font("Dialog", Font.PLAIN, 13));
        rankList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        rankList.setFixedCellHeight(35);

        JScrollPane scrollPane = new JScrollPane(rankList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    public void refreshRankList() {
        if (!listModel.isEmpty()) listModel.removeAllElements();
        TreeMap<RankStorage.RankData, Integer> rankMap;
        // 排行榜列表
        try {
            rankMap = RankStorage.loadAllRanks();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        var ref = new Object() {
            int count = 0;
        };
        rankMap.forEach((rankData, rank) -> {
            String username = rankData.getUsername();
            int totalScore = rankData.getTotalScore();
            String hashcode =  rankData.getHashcode();
            if (!Encryptor.encrypt(username + totalScore).equals(hashcode)) {
                JOptionPane.showMessageDialog(this, "您的排行数据异常！将被重置为0分.");
                rankData.setTotalScore(0);
                totalScore = 0;
            }
            GlobalData.currentRankData.put(username, rankData);
            ref.count++;
            String rankString = ref.count + ".  " + username + " - " + totalScore + "分";
            listModel.addElement(rankString);
        });
    }
}
class RightPanel extends JPanel {
    private final DefaultListModel<String> saveModel;
    public RightPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(220, 0));

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
        loadBtn.addActionListener(e -> SaveLoader.loadSelectedSave(this));

        JPanel btnPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnPanel.add(loadBtn);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
    public void refreshSaveList() {
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
    private void addSaveInfo(boolean mode) {
        SaveStorage.SaveData saveData;
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
}
class CenterPanel extends JPanel {
    public CenterPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(250, 250, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 标题
        JLabel titleLabel = new JLabel("选择游戏模式");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 150));
        add(titleLabel, gbc);

        // 模式选项
        gbc.gridy++;
        MenuButton classicBtn = new MenuButton("🎮 经典模式", MenuButton.Theme.SUCCESS);
        classicBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        add(classicBtn, gbc);
        classicBtn.addActionListener(e -> {
            GameFrame gameWindow = new GameFrame(false);
            WindowManager.switchTo(gameWindow);
        });


        gbc.gridy++;
        MenuButton hardBtn = new MenuButton("🔥 困难模式", MenuButton.Theme.DANGER);
        hardBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        add(hardBtn, gbc);
        hardBtn.addActionListener(e -> {
            GameFrame gameWindow = new GameFrame(true);
            WindowManager.switchTo(gameWindow);
        });

        gbc.gridy++;
        MenuButton loadSaveBtn = new MenuButton("💾 加载存档", MenuButton.Theme.PRIMARY);
        loadSaveBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        add(loadSaveBtn, gbc);
        loadSaveBtn.addActionListener(e -> SaveLoader.loadSelectedSave(this));
    }
}
class SaveLoader {
    //负责加载选中的存档
    public static void loadSelectedSave(JPanel panel) {
        if (!GlobalData.isLoggedIn || GlobalData.currentUsername == null) {
            JOptionPane.showMessageDialog(panel, "只有注册并登录的用户可以加载存档。");
            return;
        }

        String[] options = {"简单模式存档", "困难模式存档"};
        int choice = JOptionPane.showOptionDialog(
                panel,
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
        SaveStorage.SaveData saveData;
        try {
            saveData = SaveStorage.loadSave(GlobalData.currentUsername, mode);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(panel, "存档被篡改过");
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, "存档无效");
            return;
        }
        if (saveData == null) {
            JOptionPane.showMessageDialog(panel, "当前用户还没有" + (mode ? "困难模式" : "简单模式") + "存档。");
            return;
        }

        GameFrame gameWindow = new GameFrame(saveData.mode, saveData);
        WindowManager.switchTo(gameWindow);
    }
}
