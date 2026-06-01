package ui;

import components.frames.NeoGameFrame;

import javax.swing.*;

public class GameUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameUI::showGameMenu);
    }
    public static void showGameMenu() {
        NeoGameFrame gameFrame = new NeoGameFrame(false);
        WindowManager.setMainFrame(gameFrame);
        WindowManager.showMainWindow();
    }
}
