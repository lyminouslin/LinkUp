package main;

import ui.EntryUI;

import javax.swing.*;

public class Main {
    //主程序的函数入口
    public static void main(String[] args) {
        SwingUtilities.invokeLater(EntryUI::showMainMenu);
    }
}
