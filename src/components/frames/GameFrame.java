package components.frames;
import static constants.Constants.*;

import components.buttons.ImageGridCell;
import components.listeners.TimeListener;
import core.Core;
import core.Methods;

import data.GlobalData;
import storage.SaveStorage;
import storage.SaveStorage.SaveData;
import storage.RankStorage;
import storage.RankStorage.RankData;

import ui.WindowManager;
import util.Pair;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;//监听窗口事件
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
//4.27新增四个pack
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;
import java.util.List;


/*panel装button ，button可以显示文字, 文字就是label*/

public class GameFrame extends JFrame {
    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int WIDTH = 960;//4.27更改了下窗口尺寸
    private static final int HEIGHT = 760;

    //补充状态变量
    private final boolean mode;
    private int rows;//棋盘的行列数
    private int cols;
    private int patternCount;//要塞几个图案，简单模式5个，复杂模式12个
    private int totalPairCount;
    private int totalSeconds;//记录时间的参数
    private int leftSeconds;
    private int usedSeconds;
    private int score;//得分
    private int comboCount;//连续消除次数

    private Core core;//存储棋盘数据
    private final JPanel gridPanel;//格子
    private JLabel leftTimeLabel;//剩下时间
    private JLabel usedTimeLabel;//已经用的时间
    private JLabel scoreLabel;//存分数的标签
    private JLabel pairLabel;//剩余对数
    private JLabel progressLabel;//关卡进度
    private JLabel modeLabel;//模式标签
    private JLabel actionLabel;//显示上一步操作
    private Timer timer;
    private ImageIcon[] icons;//保存图案，java.swing自带
    private ImageGridCell[][] cells;//保存格子，再ImageGridCell类当中定义
    private ArrayList<Pair> linkPath;
    private final Stack<Pair> cellStack = new Stack<>();
    /* 构造函数，设置游戏界面，而不是在Main类中 */
    private final List<TimeListener> listeners = new ArrayList<>();

    public void addTimeListener(TimeListener timeListener) {
        listeners.add(timeListener);
    }

    private void notifyTimeChanged(int remainingSeconds) {
        for (TimeListener listener : listeners) {
            listener.onTimeChanged(remainingSeconds);
        }
    }
    private void startCountdown() {
        timer = new Timer(1000, e -> {
            leftSeconds--;
            notifyTimeChanged(leftSeconds);  // 通知所有监听器
            if (leftSeconds <= 0) {
                timer.stop();
//                notifyTimeOut();
            }
        });
        timer.start();
    }

    //5.27日增加了两种构造函数，用于支持存档
    public GameFrame(boolean mode) {
        this(mode, null);
    }

    public GameFrame(SaveData saveData) {
        this(saveData.mode, saveData);
    }

