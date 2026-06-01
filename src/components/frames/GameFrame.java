package components.frames;

import components.listeners.CellClickListener;
import components.panels.GamePanel;
import constants.Constants;
import core.Core;
import core.Methods;
import data.CellState;
import data.GameStatePack;
import data.GlobalData;
import storage.RankStorage;
import storage.SaveStorage;
import storage.SaveStorage.SaveData;
import ui.WindowManager;
import util.AudioPlayer;
import util.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

import static constants.Constants.*;

public class GameFrame extends JFrame implements CellClickListener {
    private int rows;
    private int cols;
    private int totalPairCount;
    private int totalSeconds;
    private int patternCount;
    private int score;
    private int comboCount;
    private int usedSeconds;
    private int leftSeconds;
    private int remainingPairs;

    private final boolean mode;

    private final GamePanel panel;
    private Core core;
    private Timer timer;
    private final Stack<Pair> cellStack;

    public GameFrame(boolean mode) {
        this(mode, null);
    }

    public GameFrame(boolean mode, SaveData saveData) {
        setTitle("连连看游戏");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(Constants.WIDTH, Constants.HEIGHT);
        setLocationRelativeTo(null);
        cellStack = new Stack<>();
        this.mode = mode;
        setLayout(new BorderLayout(10, 10));

        panel = new GamePanel(mode);
        panel.setOnRestart(this::restartGame);
        panel.setOnBack(this::backToMenu);
        panel.setOnSave(this::saveCurrentGame);
        panel.setOnCellClick(this);
        add(panel, BorderLayout.CENTER);

        if (saveData == null) restartGame();
        else loadGame(saveData);

        AudioPlayer.startBackgroundMusic();
    }
    public GameStatePack buildDataPack(String text, CellState state1, CellState state2) {
        return new GameStatePack(
                mode,
                score,
                leftSeconds,
                usedSeconds,
                totalPairCount,
                remainingPairs,
                comboCount,
                core.getGrid(),
                text,
                state1,
                state2
        );
    }
    public void onCellClick(int x, int y) {
        Pair currentCell = new Pair(x, y);
        GameStatePack gameStatePack;

        //如果时间已经到了，就不处理
        if (leftSeconds <= 0) {
            return;
        }

        //如果点击了空格子，就不处理
        if (core.getGrid(x, y) == 0) {
            return;
        }

        // 如果玩家当前没有选中的格子
        if (cellStack.isEmpty()) {
            cellStack.add(currentCell);
            gameStatePack = buildDataPack(null, new CellState(x, y, CHOSEN), null);
            panel.updateDisplay(gameStatePack);
            return;
        }
        Pair selectedCell = cellStack.pop();
        //第一次选中格子的坐标
        int _x = selectedCell.x();
        int _y = selectedCell.y();

        // 第二次点到同一个格子，就取消选择
        if (x == _x && y == _y) {
            gameStatePack = buildDataPack(null, new CellState(x, y, CANCEL_CHOSEN), null);
            panel.updateDisplay(gameStatePack);
            return;
        }

        int currentValue = core.getGrid(x, y);
        int selectedValue = core.getGrid(_x, _y);
        ArrayList<Pair> path = Methods.findLinkPath(core, currentCell, selectedCell);

        // 两个图案不同：第一个取消，第二个变成新的选中格子

        if (currentValue != selectedValue) {
            comboCount = 0;
            JOptionPane.showMessageDialog(this, "图案不同，不可连线");
            gameStatePack = buildDataPack(
                    "上一步：图案不同，连消中断",
                    new CellState(x, y, CHOSEN),
                    new CellState(_x, _y, CANCEL_CHOSEN));
            cellStack.add(currentCell);
            panel.updateDisplay(gameStatePack);
            return;
        }

        gameStatePack = buildDataPack(null, new CellState(x, y, CHOSEN), null);
        panel.updateDisplay(gameStatePack);
        Timer timer1 = new Timer(50, e -> {
            GameStatePack pathPack;
            // 如果支持连消，那么连续消除系数加1
            if (Methods.eliminatePattern(core, currentCell, selectedCell)) {
                panel.updateLinkPath(path); // 如果能够消除，那么对于消除的画一下
                comboCount++;

                // 任务4.2：基础分是10分，连消次数大于等于3之后，每一次递增+5分
                int addScore = 10;
                if (comboCount >= 3) {
                    addScore = addScore + (comboCount - 2) * 5;
                }

                score = score + addScore;
                remainingPairs--;
                String text = "上一步：消除了图案" + currentValue + "×2，+" + addScore + "分，连消×" + comboCount;
                pathPack = buildDataPack(text,
                        new CellState(x, y, RESET),
                        new CellState(_x, _y, RESET));
                panel.updateDisplay(pathPack);
                AudioPlayer.playSuccess();

            } else {
                comboCount = 0;
                JOptionPane.showMessageDialog(this, "您选择了不可连线的路径");
                String text = "上一步：图案相同，但不可连线，连消中断";
                pathPack = buildDataPack(
                        text,
                        new CellState(x, y, FAIL),
                        new CellState(_x, _y, FAIL));
                panel.updateDisplay(pathPack);
                AudioPlayer.playError();
            }
            Timer timer2 = new Timer(150, e1 -> {
                GameStatePack resetPack = buildDataPack(
                        null,
                        new CellState(x, y, CANCEL_CHOSEN),
                        new CellState(_x, _y, CANCEL_CHOSEN)
                );
                panel.updateLinkPath(null);
                panel.updateDisplay(resetPack);
            });
            timer2.setRepeats(false);
            timer2.start();
            core.showGrid();
        });
        timer1.setRepeats(false);
        timer1.start();
    }
    // 根据存档恢复游戏
    private void loadGame(SaveData saveData) {
        setModeData(); //根据模式设置数据
        leftSeconds = saveData.leftSeconds;// 时间
        usedSeconds = saveData.usedSeconds;
        score = saveData.score;
        comboCount = saveData.comboCount;// 联消

        cellStack.clear();
        core = new Core(rows, cols, patternCount);

        // 棋盘
        int[][] savedGrid = saveData.grid;
        for (int row = 0; row < rows && row < savedGrid.length; row++) {
            for (int col = 0; col < cols && col < savedGrid[row].length; col++) {
                core.setGrid(row, col, savedGrid[row][col]);
            }
        }
        String savedAction = saveData.actionText;
        String text = savedAction == null || savedAction.trim().isEmpty() ? "上一步：已加载存档" : savedAction;
        GameStatePack recoverPack = buildDataPack(text, null, null);
        panel.updateDisplay(recoverPack);

        if (leftSeconds > 0 && core.getRemainingPairs() > 0) {
            startTimer();
        }
    }
    private void backToMenu() {
        if (timer != null) {
            timer.stop();
        }
        AudioPlayer.stopBackgroundMusic();
        saveGameData(false);
        dispose();
        WindowManager.showMainWindow();
    }
    private void saveCurrentGame() {
        // 停止计时器，保存游戏状态，然后恢复计时器
        boolean wasRunning = timer != null && timer.isRunning();
        if (wasRunning) {
            timer.stop();
        }

        try {
            saveGameData(true);
        } finally {
            if (wasRunning && leftSeconds > 0 && core.getRemainingPairs() > 0) {
                timer.start();
            }
        }
    }
    private void restartGame() {
        setModeData();//根据模式设置数据
//        createBoardData();//创建棋盘数据
        // 时间和分数全部归零
        score = 0;
        comboCount = 0;
        usedSeconds = 0;
        leftSeconds = totalSeconds;
        remainingPairs = totalPairCount;
        fillCore();
//        actionLabel.setText("上一步：暂无操作");
        //刷新界面，更新标签，开始计时
        GameStatePack initPack = buildDataPack("INIT", null, null);
        panel.updateDisplay(initPack);
        startTimer();
    }
    private void setModeData() {
        if (mode == HARD) {
            rows = HARD_ROWS;
            cols = HARD_COLS;
            patternCount = HARD_PATTERN_NUMBER;
            totalPairCount = 50; //一共100个格子，所以50对
            totalSeconds = 240; //时间240秒
            fillHardCore();
        } else {
            rows = EASY_ROWS;
            cols = EASY_COLS;
            patternCount = EASY_PATTERN_NUMBER;
            totalPairCount = 16;
            totalSeconds = 120;
            fillEasyCore();
        }
    }
    private void fillEasyCore() {
        Core zone1 = new Core(rows, cols / 2, patternCount);
        Core zone2 = new Core(rows, cols / 2, patternCount);
        Methods.generatePattern(zone1);
        Methods.generatePattern(zone2);
        core = new Core(rows, cols, patternCount);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols / 2; j++) {
                core.setGrid(i, j, zone1.getGrid(i, j));
                core.setGrid(i, j + ((cols + 1) / 2), zone2.getGrid(i, j));
            }
        }
        core.showGrid();
    }
    private void fillCore() {
        if (mode == HARD) fillHardCore();
        else fillEasyCore();
    }
    private void fillHardCore() {
        core = new Core(rows, cols, patternCount);
    }

    private void startTimer() {
        //如果此前有活跃的计时器，停掉
        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(1000, e -> {//每一秒钟执行代码，一旦检测到计时器被执行就指向以下的内容
            usedSeconds++;
            leftSeconds--;

            if (leftSeconds < 0) {//如果时间小于0了，就设定为0
                leftSeconds = 0;
            }

            GameStatePack pack = buildDataPack(null, null, null);

            panel.updateDisplay(pack);

            if (core.getRemainingPairs() == 0) {
                timer.stop();
                saveGameData(false);
                JOptionPane.showMessageDialog(this, "恭喜！本局游戏结束，您的积分为" + score + "分，用时为" + usedSeconds + "秒");
                updateRank(GlobalData.currentUsername, score);
                AudioPlayer.stopBackgroundMusic();
                dispose();
                WindowManager.showMainWindow();
            }
            if (leftSeconds == 0) {
                timer.stop();
                saveGameData(false);
                JOptionPane.showMessageDialog(this, "失败：时间到，本局游戏结束。");
                restartGame();
            }
        });
        timer.start();
    }
    private void saveGameData(boolean showMessage) {
        if (!GlobalData.isLoggedIn || GlobalData.currentUsername == null) {
            if (showMessage) {
                JOptionPane.showMessageDialog(this, "只有注册并登录的用户可以使用存档功能。");
            }
            return;
        }
        try {
            SaveStorage.save(GlobalData.currentUsername, createSaveData());
            if (showMessage) {
                JOptionPane.showMessageDialog(this, "存档成功，已保存到" + (mode ? "困难模式" : "简单模式") + "存档。");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "存档失败：" + ex.getMessage());
        }
    }
    // 创建存档对象
    private SaveData createSaveData() {
        SaveData data = new SaveData();
        data.mode = mode;
        data.leftSeconds = leftSeconds;
        data.usedSeconds = usedSeconds;
        data.score = score;
        data.comboCount = comboCount;
        data.grid = copyCurrentGrid();
        data.actionText = panel.getPack().text();
        return data;
    }
    // 复制当前棋盘
    private int[][] copyCurrentGrid() {
        int[][] grid = new int[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col] = core.getGrid(row, col);
            }
        }
        return grid;
    }
    private void updateRank(String username, int score) {
        if (username == null) return;
        TreeMap<RankStorage.RankData, Integer> rankMap;
        try {
            rankMap = RankStorage.loadAllRanks();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (GlobalData.currentRankData.containsKey(username)) {
            int currentScore = GlobalData.currentRankData.get(username).getTotalScore();
            rankMap.remove(GlobalData.currentRankData.get(username));
            GlobalData.currentRankData.get(username).setTotalScore(currentScore + score);
            rankMap.put(GlobalData.currentRankData.get(username), score);
        }
        else {
            rankMap.put(new RankStorage.RankData(score, username), 0);
        }
        try {
            RankStorage.saveRanks(rankMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
