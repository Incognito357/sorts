package com.incognito.tools.sorts.ui.components;

import com.incognito.tools.sorts.algorithms.Sort;

import javax.swing.JPanel;
import java.awt.Graphics;

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
