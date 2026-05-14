package core;

import util.Utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameMethods {
    static Random rand = new Random();
    static ArrayList<Integer> bag = null;

    private GameMethods() {
        rand.setSeed(System.currentTimeMillis());
    }

    static private void generatePattern(GameCore game, Pair a, Pair b, ArrayList<Integer> bag) {
        int pattern;
        int x1 = a.x, y1 = a.y, x2 = b.x, y2 = b.y;
        Pair a_ = new Pair(Math.max(x1 - 1, 0), Math.max(y1 - 1, 0));
        Pair b_ = new Pair(Math.min(x2 + 1, game.getRows() - 1), Math.min(y2 + 1, game.getCols() - 1));
        if (x1 == x2) {
//            pattern = rand.nextInt(game.pattern_number) + 1;
            pattern = bag.getFirst();
            bag.removeFirst();
            for (int i = y1; i <= y2; i++) game.setGrid(x1, i, pattern);
            game.pointsMap.add(a);
            game.pointsMap.add(b);
            generatePattern(game, a_, b_, bag);
        }
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                Pair p = new Pair(i, j);
                if (!isCoordinateValid(i, j, game.getRows(), game.getCols())) continue;
                if (game.getGrid(i, j) == 0) { game.Points.add(p);}
            }
        }
        if (game.Points.isEmpty()) return;
        game.Points.sort((o1, o2) -> o1.x - o2.x == 0 ? o1.y - o2.y : o1.x - o2.x);
        for (int i = 1; i < game.Points.size(); i++) {
            Pair p = game.Points.get(i);
            Pair _p = game.Points.get(i - 1);
            if (p.x == _p.x && p.y == _p.y) game.Points.remove(p);
        }
        Collections.shuffle(game.Points);
        while (game.Points.size() > 1) {
            Pair p1 = game.Points.get(0);
            Pair p2 = game.Points.get(1);
            game.Points.remove(p1);
            game.Points.remove(p2);
            pattern = bag.getFirst();
            bag.removeFirst();
            game.setGrid(p1.x, p1.y, pattern);
            game.setGrid(p2.x, p2.y, pattern);
        }
        generatePattern(game, a_, b_, bag);
    }

    public static void generatePattern(GameCore game) {
        int area = game.getCols() * game.getRows();
        bag = buildPatternBag(area / 2, game.pattern_number);
        Pair a, b;
        int init_x = rand.nextInt(game.getRows());
        int init_y = rand.nextInt(game.getCols() - 1);
        a = new Pair(init_x, init_y);
        b = new Pair(init_x, init_y + 1);
        generatePattern(game, a, b, bag);
    }

    public static boolean eliminatePattern(GameCore game, Pair a, Pair b) {
        boolean result = false;
        if (eliminationInvalid(game, a, b)) {
            game.setGrid(a.x, a.y, 0);
            game.setGrid(b.x, b.y, 0);
            System.out.println(a.x + " " + a.y);
            System.out.println(b.x + " " + b.y);
            result = true;
        }
        return result;
    }

    static boolean eliminationInvalid(GameCore game, Pair a, Pair b){
        return findLinkPath(game, a, b) != null;
    }

    public static ArrayList<Pair> findLinkPath(GameCore game, Pair a, Pair b) {
    // 首先排除图案不同、点重合、空格子的情况
    if (game.getGrid(a.x, a.y) != game.getGrid(b.x, b.y)) return null;
    if (a.x == b.x && a.y == b.y) return null;
    if (game.getGrid(a.x, a.y) == 0 || game.getGrid(b.x, b.y) == 0) return null;

    int currentMinLength = Integer.MAX_VALUE;
    ArrayList<Pair> path = new ArrayList<>();

    // 先看横线段能否存在
    for (int i = 0; i < game.getRows(); i++) {
        if (reachableInRow(game, i, a.y, b.y)) {
            // 直线连接
            if (i == a.x && i == b.x) {
                path.clear();
                path.add(a);
                path.add(b);
            }

            // 两次转弯：a到(i, a.y)到(i, b.y)到b
            if (reachableInCol(game, a.x, i, a.y) // 前两个判断条件，表示第一个竖线和第二个竖线之间有没有空隙
                    && reachableInCol(game, i, b.x, b.y)  
                    && (game.getGrid(i, a.y) == 0 || i == a.x) //表示“转折点”空隙
                    && (game.getGrid(i, b.y) == 0 || i == b.x)) {
                path.clear();
                path.add(a);
                path.add(new Pair(i, a.y)); path.add(new Pair(i, b.y));// 加入中间两个转折点
                path.add(b);
            }
        }
    }

    // 再看竖线段能否存在
    for (int i = 0; i < game.getCols(); i++) {
        if (reachableInCol(game, a.x, b.x, i)) {
            // 直线连接
            if (i == a.y && i == b.y) {
                path.clear();
                path.add(a);
                path.add(b);
            }

            // 两次转弯：a到(a.x, i)到(b.x, i)再到b
            if (reachableInRow(game, a.x, i, a.y) // 在a的行，
                    && reachableInRow(game, b.x, b.y, i) // 首先是没有障碍物
                    && (game.getGrid(a.x, i) == 0 || i == a.y)
                    && (game.getGrid(b.x, i) == 0 || i == b.y)) {
                path.clear();
                path.add(a);
                path.add(new Pair(a.x, i));
                path.add(new Pair(b.x, i));
                path.add(b);
            }
        }
    }

    return path;
}


    // 在第x行，第y1和y2列之间是否存在障碍物
    static boolean reachableInRow(GameCore game, int x, int y1, int y2) {
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        for (int i = y1 + 1; i < y2; i++) {
            if (game.getGrid(x, i) != 0) return false;
        }
        return true;
    }

    // 在第y列当中，x1行到x2行之间有咩有障碍
    static boolean reachableInCol(GameCore game, int x1, int x2, int y) {
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        for (int i = x1 + 1; i < x2; i++) {
            if (game.getGrid(i, y) != 0) return false;
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
