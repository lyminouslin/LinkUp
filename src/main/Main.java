package main;

import ui.Entry;

import javax.swing.*;

public class Main {
    //主程序的函数入口
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Entry::showMainMenu);
    }
}
