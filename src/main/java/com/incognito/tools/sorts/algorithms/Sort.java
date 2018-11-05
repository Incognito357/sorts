package com.incognito.tools.sorts.algorithms;

import java.awt.Graphics;

public interface Sort {
    void paint(Graphics graphics, int width, int height);
    void stepNext();
    void stepBack();
    void reset();
    boolean isDone();
    String getName();
}
