package com.incognito.tools.sorts.algorithms;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class BubbleSort implements Sort {
    private final List<Integer> unsorted;
    private final List<Integer> array = new ArrayList<>();
    private final int maxVal;
    private int i = 0;
    private int j = 0;
    private int stepCount = 0;
    private int swapCount = 0;
    private int compareCount = 0;
    private State state = State.COMPARE;

    private enum State {
        COMPARE,
        SWAP,
        NEXT,
        DONE
    }

    public BubbleSort(List<Integer> array) {
        unsorted = array;
        this.array.addAll(array);
        maxVal = array.stream().mapToInt(v -> v).max().getAsInt();
    }

    @Override
    public void paint(Graphics graphics, int width, int height) {
        int barWidth = (int)((double)width / array.size());
        int barScale = (int)((double)height / maxVal);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);

        for (int x = 0; x < array.size(); x++){
            if (state == State.DONE){
                graphics.setColor(Color.GREEN);
            } else if (x == j || x == j + 1){
                graphics.setColor(state == State.SWAP ? Color.RED : Color.YELLOW);
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

        graphics.setColor(Color.WHITE);
        graphics.drawString("Steps: " + stepCount, 5, 15);
        graphics.drawString("Swaps: " + swapCount, 5, 30);
        graphics.drawString("Comparisons: " + compareCount, 5, 45);
    }

    @Override
    public void stepNext() {
        stepCount++;
        switch(state){
            case COMPARE:
                if (array.get(j) > array.get(j + 1)){
                    state = State.SWAP;
                } else {
                    state = State.NEXT;
                }
                compareCount++;
                break;
            case SWAP:
                int t = array.get(j);
                array.set(j, array.get(j + 1));
                array.set(j + 1, t);
                state = State.NEXT;
                swapCount++;
                break;
            case NEXT:
                j++;
                if (j == array.size() - 1){
                    i++;
                    if (i == array.size() - 1){
                        state = State.DONE;
                        break;
                    }
                    j = 0;
                }
                state = State.COMPARE;
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
        stepCount = 0;
        swapCount = 0;
        compareCount = 0;
        state = State.COMPARE;
        array.clear();
        array.addAll(unsorted);
    }

    @Override
    public boolean isDone() {
        return state == State.DONE;
    }

    @Override
    public String getName() {
        return "Bubble Sort";
    }
}
