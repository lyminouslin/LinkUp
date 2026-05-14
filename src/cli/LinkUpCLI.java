package cli;

import constants.Constants;
import core.GameCore;
import core.GameMethods;

import java.util.Random;
import java.util.Scanner;

import static util.Utils.Pair;

public class LinkUpCLI {
    public static void main(String[] args) {
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to the Link Up CLI!");
        System.out.println("Please enter the mode (1.Easy or 2.Hard) would like to play:");

        int type = input.nextInt(), pattern_number = 0;
        if (type == 1) pattern_number = Constants.EASY_PATTERN_NUMBER;
        else if (type == 2) pattern_number = Constants.HARD_PATTERN_NUMBER;

        System.out.println("Please enter the area of the chessboard:(a * b)");

        int rows = input.nextInt();
        int cols = input.nextInt();

        GameCore game = new GameCore(rows, cols, pattern_number);
        GameMethods.generatePattern(game);
        game.showGrid();

        System.out.println("Now I will telling you the rule of this game:");
        System.out.println("First, you need to choose two points, using (x, y) coordinates,");
        System.out.println("Then, I will check if the two points can be linked with less than 3 lines.");
        System.out.println("If so, you eliminate them; else, this operation is invalid.");
        System.out.println("If you eliminate all the blocks, you win.");
        System.out.println("Now Let's PLAY!");

        while (!game.isAllEliminated()) {
            game.showGrid();
            System.out.println("Please enter the coordinates of the first point:");
            int x1 = input.nextInt();
            int y1 = input.nextInt();
            System.out.println("Please enter the coordinates of the second point:");
            int x2 = input.nextInt();
            int y2 = input.nextInt();
            Pair P1 = new Pair(x1, y1);
            Pair P2 = new Pair(x2, y2);
            boolean succeed = GameMethods.eliminatePattern(game, P1, P2);
            if (succeed) {
                System.out.println("The pattern has been eliminated.");

            } else {
                System.out.println("The pattern could not be eliminated.");
            }

        }
    }
}
