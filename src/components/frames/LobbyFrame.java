// ui/MainMenuFrame.java
package components.frames;

import components.panels.LobbyPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static constants.Constants.MENU_LENGTH;
import static constants.Constants.MENU_WIDTH;

public class LobbyFrame extends JFrame {

    public LobbyFrame() {
        setTitle("游戏大厅 - 连连看");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MENU_LENGTH, MENU_WIDTH);
        setLocationRelativeTo(null);

        // 主布局：BorderLayout
        setLayout(new BorderLayout(10, 10));
        LobbyPanel lobbyPanel = new LobbyPanel();
        add(lobbyPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                lobbyPanel.refreshSaveList();
                lobbyPanel.refreshRankList();
            }
        });
    }
}