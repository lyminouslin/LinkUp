package ui;

import components.frames.GameFrame;

import javax.swing.*;

public class GameUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameUI::showGameMenu);
    }
    public static void showGameMenu() {
        GameFrame gameFrame = new GameFrame(false);
        WindowManager.switchTo(gameFrame);
    }
}
