package com.incognito.tools.sorts.algorithms;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ShaveSort implements Sort {
    private final List<Integer> unsorted;
    private final List<Integer> array = new ArrayList<>();
    private final int maxVal;
    private int i = 0;
    private int j = 0;
    private int k = 0;
    private int stepCount = 0;
    private State state = State.SHAVE_NEXT;
    private final List<Integer> sorted = new ArrayList<>();

    private enum State {
        SHAVE,
        SHAVE_NEXT,
        SHAVE_NEXT_I,
        DONE
    }

    public ShaveSort(List<Integer> array) {
        unsorted = array;
        this.array.addAll(array);
        maxVal = array.stream().mapToInt(v -> v).max().getAsInt();
        sorted.addAll(Collections.nCopies(array.size(), 0));
    }

    @Override
    public void paint(Graphics graphics, int width, int height) {
        int barWidth = (int)((double)width / array.size());
        int barScale = (int)((double)height / maxVal);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);

        Consumer<Integer> drawSorted = x -> {
            if (state == State.DONE){
                graphics.setColor(Color.GREEN);
            } else if (x == j){
                graphics.setColor(Color.MAGENTA);
            } else if (x < k) {
                graphics.setColor(Color.GREEN);
            } else {
                graphics.setColor(Color.PINK);
            }
            int y = sorted.get(x) * barScale;
            graphics.fillRect(x * barWidth, height - y, barWidth, y);
            if (barWidth >= 3) {
                graphics.setColor(Color.BLACK);
                graphics.drawRect(x * barWidth, height - y, barWidth, y);
            }
        };

        Consumer<Integer> drawArray = x -> {
            if (state != State.DONE) {
                if (x == i) {
                    graphics.setColor(Color.CYAN);
                } else {
                    graphics.setColor(Color.BLUE);
                }
                int y = array.get(x) * barScale;
                graphics.fillRect(x * barWidth, height - y, barWidth, y);
                if (barWidth >= 3) {
                    graphics.setColor(Color.BLACK);
                    graphics.drawRect(x * barWidth, height - y, barWidth, y);
                }
            }
        };

        for (int x = 0; x < array.size(); x++){
            if (array.get(x) > sorted.get(x)){
                drawArray.accept(x);
                drawSorted.accept(x);
            } else {
                drawSorted.accept(x);
                drawArray.accept(x);
            }
        }

        graphics.setColor(Color.WHITE);
        graphics.drawString("Steps: " + stepCount, 5, 15);
    }

    @Override
    public void stepNext() {
        stepCount++;
        switch (state){
            case SHAVE:
                int v = array.get(i);
                if (v > 0){
                    array.set(i, v - 1);
                    j--;
                    sorted.set(j, sorted.get(j) + 1);
                }
                state = State.SHAVE_NEXT_I;
                break;
            case SHAVE_NEXT_I:
                i++;
                if (i >= array.size()){
                    state = State.SHAVE_NEXT;
                } else {
                    state = State.SHAVE;
                }
                break;
            case SHAVE_NEXT:
                if (j == array.size()){
                    state = State.DONE;
                } else {
                    i = 0;
                    k = j;
                    j = array.size();
                    state = State.SHAVE;
                }
                break;
            case DONE:
                stepCount--;
                break;
        }
    }

    @Override
    public void stepBack() {

    }

    @Override
    public void reset() {
        i = 0;
        j = 0;
        k = 0;
        stepCount = 0;
        state = State.SHAVE_NEXT;
        array.clear();
        array.addAll(unsorted);
        sorted.clear();
        sorted.addAll(Collections.nCopies(array.size(), 0));
    }

    @Override
    public boolean isDone() {
        return state == State.DONE;
    }

    @Override
    public String getName() {
        return "Shave Sort";
    }
}
