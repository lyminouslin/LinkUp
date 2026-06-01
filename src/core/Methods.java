package core;

import util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Methods {
    static Random rand = new Random();
    static ArrayList<Integer> bag = null;

    private Methods() {
        rand.setSeed(System.currentTimeMillis());
    }

    static private void generatePattern(Core core, Pair a, Pair b, ArrayList<Integer> bag) {
        int pattern;
        int x1 = a.x, y1 = a.y, x2 = b.x, y2 = b.y;
        Pair a_ = new Pair(Math.max(x1 - 1, 0), Math.max(y1 - 1, 0));
        Pair b_ = new Pair(Math.min(x2 + 1, core.getRows() - 1), Math.min(y2 + 1, core.getCols() - 1));
        if (x1 == x2) {
//            pattern = rand.nextInt(core.pattern_number) + 1;
            pattern = bag.getFirst();
            bag.removeFirst();
            for (int i = y1; i <= y2; i++) core.setGrid(x1, i, pattern);
            core.pointsMap.add(a);
            core.pointsMap.add(b);
            generatePattern(core, a_, b_, bag);
        }
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                Pair p = new Pair(i, j);
                if (!isCoordinateValid(i, j, core.getRows(), core.getCols())) continue;
                if (core.getGrid(i, j) == 0) { core.points.add(p);}
            }
        }
        if (core.points.isEmpty()) return;
        core.points.sort((o1, o2) -> o1.x - o2.x == 0 ? o1.y - o2.y : o1.x - o2.x);
        for (int i = 1; i < core.points.size(); i++) {
            Pair p = core.points.get(i);
            Pair _p = core.points.get(i - 1);
            if (p.x == _p.x && p.y == _p.y) core.points.remove(p);
        }
        Collections.shuffle(core.points);
        while (core.points.size() > 1) {
            Pair p1 = core.points.get(0);
            Pair p2 = core.points.get(1);
            core.points.remove(p1);
            core.points.remove(p2);
            pattern = bag.getFirst();
            bag.removeFirst();
            core.setGrid(p1.x, p1.y, pattern);
            core.setGrid(p2.x, p2.y, pattern);
        }
        generatePattern(core, a_, b_, bag);
    }

    public static void generatePattern(Core core) {
        int area = core.getCols() * core.getRows();
        bag = buildPatternBag(area / 2, core.patternNumber);
        Pair a, b;
        int init_x = rand.nextInt(core.getRows());
        int init_y = rand.nextInt(core.getCols() - 1);
        a = new Pair(init_x, init_y);
        b = new Pair(init_x, init_y + 1);
        generatePattern(core, a, b, bag);
    }

    public static boolean eliminatePattern(Core core, Pair a, Pair b) {
        boolean result = false;
        if (eliminationInvalid(core, a, b)) {
            core.setGrid(a.x, a.y, 0);
            core.setGrid(b.x, b.y, 0);
            result = true;
        }
        return result;
    }

    static boolean eliminationInvalid(Core core, Pair a, Pair b){
        return findLinkPath(core, a, b) != null;
    }

    static int getLength(int x0, int x1, int x2) { return Math.abs(x1 - x0) + Math.abs(x2 - x0); }

    static int dist(int x1, int x2) {return Math.abs(x1 - x2); }

    public static ArrayList<Pair> findLinkPath(Core core, Pair a, Pair b) {
    // 首先排除图案不同、点重合、空格子的情况
    if (core.getGrid(a.x, a.y) != core.getGrid(b.x, b.y)) return null;
    if (a.x == b.x && a.y == b.y) return null;
    if (core.getGrid(a.x, a.y) == 0 || core.getGrid(b.x, b.y) == 0) return null;

    int currentMinLength = Integer.MAX_VALUE;
    ArrayList<Pair> path = new ArrayList<>();

    // 先看横线段能否存在
    for (int i = 0; i < core.getRows(); i++) {
        if (reachableInRow(core, i, a.y, b.y)) {
            // 直线连接
            if (i == a.x && i == b.x && dist(a.y, b.y) < currentMinLength) {
                currentMinLength = dist(a.y, b.y);
                path.clear();
                path.add(a);
                path.add(b);
            }
            // 两次转弯：a到(i, a.y)到(i, b.y)到b
            if (reachableInCol(core, a.x, i, a.y) // 前两个判断条件，表示第一个竖线和第二个竖线之间有没有空隙
                && reachableInCol(core, i, b.x, b.y)
                && (core.getGrid(i, a.y) == 0 || i == a.x) //表示“转折点”空隙
                && (core.getGrid(i, b.y) == 0 || i == b.x)
                && getLength(i, a.x, b.x) + dist(a.y, b.y) < currentMinLength)
            {
                currentMinLength = getLength(i, a.x, b.x) +  dist(a.y, b.y);
                path.clear();
                path.add(a);
                path.add(new Pair(i, a.y)); path.add(new Pair(i, b.y));// 加入中间两个转折点
                path.add(b);
            }
        }
    }

    // 再看竖线段能否存在
    for (int i = 0; i < core.getCols(); i++) {
        if (reachableInCol(core, a.x, b.x, i)) {
            // 直线连接
            if (i == a.y && i == b.y && dist(a.x, b.x) < currentMinLength) {
                currentMinLength = dist(a.x, b.x);
                path.clear();
                path.add(a);
                path.add(b);
            }
            // 两次转弯：a到(a.x, i)到(b.x, i)再到b
            if (reachableInRow(core, a.x, i, a.y) // 在a的行，
                && reachableInRow(core, b.x, b.y, i) // 首先是没有障碍物
                && (core.getGrid(a.x, i) == 0 || i == a.y)
                && (core.getGrid(b.x, i) == 0 || i == b.y)
                && getLength(i, a.y, b.y) + dist(a.x, b.x) < currentMinLength)
            {
                currentMinLength = getLength(i, a.x, b.x) +  dist(a.y, b.y);
                path.clear();
                path.add(a);
                path.add(new Pair(a.x, i));
                path.add(new Pair(b.x, i));
                path.add(b);
            }
        }
    }

    if (!path.isEmpty()) return path;
    else return null;
}


    // 在第x行，第y1和y2列之间是否存在障碍物
    static boolean reachableInRow(Core core, int x, int y1, int y2) {
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        for (int i = y1 + 1; i < y2; i++) {
            if (core.getGrid(x, i) != 0) return false;
        }
        return true;
    }

    // 在第y列当中，x1行到x2行之间有咩有障碍
    static boolean reachableInCol(Core core, int x1, int x2, int y) {
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        for (int i = x1 + 1; i < x2; i++) {
            if (core.getGrid(i, y) != 0) return false;
        }
        return true;
    }

    static boolean isCoordinateValid(int x, int y, int rows, int cols) {
        return (0 <= x && x < rows && 0 <= y && y < cols);
    }

    static private ArrayList<Integer> buildPatternBag(int pairCount, int differentPatternCount) {
        ArrayList<Integer> bag = new ArrayList<>();

        for (int i = 1; i <= differentPatternCount; i++) {
            bag.add(i);
        }

        while (bag.size() < pairCount) {
            bag.add(rand.nextInt(differentPatternCount) + 1);
        }

        Collections.shuffle(bag);
        return bag;
    }
}
