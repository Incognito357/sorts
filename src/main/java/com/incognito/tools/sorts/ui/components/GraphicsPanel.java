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
package com.incognito.tools.sorts.ui.components;

import com.incognito.tools.sorts.algorithms.Sort;

import javax.swing.JPanel;
import java.awt.Graphics;

/**
 * Created by jahorton on 10/30/2018
 */
public class GraphicsPanel extends JPanel {
    private Sort sort = null;
    private boolean isPlaying = false;

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    public void paint(Graphics g) {
        if (sort != null) sort.paint(g, getWidth(), getHeight());
    }
}
