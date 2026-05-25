package constants;

import java.awt.*;

public class Constants {
    //私有化构造
    private Constants() {}
    static public final int HARD_PATTERN_NUMBER = 12;//类似c当中的宏
    static public final int EASY_PATTERN_NUMBER = 5;
    static public final int HARD_ROWS = 10;
    static public final int HARD_COLS = 10;
    static public final int EASY_ROWS = 4;
    static public final int EASY_COLS = 9;
    static public final int MENU_LENGTH = 1200;
    static public final int MENU_WIDTH = 900;
    static public final Color[] COLORS = new Color[]{
            new Color(255, 204, 204),
            new Color(255, 235, 179),
            new Color(204, 255, 204),
            new Color(204, 229, 255),
            new Color(230, 204, 255),
            new Color(255, 220, 185),
            new Color(220, 255, 240),
            new Color(240, 240, 180),
            new Color(210, 210, 255),
            new Color(255, 210, 230),
            new Color(210, 255, 255),
            new Color(230, 230, 230)
    };

}
