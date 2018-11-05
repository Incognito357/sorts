package com.incognito.tools.sorts.algorithms;

import java.awt.Color;
import java.awt.Graphics;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class QuickSort implements Sort {
    private final List<Integer> unsorted;
    private final List<Integer> array = new ArrayList<>();
    private final int maxVal;
    private int stepCount = 0;
    private int compareCount = 0;
    private int swapCount = 0;
    private int partitionStart = 0;
    private int partitionEnd = 0;
    private int partitionIndex = 0;
    private int pivot = 0;
    private int pivotValue = 0;
    private int i = 0;
    private final Deque<Map.Entry<Integer, Integer>> stack = new ArrayDeque<>();
    private State state = State.NEXT_STACK;
    private static final Object LOCK = new Object();

    public QuickSort(List<Integer> array) {
        unsorted = array;
        this.array.addAll(array);
        maxVal = array.stream().mapToInt(v -> v).max().getAsInt();
    }

    private enum State {
        NEXT_STACK,
        PICK_PIVOT,
        SWAP_PIVOT,
        PARTITION_NEXT,
        PARTITION_COMPARE,
        PARTITION_SWAP,
        PARTITION_FINAL_SWAP,
        ADD_STACK,
        DONE
    }

    @Override
    public void paint(Graphics graphics, int width, int height) {
        int barWidth = (int)((double)width / array.size());
        int barScale = (int)((double)height / maxVal);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);

        for (int x = 0; x < array.size(); x++) {
            if (state == State.DONE) {
                graphics.setColor(Color.GREEN);
            } else if (state == State.PICK_PIVOT && (x == partitionStart || x == pivot || x == partitionEnd)) {
                graphics.setColor(Color.YELLOW);
            } else if (state == State.SWAP_PIVOT && (x == partitionEnd || x == pivot)) {
                graphics.setColor(Color.RED);
            } else if (x == pivot) {
                graphics.setColor(Color.CYAN);
            } else if (x == partitionIndex){
                graphics.setColor(state == State.PARTITION_SWAP || state == State.PARTITION_FINAL_SWAP ? Color.RED : Color.PINK);
            } else if (x == i){
                graphics.setColor(state == State.PARTITION_SWAP || state == State.PARTITION_FINAL_SWAP ? Color.RED : Color.YELLOW);
            } else if (x >= partitionStart && x <= partitionEnd) {
                graphics.setColor(Color.MAGENTA);
            } else {
                int thisX = x;
                synchronized(LOCK) {
                    if (stack.parallelStream().anyMatch(pair -> thisX >= pair.getKey() && thisX <= pair.getValue())) {
                        graphics.setColor(Color.BLUE);
                    } else {
                        graphics.setColor(Color.GREEN);
                    }
                }
            }
            int y = array.get(x) * barScale;
            graphics.fillRect(x * barWidth, height - y, barWidth, y);
            if (barWidth >= 3) {
                graphics.setColor(Color.BLACK);
                graphics.drawRect(x * barWidth, height - y, barWidth, y);
            }
        }

        if (state != State.DONE && state != State.NEXT_STACK){
            int y = height - pivotValue * barScale;
            graphics.setColor(Color.CYAN);
            graphics.drawLine(0, y, width, y);
        }

        graphics.setColor(Color.WHITE);
        graphics.drawString("Steps: " + stepCount, 5, 15);
        graphics.drawString("Comparisons: " + compareCount, 5, 30);
        graphics.drawString("Swaps: " + swapCount, 5, 45);
    }

    @Override
    public void stepNext() {
        stepCount++;
        switch (state){
            case NEXT_STACK:
                if (stack.isEmpty()){
                    state = State.DONE;
                } else {
                    synchronized(LOCK) {
                        Map.Entry<Integer, Integer> indicies = stack.pop();
                        partitionStart = indicies.getKey();
                        partitionEnd = indicies.getValue();
                    }
                    partitionIndex = partitionStart;
                    if (partitionEnd - partitionStart >= 3) {
                        pivot = (int)((partitionStart + partitionEnd) / 2.0 + 0.5);
                        state = State.PICK_PIVOT;
                    } else {
                        state = State.PARTITION_COMPARE;
                        pivot = partitionEnd;
                        pivotValue = array.get(pivot);
                    }
                    i = partitionStart;
                }
                break;
            case PICK_PIVOT:
                pivot = getMedian(array.get(partitionStart), array.get(pivot), array.get(partitionEnd), partitionStart, pivot, partitionEnd);
                pivotValue = array.get(pivot);
                if (pivot != partitionEnd) {
                    state = State.SWAP_PIVOT;
                } else {
                    state = State.PARTITION_COMPARE;
                }
                break;
            case SWAP_PIVOT:
                swap(pivot, partitionEnd);
                state = State.PARTITION_COMPARE;
                pivot = partitionEnd;
                break;
            case PARTITION_NEXT:
                i++;
                if (i >= partitionEnd){
                    state = State.PARTITION_FINAL_SWAP;
                } else {
                    state = State.PARTITION_COMPARE;
                }
                break;
            case PARTITION_COMPARE:
                compareCount++;
                if (array.get(i) <= pivotValue){
                    state = State.PARTITION_SWAP;
                } else {
                    state = State.PARTITION_NEXT;
                }
                break;
            case PARTITION_SWAP:
                swap(i, partitionIndex);
                partitionIndex++;
                state = State.PARTITION_NEXT;
                break;
            case PARTITION_FINAL_SWAP:
                swap(partitionIndex, partitionEnd);
                state = State.ADD_STACK;
                break;
            case ADD_STACK:
                if (partitionIndex - 1 > partitionStart){
                    synchronized(LOCK) {
                        stack.push(new AbstractMap.SimpleEntry<>(partitionStart, partitionIndex - 1));
                    }
                }
                if (partitionIndex + 1 < partitionEnd){
                    synchronized(LOCK) {
                        stack.push(new AbstractMap.SimpleEntry<>(partitionIndex + 1, partitionEnd));
                    }
                }
                state = State.NEXT_STACK;
                break;
            case DONE:
                stepCount--;
                break;
        }
    }

    private void swap(int a, int b){
        swapCount++;
        int t = array.get(a);
        array.set(a, array.get(b));
        array.set(b, t);
    }

    private int getMedian(int a, int b, int c, int ai, int bi, int ci){
        if (a > b){
            if (b > c){
                return bi;
            } else if (a > c){
                return ci;
            } else {
                return ai;
            }
        } else {
            if (a > c){
                return ai;
            } else if (b > c){
                return ci;
            } else {
                return bi;
            }
        }
    }

    @Override
    public void stepBack() {

    }

    @Override
    public void reset() {
        stepCount = 0;
        compareCount = 0;
        swapCount = 0;
        partitionStart = 0;
        partitionEnd = 0;
        partitionIndex = 0;
        pivot = 0;
        pivotValue = 0;
        i = 0;
        state = State.NEXT_STACK;
        array.clear();
        array.addAll(unsorted);
        stack.clear();
        stack.push(new AbstractMap.SimpleEntry<>(0, array.size() - 1));
    }

    @Override
    public boolean isDone() {
        return state == State.DONE;
    }

    @Override
    public String getName() {
        return "Quick Sort";
    }
}
