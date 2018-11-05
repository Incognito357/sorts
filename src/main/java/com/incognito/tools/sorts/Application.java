package com.incognito.tools.sorts;

import com.incognito.tools.sorts.ui.MainWindow;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Application {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new MainWindow();
    }
}
