package util;

import javax.swing.*;
import java.awt.*;

public class DifficultySelector {//逻辑和usermodeselector完全相同
    private DifficultySelector() {
    }

    public static Boolean chooseDifficulty(Component parent) {
        String[] options = {"简单模式", "困难模式", "取消"};
        int result = JOptionPane.showOptionDialog(
            parent,
            "请选择难度",
            "难度选择",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (result == 0) {//false表示简单模式，true代表困难模式
            return false;
        }
        if (result == 1) {
            return true;
        }
        return null;
    }
}
