package ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowManager {
    private static JFrame currentMainFrame;

    // 设置主窗口
    public static void setMainFrame(JFrame frame) {
        currentMainFrame = frame;
    }

    // 切换到新窗口，主窗口隐藏
    public static void switchTo(JFrame newWindow) {
        if (currentMainFrame != null) {
            currentMainFrame.setVisible(false);
        }
        // 监听新窗口关闭事件
        newWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 窗口关闭时，重新显示主窗口
                if (currentMainFrame != null) {
                    currentMainFrame.setVisible(true);
                }
            }
        });

        newWindow.setVisible(true);
    }
    // 显示主窗口
    public static void showMainWindow() {
        if (currentMainFrame != null) {
            currentMainFrame.setVisible(true);
        }
    }
}
