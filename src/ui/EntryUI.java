package ui;
import components.frames.EntryFrame;

import javax.swing.*;

public class EntryUI {
    // 创建用户启动之后显示的登录窗口
    public static void main(String[] args) {
        SwingUtilities.invokeLater(EntryUI::showEntryMenu);
    }
    public static void showEntryMenu() {
        EntryFrame entryFrame = new EntryFrame();
        WindowManager.setMainFrame(entryFrame);
        WindowManager.showMainWindow();
    }
}
