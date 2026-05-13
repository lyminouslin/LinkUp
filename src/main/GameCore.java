package main;

import util.Utils.Pair;
import java.util.ArrayList;
import java.util.HashSet;

public class GameCore {
    private final int[][] grid;
    private final int rows;
    private final int cols;
    public final int pattern_number;

    public ArrayList<Pair> Points;
    public HashSet<Pair> pointsMap;

    public GameCore(int rows, int cols, int pattern_number) {
        this.rows = rows;
        this.cols = cols;
        this.pattern_number = pattern_number;
        grid = new int[rows][cols];
        Points = new ArrayList<>();
        pointsMap = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = 0;
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getGrid(int i, int j) {
        return grid[i][j];
    }
    public void setGrid(int i, int j, int value) {
        this.grid[i][j] = value;
    }

    public void showGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void resetGrid() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                grid[i][j] = 0;
    }
    public boolean isAllEliminated() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (grid[i][j] != 0) return false;
        return true;
    }


}
