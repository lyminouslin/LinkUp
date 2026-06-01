package ui;

import components.frames.LobbyFrame;

import javax.swing.SwingUtilities;

public class LobbyUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LobbyUI::showMainMenu);
    }
    public static void showMainMenu() {
        LobbyFrame mainMenuFrame = new LobbyFrame();
        WindowManager.setMainFrame(mainMenuFrame);
        WindowManager.showMainWindow();
    }
}
