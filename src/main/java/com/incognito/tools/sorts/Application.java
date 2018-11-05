/**
 * WARNING - This document contains technical data whose export is restricted
 * by the Arms Export Control Act (Title 22, U.S.C., Sec 2751, et seq.) or the
 * Export Administration Act of 1979 (Title 50, U.S.C., App. 2401 et seq), as
 * amended.  Violations of these export laws are subject to severe criminal
 * penalties.  Disseminate in accordance with provisions of DoD Directive
 * 5230.25.
 * <p>
 * DESTRUCTION NOTICE: Destroy by any method that will prevent disclosure of
 * contents or reconstruction of the document.
 */
package com.incognito.tools.sorts;

import com.incognito.tools.sorts.ui.MainWindow;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Created by jahorton on 10/30/2018
 */
public class Application {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new MainWindow();
    }
}
