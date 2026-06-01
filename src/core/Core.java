package core;

import util.Pair;

import java.util.ArrayList;
import java.util.HashSet;

public class Core {
    private final int[][] grid;
    private final int rows;
    private final int cols;
    private int remainingPairs;


    public final int patternNumber;
    public final ArrayList<Pair> points;
    public final HashSet<Pair> pointsMap;

    public Core(int rows, int cols, int pattern_number) {
        this.rows = rows;
        this.cols = cols;
        this.patternNumber = pattern_number;
        grid = new int[rows][cols];
        points = new ArrayList<>();
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
    public int getRemainingPairs() {
        countRemainingPairs();
        return remainingPairs;
    }
    public int[][] getGrid() {
        int[][] returnGrid = new int[rows][cols];
        for (int i = 0; i < rows; i++) System.arraycopy(grid[i], 0, returnGrid[i], 0, cols);
        return returnGrid;
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

    public void countRemainingPairs() {
        int count = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (getGrid(row, col) != 0) {
                    count++;
                }
            }
        }
        remainingPairs = count / 2;
    }
}
