import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import util.Utils.Pair;

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
    static void generatePattern(GameCore game) {
        int area = game.getCols() * game.getRows();
        bag = buildPatternBag(area / 2, game.pattern_number);
        Pair a, b;
        int init_x = rand.nextInt(game.getRows());
        int init_y = rand.nextInt(game.getCols() - 1);
        a = new Pair(init_x, init_y);
        b = new Pair(init_x, init_y + 1);
        generatePattern(game, a, b, bag);
    }
    static boolean eliminatePattern(GameCore game, Pair a, Pair b) {
        boolean result = false;
        if (eliminationInvalid(game, a, b)) {
            game.setGrid(a.x, a.y, 0);
            game.setGrid(b.x, b.y, 0);
            result = true;
        }
        return result;
    }
    static boolean eliminationInvalid(GameCore game, Pair a, Pair b) {
        // 如果两点要能在两个直角内可达，棋盘上必须存在一条横线段，从x1到x2可达，或竖线段从y1到y2可达。
        // 所有的合法路径都应该是三折线形式，我们引入两个辅助函数进行处理。
        // 首先排除两点不等的情况 和点重合的情况
        if (game.getGrid(a.x, a.y) != game.getGrid(b.x, b.y)) return false;
        if (a == b) return false;
        if (game.getGrid(a.x, a.y) == 0 || game.getGrid(b.x, b.y) == 0) return false;
        // 然后先看横线段能否存在
        for (int i = 0; i < game.getRows(); i++) {
            if (reachableInRow(game, i, a.y, b.y)) {
                if (reachableInCol(game, a.x, i, a.y) && reachableInCol(game, i, b.x, b.y)) return true;
            }
        }
        // 再看竖线段能否存在
        for (int i = 0; i < game.getCols(); i++) {
            if (reachableInCol(game, a.x, b.x, i)) {
                if (reachableInRow(game, a.x, i, a.y) && reachableInRow(game, b.x, b.y, i)) return true;
            }
        }
        return false;
    }
    static boolean reachableInRow(GameCore game, int x, int y1, int y2) {
        if (y1 > y2) {
            int tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        if (y1 == y2) {
            return game.getGrid(x, y1) == 0;
        }
        for (int i = y1 + 1; i < y2; i++) {
            if (game.getGrid(x, i) != 0) return false;
        }
        return true;
    }
    static boolean reachableInCol(GameCore game, int x1, int x2, int y) {
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (x1 == x2) {
            return game.getGrid(x1, y) == 0;
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
        //list：存储的数据为图案编号，然后在后期，例如{1,2,3,4,5}，在后期取出来在棋盘上所表现得效果为
        // 1 1 2 2 3 3 4 4 5 5(忽略换行)，也就是每两个格子放一个图案，图案的编号由list当中的数字决定
        ArrayList<Integer> bag = new ArrayList<>();

        //先确保每一个编号都出现一次
        for (int i = 1; i <= differentPatternCount; i++) {
            bag.add(i);
        }

        //当包里面的图案不足以摆满棋盘的时候就一直加
        while (bag.size() < pairCount) {
            bag.add(rand.nextInt(differentPatternCount) + 1);//考虑到nextInt是从0开始，于是加1
        }

        Collections.shuffle(bag);//打乱顺序
        return bag;
    }
}
