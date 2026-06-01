package data;

public record GameStatePack(boolean mode,
                            int score,
                            int leftSeconds,
                            int usedSeconds,
                            int totalPairCount,
                            int remainingPairs,
                            int comboCount,
                            int[][] grid,
                            String text,
                            CellState state1,
                            CellState state2
                            ) {}