    private GameFrame(boolean mode, SaveData saveData) {
        setTitle("连连看游戏");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        this.mode = mode;
        setLayout(new BorderLayout(10, 10));
        add(createTopPanel(), BorderLayout.NORTH);


        // 创建一个JPanel的对象，但是重写了paint，也就是匿名子类
        gridPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                // 如果画线路径不合法，直接不画
                if (linkPath == null || linkPath.size() < 2 || cells == null) {
                    return;
                }

                // 复制一支画笔，然后颜色为橘色
                Graphics2D pen = (Graphics2D) g.create();
                pen.setColor(Color.ORANGE);// 设置颜色和笔触，我们选择高亮，因此需要把颜色设置成橙色
                pen.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 1; i < linkPath.size(); i++) {
                    Pair p1 = linkPath.get(i - 1);// 第一个端点
                    Pair p2 = linkPath.get(i);// 第二个端点
                    Rectangle r1 = cells[p1.x][p1.y].getBounds();
                    Rectangle r2 = cells[p2.x][p2.y].getBounds();
                    pen.drawLine(r1.x + r1.width / 2, r1.y + r1.height / 2,
                            r2.x + r2.width / 2, r2.y + r2.height / 2);
                } // 从第一个端点的中心到第二个端点的中心之间画一个直线
                pen.dispose();
            }
        };
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        gridPanel.setBackground(new Color(245, 245, 245));
        add(gridPanel, BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);

        String imageUrl = "./resources/1.png";//图片路径
        ImageIcon originalIcon = new ImageIcon(imageUrl);
        ImageIcon scaledIcon;//缩放后的图标

        /*
            检查图片是否能够正常加载
            getImageLoadStatus()获取加载状态
            MediaTracker.COMPLETE表示“加载完成” 
        */
        if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            //将图片缩放到适合网格单元格的大小，保持4/5的比例以留出边距
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                    WIDTH / COLS * 4 / 5,
                    HEIGHT / ROWS * 4 / 5,
                    Image.SCALE_SMOOTH//平滑算法
            );
            scaledIcon = new ImageIcon(scaledImage);
        } else {
            scaledIcon = createPlaceholderIcon();//如果图片加载失败，使用占位图标
        }

        for (int i = 0; i < ROWS * COLS; i++) {
            ImageGridCell cell = new ImageGridCell(scaledIcon);
            cell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cell.toggleRed();
                }
            });
            gridPanel.add(cell);
        }

        add(gridPanel);
        setVisible(true);

        addWindowListener(new WindowAdapter() {//窗口监听器
            @Override
            public void windowClosing(WindowEvent e) {//窗口关闭时执行以下代码
                if (timer != null) {//如果计时器不为null(也就是已经开始了)，就停止计时器，释放资源
                    timer.stop();
                }
            }
        });
        if (saveData == null) {
            restartGame();
        } else {
            loadGame(saveData);
        }

    }

    //上面有一个占位图标，这里创建一个简单的占位图标，显示“Image”字样，颜色是灰色
    private JPanel createTopPanel() {
        //两行三列，行列间距都是10，外边距上15下0左15右15
        JPanel topPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        //创建标签
        modeLabel = new JLabel();
        leftTimeLabel = new JLabel();
        usedTimeLabel = new JLabel();
        scoreLabel = new JLabel();
        pairLabel = new JLabel();
        progressLabel = new JLabel();

        //将标签添加到面板中，此时标签是空的，后续会在refreshLabels方法当中更新标签内容
        topPanel.add(modeLabel);
        topPanel.add(leftTimeLabel);
        topPanel.add(usedTimeLabel);
        topPanel.add(scoreLabel);
        topPanel.add(pairLabel);
        topPanel.add(progressLabel);
        return topPanel;
    }

    //底部面板包括重新开始和返回菜单
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        JButton restartButton = new JButton("重新开始");
        JButton saveButton = new JButton("保存存档");// 保存存档
        JButton backButton = new JButton("返回菜单");

        restartButton.addActionListener(e -> restartGame());//为按钮添加监听器，如果检测到被按到那就停止计时同时回到主菜单
        saveButton.addActionListener(e -> saveCurrentGame());
        backButton.addActionListener(e -> {
            if (timer != null) {
                timer.stop();
            }
            saveGameData(false);
            dispose();
            WindowManager.showMainWindow();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.add(restartButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        actionLabel = new JLabel("上一步：暂无操作", SwingConstants.CENTER);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);//按钮在上面
        bottomPanel.add(actionLabel, BorderLayout.SOUTH);//提示在下面
        return bottomPanel;
    }

    private void restartGame() {
        setModeData();//根据模式设置数据
        createPatternIcons();//创建图标
        createBoardData();//创建棋盘数据
        //时间和分数全部归零
        score = 0;
        comboCount = 0;
        usedSeconds = 0;
        leftSeconds = totalSeconds;
        actionLabel.setText("上一步：暂无操作");
        //刷新界面，更新标签，开始计时
        refreshBoard();
        refreshLabels();
        startTimer();
    }

    // 保存当前游戏
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
        data.actionText = actionLabel.getText();
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
    
    // 根据存档恢复游戏
    private void loadGame(SaveData saveData) {
        setModeData(); //根据模式设置数据
        leftSeconds = saveData.leftSeconds;// 时间
        usedSeconds = saveData.usedSeconds;
        score = saveData.score;
        comboCount = saveData.comboCount;// 联消
        linkPath = null;
        cellStack.clear();

        createPatternIcons();
        core = new Core(rows, cols, patternCount);

        // 棋盘
        int[][] savedGrid = saveData.grid;
        for (int row = 0; row < rows && row < savedGrid.length; row++) {
            for (int col = 0; col < cols && col < savedGrid[row].length; col++) {
                core.setGrid(row, col, savedGrid[row][col]);
            }
        }

        String savedAction = saveData.actionText;
        actionLabel.setText(savedAction == null || savedAction.trim().isEmpty() ? "上一步：已加载存档" : savedAction);
        refreshBoard();
        refreshLabels();
        if (leftSeconds > 0 && core.getRemainingPairs() > 0) {
            startTimer();
        }
    }

    private void setModeData() {
        if (mode) {
            rows = 10;
            cols = 10;
            patternCount = HARD_PATTERN_NUMBER;// HARD_PATTERN_NUMBER是12，因此这里把图标的数目改成了12
            totalPairCount = 50;//一共100个格子，所以50对
            totalSeconds = 240;//时间240秒
        } else {
            rows = 4;
            cols = 9;
            patternCount = EASY_PATTERN_NUMBER;
            totalPairCount = 16;
            totalSeconds = 120;
        }
    }

    private void createBoardData() {
        core = new Core(rows, cols, patternCount);
        if (mode) {
            fillHardBoard();//困难模式的棋盘是10行10列的，直接从左到右从上到下依次填充
        } else {
            fillEasyBoard();//简单模式的棋盘是4行9列的，分成两块，每块4行4列，剩下一列空着，依次填充
        }
    }

    /*注意，这里的填充逻辑是最简单的，按照两两成对拜访，后续需要在弄出来可销路径之后更改逻辑*/
    private void fillEasyBoard() {
        core.resetGrid();
        Core game_ = new Core(4, 4, patternCount);
        Methods.generatePattern(game_);
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                core.setGrid(i, j, game_.getGrid(i, j));
        game_.resetGrid();
        Methods.generatePattern(game_);
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                core.setGrid(i, j + 5, game_.getGrid(i, j));

    }

    //逻辑和fillEasyBoard完全相同，只不过是10行10列的棋盘，每行每两列填充同样的图案
    private void fillHardBoard() {
        Methods.generatePattern(core);
    }

    //生成并保存所有图案的图片
    private void createPatternIcons() {
        int iconSize;
        //困难模式大尺寸，简单模式小尺寸
        if (mode) {
            iconSize = 48;
        } else {
            iconSize = 58;
        }

        icons = new ImageIcon[HARD_PATTERN_NUMBER + 1];//因为图案编号是从1开始的，数组长度要加上1，以便和图案编号对应
        for (int i = 1; i <= HARD_PATTERN_NUMBER; i++) {
            icons[i] = loadPatternIcon(i, iconSize);
        }
    }

    /*函数逻辑要后期更改：目前只有两个图片，我们需要12个，我这里只判断了0.jpg和1.jpg的情况*/
    //按照图片编号加载对应的图片图标并返回
    private ImageIcon loadPatternIcon(int index, int iconSize) {
//        if (index == 1 || index == 2) {
//            String path;
//            if (index == 1) {
//                path = "resources/0.png";
//            } else {
//                path = "resources/1.png";
//            }
//
//            File file = new File(path);
//            if (file.exists()) {
//                ImageIcon originalIcon = new ImageIcon(path);//加载对应路径的图片
//                if (originalIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {//如果图片已经加载成功。MediaTracker是一个工具类，用于跟踪媒体对象的加载状态，COMPLETE表示加载完成
//                    Image scaledImage = originalIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
//                    return new ImageIcon(scaledImage);
//                }
//            }
//        }

        //如果不存在对应的图片，就返回一个自己画的图标，函数见下方
        return createDrawnIcon(index, iconSize);
    }

    private ImageIcon createDrawnIcon(int number, int iconSize) {
        //先创建空白画布，icon-size表示宽和高，ARGB表示每个像素点包含alpha(透明度)和rgb三色
        BufferedImage img = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
        //Graphics2D和buffered-image的区别是，前者是一个画笔，可以在后者这个画布上进行绘画
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(COLORS[(number - 1) % COLORS.length]);
        g2d.fillRoundRect(2, 2, iconSize - 4, iconSize - 4, 16, 16);//填充一个圆角矩形，参数分别是左上角坐标，宽高，圆角弧度
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(2, 2, iconSize - 4, iconSize - 4, 16, 16);

        g2d.setFont(new Font("Arial", Font.BOLD, iconSize / 3));
        String text = String.valueOf(number);
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (iconSize - metrics.stringWidth(text)) / 2;
        int textY = (iconSize - metrics.getHeight()) / 2 + metrics.getAscent();
        g2d.drawString(text, textX, textY);
        g2d.dispose();
        return new ImageIcon(img);
    }

    private void refreshBoard() {
        gridPanel.removeAll(); //把容器和组件全部删除
        gridPanel.setLayout(new GridLayout(rows, cols, 8, 8));//设置新的布局，行列数根据模式设置，格子之间的间距是8
        cells = new ImageGridCell[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int value = core.getGrid(row, col);
                //创建一个新的cell，然后把行列和图案编号（如果有的话）传入，如果value是0就传入null，否则传入对应编号的图标
                ImageGridCell cell = new ImageGridCell(row, col, value == 0 ? null : icons[value]);
                cells[row][col] = cell;

                cell.addMouseListener(new MouseAdapter() {//若用户点击了这个格子，就调用函数
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleCellClick(cell);
                    }
                });

                gridPanel.add(cell);
            }
        }

        //这两个函数一个解决布局没有及时更新的问题，一个解决界面没有及时刷新的问题
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    //处理用户点击到图片后的行为
    private void handleCellClick(ImageGridCell cell) {
//        long start = System.nanoTime();
        int x = cell.getRow();
        int y = cell.getCol();
        Pair currentCell = new Pair(x, y);
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
            cell.setChosen(true);
        } else {
            Pair selectedCell = cellStack.pop();
            //第一次选中格子的坐标
            int _x = selectedCell.x;
            int _y = selectedCell.y;

            // 第二次点到同一个格子，就取消选择
            if (x == _x && y == _y) {
                cells[_x][_y].setChosen(false);
                return;
            }

            int currentValue = core.getGrid(x, y);
            int selectedValue = core.getGrid(_x, _y);
            ArrayList<Pair> path = Methods.findLinkPath(core, currentCell, selectedCell);
            // 两个图案不同：第一个取消，第二个变成新的选中格子
            if (currentValue != selectedValue) {
                comboCount = 0;
                actionLabel.setText("上一步：图案不同，连消中断");
                JOptionPane.showMessageDialog(this, "图案不同，不可连线");
                cells[_x][_y].setChosen(false);
                cells[x][y].setChosen(true);
                cellStack.add(currentCell);
                return;
            }

            cell.setChosen(true);
            Timer timer1 = new Timer(100, e -> {
                // 如果支持连消，那么连续消除系数加1
                if (Methods.eliminatePattern(core, currentCell, selectedCell)) {
                    linkPath = path; // 如果能够消除，那么对于消除的画一下
                    gridPanel.repaint();
                    comboCount++;

                    // 任务4.2：基础分是10分，连消次数大于等于3之后，每一次递增+5分
                    int addScore = 10;
                    if (comboCount >= 3) {
                        addScore = addScore + (comboCount - 2) * 5;
                    }

                    score = score + addScore;
                    actionLabel.setText("上一步：消除了图案" + currentValue + "×2，+" + addScore + "分，连消×" + comboCount);
                    cells[x][y].resetValue();
                    cells[_x][_y].resetValue();
                    refreshLabels();
                } else {
                    comboCount = 0;
                    actionLabel.setText("上一步：图案相同，但不可连线，连消中断");
                    JOptionPane.showMessageDialog(this, "您选择了不可连线的路径");
                    cells[x][y].toggleRed();
                    cells[_x][_y].toggleRed();
                }
                Timer timer2 = new Timer(220, e1 -> {
                    cells[x][y].setChosen(false);
                    cells[_x][_y].setChosen(false);
                    linkPath = null;
                    gridPanel.repaint();
                });
                timer2.setRepeats(false);
                timer2.start();
                core.showGrid();
            });
            timer1.setRepeats(false);
            timer1.start();
        }
    }

    //计时器启动器
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

            refreshLabels();//刷新标签,当前界面不再保留

            if (core.getRemainingPairs() == 0) {
                timer.stop();
                saveGameData(false);
                JOptionPane.showMessageDialog(this, "恭喜！本局游戏结束，您的积分为" + score + "分，用时为" + usedSeconds + "秒");
                updateRank(GlobalData.currentUsername, score);
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

    private void updateRank(String username, int score) {
        TreeMap<RankData, Integer> rankMap;
        try {
            rankMap = RankStorage.loadRanks();
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
            rankMap.put(new RankData(score, username, "123"), 0);
        }
        try {
            RankStorage.saveRanks(rankMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void refreshLabels() {
        int remainingPairs = core.getRemainingPairs();
        int finishedPairs = totalPairCount - remainingPairs;

        if (mode) {
            modeLabel.setText("当前模式：困难模式");
        } else {
            modeLabel.setText("当前模式：简单模式");
        }

        leftTimeLabel.setText("剩余时间：" + leftSeconds + " 秒");
        usedTimeLabel.setText("已用时间：" + usedSeconds + " 秒");
        scoreLabel.setText("当前分数：" + score);
        pairLabel.setText("剩余可消除对数：" + remainingPairs);
        progressLabel.setText("关卡进度：" + finishedPairs + "/" + totalPairCount);
    }

    private static ImageIcon createPlaceholderIcon() {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, 100, 100);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Image", 30, 50);
        g2d.dispose();
        return new ImageIcon(img);
    }
}

