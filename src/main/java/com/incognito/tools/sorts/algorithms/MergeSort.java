package com.incognito.tools.sorts.algorithms;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class MergeSort implements Sort {
    private final List<Integer> unsorted;
    private final List<Integer> array = new ArrayList<>();
    private final int maxVal;
    private int curSize = 1;
    private int left = 0;
    private int right = -1;
    private int rightEnd = 0;
    private int i = 0;
    private int j = 0;
    private int k = 0;
    private int stepCount = 0;
    private int compareCount = 0;
    private List<Integer> merged = new ArrayList<>();
    private State state = State.NEXT_SUB;

    public MergeSort(List<Integer> array) {
        unsorted = array;
        this.array.addAll(array);
        maxVal = array.stream().mapToInt(v -> v).max().getAsInt();
    }

    private enum State {
        NEXT_SIZE,
        NEXT_SUB,
        MERGE_COMPARE,
        MERGE_NEXT,
        MERGE_END,
        MERGE_REPLACE,
        MERGE_REPLACE_NEXT,
        DONE
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
            } else if ((x == i && i < right) || (x == j && j < rightEnd)){
                graphics.setColor(Color.YELLOW);
            } else if ((state == State.MERGE_REPLACE || state == State.MERGE_REPLACE_NEXT) && x - left < k){
                graphics.setColor(Color.BLUE);
            } else if (right != -1 && x >= left && x < right) {
                graphics.setColor(Color.CYAN);
            } else if (right != -1 && x >= right && x < rightEnd){
                graphics.setColor(Color.MAGENTA);
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

        if (state != State.DONE) {
            for (int x = 0; x < merged.size(); x++) {
                if (state == State.MERGE_REPLACE || state == State.MERGE_REPLACE_NEXT){
                    if (x < k) {
                        continue;
                    } else if (x == k) {
                        graphics.setColor(Color.RED);
                    } else {
                        graphics.setColor(Color.PINK);
                    }
                } else {
                    if (x == merged.size() - 1) {
                        graphics.setColor(Color.RED);
                    } else {
                        graphics.setColor(Color.PINK);
                    }
                }
                int y = merged.get(x) * barScale;
                graphics.fillRect((x + left) * barWidth, height - y, barWidth, y);
                if (barWidth >= 3) {
                    graphics.setColor(Color.BLACK);
                    graphics.drawRect((x + left) * barWidth, height - y, barWidth, y);
                }
            }
        }

        graphics.setColor(Color.WHITE);
        graphics.drawString("Steps: " + stepCount, 5, 15);
        graphics.drawString("Comparisons: " + compareCount, 5, 30);
        graphics.drawString("Sublist Size: " + curSize, 5, 45);
    }

    @Override
    public void stepNext() {
        stepCount++;
        switch (state){
            case NEXT_SIZE:
                curSize *= 2;
                if (curSize > array.size() - 1) {
                    state = State.DONE;
                    break;
                } else {
                    left = 0;
                    right = -1;
                }
                state = State.NEXT_SUB;
                break;
            case NEXT_SUB:
                if (right != -1){
                    left += curSize * 2;
                    if (left + curSize >= array.size()) {
                        state = State.NEXT_SIZE;
                        break;
                    }
                }
                right = left + curSize;
                rightEnd = right + curSize;
                if (rightEnd > array.size()) rightEnd = array.size();
                merged.clear();
                i = left;
                j = right;
                k = 0;
                state = State.MERGE_COMPARE;
                break;
            case MERGE_NEXT:
                if (i >= right || j >= rightEnd) {
                    state = State.MERGE_END;
                    break;
                }
                state = State.MERGE_COMPARE;
                break;
            case MERGE_COMPARE:
                compareCount++;
                if (array.get(i) <= array.get(j)){
                    merged.add(array.get(i));
                    i++;
                } else {
                    merged.add(array.get(j));
                    j++;
                }
                state = State.MERGE_NEXT;
                break;
            case MERGE_END:
                if (i < right){
                    merged.add(array.get(i));
                    i++;
                }
                if (j < rightEnd){
                    merged.add(array.get(j));
                    j++;
                }
                if (i >= right && j >= rightEnd){
                    state = State.MERGE_REPLACE;
                    k = 0;
                }
                break;
            case MERGE_REPLACE:
                array.set(left + k, merged.get(k));
                state = State.MERGE_REPLACE_NEXT;
                break;
            case MERGE_REPLACE_NEXT:
                k++;
                if (k >= merged.size()){
                    merged.clear();
                    state = State.NEXT_SUB;
                } else {
                    state = State.MERGE_REPLACE;
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
        curSize = 1;
        left = 0;
        right = -1;
        rightEnd = 0;
        i = 0;
        j = 0;
        k = 0;
        stepCount = 0;
        compareCount = 0;
        state = State.NEXT_SUB;
        array.clear();
        array.addAll(unsorted);
        merged.clear();
    }

    @Override
    public boolean isDone() {
        return state == State.DONE;
    }

    @Override
    public String getName() {
        return "Merge Sort";
    }
}
