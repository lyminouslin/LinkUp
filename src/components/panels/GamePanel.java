package components.panels;

import components.buttons.ImageGridCell;
import static constants.Constants.*;

import components.listeners.CellClickListener;
import components.listeners.GameListener;
import data.GameStatePack;
import util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GamePanel extends JPanel implements GameListener {

    private final GameTopPanel gameTopPanel;
    private final GameBottomPanel bottomPanel;
    private final GameGridPanel gridPanel;
    private GameStatePack pack;
    public GamePanel(boolean mode) {
        setLayout(new BorderLayout());

        gameTopPanel = new GameTopPanel();
        bottomPanel = new GameBottomPanel();
        gridPanel = new GameGridPanel(mode);

        add(gameTopPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        add(gridPanel, BorderLayout.CENTER);
    }
    public void setOnRestart(Runnable onRestart) {
        bottomPanel.setOnRestart(onRestart);
    }

    public void setOnSave(Runnable onSave) {
        bottomPanel.setOnSave(onSave);
    }

    public void setOnBack(Runnable onBack) {
        bottomPanel.setOnBack(onBack);
    }
    public void setOnCellClick(CellClickListener cellClickListener) {
        gridPanel.setClickListener(cellClickListener);
    }
    public GameStatePack getPack() {
        return pack;
    }
    public void updateDisplay(GameStatePack pack) {
        this.pack = pack;
        gameTopPanel.updateDisplay(this.pack);
        bottomPanel.updateDisplay(this.pack);
        gridPanel.updateDisplay(this.pack);
    }
    public void updateLinkPath(ArrayList<Pair> linkPath) {
        gridPanel.setLinkPath(linkPath);
    }

}

class GameTopPanel extends JPanel implements GameListener {
    private final JLabel modeLabel;
    private final JLabel scoreLabel;
    private final JLabel pairLabel;
    private final JLabel progressLabel;
    private final JLabel usedTimeLabel;
    private final JLabel leftTimeLabel;

    public GameTopPanel() {
        setLayout(new GridLayout(2, 3, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        //创建标签
        modeLabel = new JLabel();
        leftTimeLabel = new JLabel();
        usedTimeLabel = new JLabel();
        scoreLabel = new JLabel();
        pairLabel = new JLabel();
        progressLabel = new JLabel();

        //将标签添加到面板中，此时标签是空的，后续会在refreshLabels方法当中更新标签内容
        add(modeLabel);
        add(leftTimeLabel);
        add(usedTimeLabel);
        add(scoreLabel);
        add(pairLabel);
        add(progressLabel);
    }

    private void refreshLabels(GameStatePack pack) {
        int remainingPairs = pack.remainingPairs();
        int totalPairCount = pack.totalPairCount();
        int finishedPairs = totalPairCount - remainingPairs;

        if (pack.mode() == HARD) { modeLabel.setText("当前模式：困难模式");
        } else { modeLabel.setText("当前模式：简单模式");}
        int leftSeconds = pack.leftSeconds();
        int usedSeconds = pack.usedSeconds();
        int score = pack.score();

        leftTimeLabel.setText("剩余时间：" + leftSeconds + " 秒");
        usedTimeLabel.setText("已用时间：" + usedSeconds + " 秒");
        scoreLabel.setText("当前分数：" + score);
        pairLabel.setText("剩余可消除对数：" + remainingPairs);
        progressLabel.setText("关卡进度：" + finishedPairs + "/" + totalPairCount);
    }
    public void updateDisplay(GameStatePack pack) {
        refreshLabels(pack);
    }
}

class GameBottomPanel extends JPanel implements GameListener {
    private Runnable onRestart;
    private Runnable onSave;
    private Runnable onBack;
    private final JButton restartButton;
    private final JButton saveButton;
    private final JButton backButton;
    private final JLabel actionLabel;
    public GameBottomPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        restartButton = new JButton("重新开始");
        saveButton = new JButton("保存存档");// 保存存档
        backButton = new JButton("返回菜单");
        actionLabel = new JLabel("上一步：暂无操作", SwingConstants.CENTER);

        restartButton.addActionListener(e -> {
            if (onRestart != null) {
                onRestart.run();
            }
        });//为按钮添加监听器，如果检测到被按到那就停止计时同时回到主菜单
        saveButton.addActionListener(e -> {
            if (onSave != null) {
                onSave.run();
            }
        });
        backButton.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.add(restartButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.NORTH);//按钮在上面
        add(actionLabel, BorderLayout.SOUTH);//提示在下面
    }
    public void setOnRestart(Runnable onRestart) {  // ← 定义这个方法
        this.onRestart = onRestart;
    }
    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }
    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
    public void updateDisplay(GameStatePack pack) {
        String text = pack.text();
        if (text == null) return;
        if (!text.equals("INIT")) this.actionLabel.setText(text);
        else this.actionLabel.setText("上一步：暂无操作");
    }
}

class GameGridPanel extends JPanel implements GameListener {
    private ArrayList<Pair> linkPath;
    private final int rows;
    private final int cols;
    private ImageIcon[] icons;//保存图案，java.swing自带
    private final ImageGridCell[][] cells;//保存格子，再ImageGridCell类当中定义
    private CellClickListener clickListener;
    public GameGridPanel(boolean mode) {
        linkPath = new ArrayList<>();
        if (mode == HARD) {
            rows = HARD_ROWS;
            cols = HARD_COLS;
        } else {
            rows = EASY_ROWS;
            cols = EASY_COLS;
        }
        cells = new ImageGridCell[rows][cols];
        createPatternIcons(mode);
    }

    private void createPatternIcons(boolean mode) {
        int iconSize;
        //困难模式大尺寸，简单模式小尺寸
        if (mode == HARD) {
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
    private void refreshBoard(GameStatePack pack) {
        if (pack.state1() == null) {
            if (pack.text() == null) return;
            if (!pack.text().equals("INIT")) return;
        }
        removeAll();
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setBackground(new Color(245, 245, 245));
        setLayout(new GridLayout(rows, cols, 8, 8)); //设置新的布局，行列数根据模式设置，格子之间的间距是8
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int value = pack.grid()[row][col];
                int finalRow = row;
                int finalCol = col;
                //创建一个新的cell，然后把行列和图案编号（如果有的话）传入，如果value是0就传入null，否则传入对应编号的图标
                ImageGridCell cell = new ImageGridCell(row, col, value == 0 ? null : icons[value]);
                cells[row][col] = cell;
                cell.addMouseListener(new MouseAdapter() {//若用户点击了这个格子，就调用函数
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        clickListener.onCellClick(finalRow, finalCol);
                    }
                });
                add(cell);
            }
        }
        if (pack.state1() != null) {
            int row = pack.state1().row();
            int col = pack.state1().col();
            switch (pack.state1().state()) {
                case CHOSEN -> cells[row][col].setChosen(true);
                case CANCEL_CHOSEN -> cells[row][col].setChosen(false);
                case FAIL -> cells[row][col].toggleRed();
            }
        }
        if (pack.state2() != null) {
            int row = pack.state2().row();
            int col = pack.state2().col();
            switch (pack.state2().state()) {
                case CHOSEN -> cells[row][col].setChosen(true);
                case CANCEL_CHOSEN -> cells[row][col].setChosen(false);
                case FAIL -> cells[row][col].toggleRed();
            }
        }
        //这两个函数一个解决布局没有及时更新的问题，一个解决界面没有及时刷新的问题
        revalidate();
        repaint();
    }
    public void setLinkPath(ArrayList<Pair> linkPath) {
        this.linkPath = linkPath;
    }
    public void updateDisplay(GameStatePack pack) {
        refreshBoard(pack);
    }
    public void setClickListener(CellClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
