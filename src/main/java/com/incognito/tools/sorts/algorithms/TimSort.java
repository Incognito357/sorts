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
package com.incognito.tools.sorts.algorithms;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jahorton on 11/5/2018
 */
public class TimSort implements Sort {
    private static final int MIN_MERGE = 32;
    private static final int MIN_GALLOP = 7;

    private final List<Integer> unsorted;
    private final List<Integer> array = new ArrayList<>();
    private final int maxVal;
    private int stepCount = 0;
    private int swapCount = 0;
    private int compareCount = 0;
    private int minGallop = MIN_GALLOP;
    private int mainHi = 0;
    private int mainLo = 0;
    private int mainNumRemaining = 0;
    private int mainMinRunLen = 0;
    private int mainRunLen = 0;
    private int mainForce = 0;
    private int countRunHi = 0;
    private int reverseLo = 0;
    private int reverseHi = 0;
    private int minRunR = 0;
    private int minRunN = 0;
    private int binLo = 0;
    private int binHi = 0;
    private int binStart = 0;
    private int binPivot = 0;
    private int binLeft = 0;
    private int binRight = 0;
    private int binMid = 0;
    private int binN = 0;
    private int mergeN = 0;
    private int mergeI = 0;
    private int mergeBase1 = 0;
    private int mergeLen1 = 0;
    private int mergeBase2 = 0;
    private int mergeLen2 = 0;
    private List<Integer> gallopArray;
    private int gallopKey = 0;
    private int gallopBase = 0;
    private int gallopLen = 0;
    private int gallopHint = 0;
    private int gallopOfs = 1;
    private int gallopLastOfs = 0;
    private int gallopMaxOfs = 0;
    private int gallopM = 0;
    private final ArrayList<Integer> mergeTempArray = new ArrayList<>();
    private final ArrayList<Integer> mergeFakeCopy = new ArrayList<>();
    private int mergeCopySrcI = 0;
    private int mergeCopyDestI = 0;
    private int mergeCopyLen = 0;
    private List<Integer> mergeCopySrc;
    private List<Integer> mergeCopyDest;
    private int mergeCursor1 = 0;
    private int mergeCursor2 = 0;
    private int mergeDest = 0;
    private int mergeCount1 = 0;
    private int mergeCount2 = 0;
    private State state = State.INIT;
    private boolean normal = true;
    private boolean finalMerge = false;
    private boolean mergingLo = true;
    private State callReturn = State.GALLOP_LEFT_INIT;
    private final ArrayList<Pair> stack = new ArrayList<>();

    private class Pair {
        private int runBase;
        private int runLen;

        public Pair(int runBase, int runLen) {
            this.runBase = runBase;
            this.runLen = runLen;
        }

        public int getRunBase() {
            return runBase;
        }

        public void setRunBase(int runBase) {
            this.runBase = runBase;
        }

        public int getRunLen() {
            return runLen;
        }

        public void setRunLen(int runLen) {
            this.runLen = runLen;
        }
    }

    private enum State {
        INIT,
        MAIN_NEXT,
        COUNT_RUN_CHECK,
        COUNT_RUN_DESC_STEP,
        COUNT_RUN_DESC_NEXT,
        COUNT_RUN_ASC_STEP,
        COUNT_RUN_ASC_NEXT,
        REVERSE_STEP,
        REVERSE_NEXT,
        BINARY_SORT_INIT,
        BINARY_SORT_NEXT,
        BINARY_SORT_STEP,
        BINARY_SORT_COMPARE_NEXT,
        BINARY_SORT_COMPARE_STEP,
        BINARY_SORT_COPY_NEXT,
        BINARY_SORT_COPY_STEP,
        MIN_RUN_NEXT,
        MIN_RUN_STEP,
        PUSH_RUN,
        MERGE_COLLAPSE_NEXT,
        MERGE_COLLAPSE_STEP,
        MERGE_COLLAPSE_FORCE_NEXT,
        MERGE_COLLAPSE_FORCE_STEP,
        MERGE_AT_INIT,
        GALLOP_RIGHT_INIT,
        GALLOP_RIGHT_1_NEXT,
        GALLOP_RIGHT_1_STEP,
        GALLOP_RIGHT_2_NEXT,
        GALLOP_RIGHT_2_STEP,
        GALLOP_RIGHT_3_NEXT,
        GALLOP_RIGHT_3_STEP,
        GALLOP_LEFT_INIT,
        GALLOP_LEFT_1_NEXT,
        GALLOP_LEFT_1_STEP,
        GALLOP_LEFT_2_NEXT,
        GALLOP_LEFT_2_STEP,
        GALLOP_LEFT_3_NEXT,
        GALLOP_LEFT_3_STEP,
        MERGE_COPY_NEXT,
        MERGE_COPY_STEP,
        MERGE_LO_INIT,
        MERGE_LO_OUTER_STEP,
        MERGE_LO_INNER_1_NEXT,
        MERGE_LO_INNER_1_STEP,
        MERGE_LO_INNER_2_NEXT,
        MERGE_LO_INNER_2_STEP,
        MERGE_LO_GALLOP_RIGHT_FINISH,
        MERGE_LO_GALLOP_RIGHT_FINISH2,
        MERGE_LO_GALLOP_LEFT_FINISH,
        MERGE_LO_GALLOP_LEFT_FINISH2,
        MERGE_HI_INIT,
        MERGE_HI_OUTER_STEP,
        MERGE_HI_INNER_1_NEXT,
        MERGE_HI_INNER_1_STEP,
        MERGE_HI_INNER_2_NEXT,
        MERGE_HI_INNER_2_STEP,
        MERGE_HI_GALLOP_RIGHT_FINISH,
        MERGE_HI_GALLOP_RIGHT_FINISH2,
        MERGE_HI_GALLOP_LEFT_FINISH,
        MERGE_HI_GALLOP_LEFT_FINISH2,
        DONE
    }

    public TimSort(List<Integer> array) {
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
        graphics.drawString("Comparisons: " + compareCount, 5, 30);
        graphics.drawString("State: " + state, 5, 45);
        if (!normal){
            graphics.drawString("Mini-Sort Mode", 5, 60);
        }
        graphics.drawString("Return: " + callReturn, 5, 60);
    }

    @Override
    public void stepNext() {
        stepCount++;
        switch(state){
            case INIT:
                mainLo = 0;
                mainHi = array.size();
                mainNumRemaining = array.size();
                if (mainNumRemaining < MIN_MERGE){
                    normal = false;
                    countRunStart();
                } else {
                    minRunR = 0;
                    minRunN = array.size();
                    state = State.MIN_RUN_NEXT;
                }
                break;
            case MAIN_NEXT:
                mainLo += mainRunLen;
                mainNumRemaining -= mainRunLen;
                if (mainNumRemaining == 0){
                    state = State.MERGE_COLLAPSE_FORCE_NEXT;
                } else {
                    countRunStart();
                }
                break;
            case COUNT_RUN_CHECK:
                compareCount++;
                if (array.get(countRunHi++) < array.get(mainLo)){
                    state = State.COUNT_RUN_DESC_NEXT;
                } else {
                    state = State.COUNT_RUN_ASC_NEXT;
                }
                break;
            case COUNT_RUN_DESC_NEXT:
                if (countRunHi >= mainHi){
                    reverseStart();
                } else {
                    compareCount++;
                    if (array.get(countRunHi) >= array.get(countRunHi - 1)){
                        reverseStart();
                    } else {
                        state = State.COUNT_RUN_DESC_STEP;
                    }
                }
                break;
            case COUNT_RUN_DESC_STEP:
                countRunHi++;
                state = State.COUNT_RUN_DESC_NEXT;
                break;
            case COUNT_RUN_ASC_NEXT:
                if (countRunHi >= mainHi){
                    countRunFinish();
                } else {
                    compareCount++;
                    if (array.get(countRunHi) < array.get(countRunHi - 1)){
                        countRunFinish();
                    } else {
                        state = State.COUNT_RUN_ASC_STEP;
                    }
                }
                break;
            case COUNT_RUN_ASC_STEP:
                countRunHi++;
                state = State.COUNT_RUN_ASC_NEXT;
                break;
            case REVERSE_NEXT:
                if (reverseLo >= reverseHi) {
                    countRunFinish();
                } else {
                    state = State.REVERSE_STEP;
                }
                break;
            case REVERSE_STEP:
                swapCount++;
                int tmp = array.get(reverseLo);
                array.set(reverseLo++, array.get(reverseHi));
                array.set(reverseHi--, tmp);
                state = State.REVERSE_NEXT;
                break;
            case BINARY_SORT_INIT:
                if (binStart == binLo){
                    binStart++;
                }
                if (binStart >= binHi){
                    if (!normal){
                        state = State.DONE;
                    } else {
                        mainRunLen = mainForce;
                        state = State.PUSH_RUN;
                    }
                } else {
                    state = State.BINARY_SORT_STEP;
                }
                break;
            case BINARY_SORT_NEXT:
                binStart++;
                if (binStart >= binHi){
                    if (!normal){
                        state = State.DONE;
                    } else {
                        mainRunLen = mainForce;
                        state = State.PUSH_RUN;
                    }
                } else {
                    state = State.BINARY_SORT_STEP;
                }
                break;
            case BINARY_SORT_STEP:
                binPivot = array.get(binStart);
                binLeft = binLo;
                binRight = binStart;
                state = State.BINARY_SORT_COMPARE_NEXT;
                break;
            case BINARY_SORT_COMPARE_NEXT:
                if (binLeft >= binRight){
                    binN = binStart - binLeft;
                    state = State.BINARY_SORT_COPY_NEXT;
                } else {
                    binMid = (binLeft + binRight) >>> 1;
                    state = State.BINARY_SORT_COMPARE_STEP;
                }
                break;
            case BINARY_SORT_COMPARE_STEP:
                compareCount++;
                if (binPivot < array.get(binMid)){
                    binRight = binMid;
                } else {
                    binLeft = binMid + 1;
                }
                state = State.BINARY_SORT_COMPARE_NEXT;
                break;
            case BINARY_SORT_COPY_NEXT:
                binN--;
                if (binN < 0){
                    array.set(binLeft, binPivot);
                    state = State.BINARY_SORT_NEXT;
                } else {
                    state = State.BINARY_SORT_COPY_STEP;
                }
                break;
            case BINARY_SORT_COPY_STEP:
                array.set(binLeft + binN + 1, array.get(binLeft + binN));
                state = State.BINARY_SORT_COPY_NEXT;
                break;
            case MIN_RUN_NEXT:
                if (minRunN < MIN_MERGE){
                    mainMinRunLen = minRunN + minRunR;
                    countRunStart();
                } else {
                    state = State.MIN_RUN_STEP;
                }
                break;
            case MIN_RUN_STEP:
                minRunR |= (minRunN & 1);
                minRunN >>= 1;
                state = State.MIN_RUN_NEXT;
                break;
            case PUSH_RUN:
                stack.add(new Pair(mainLo, mainRunLen));
                state = State.MERGE_COLLAPSE_NEXT;
                break;
            case MERGE_COLLAPSE_NEXT:
                if (stack.size() <= 1){
                    state = State.MAIN_NEXT;
                } else {
                    mergeN = stack.size() - 2;
                    state = State.MERGE_COLLAPSE_STEP;
                }
                break;
            case MERGE_COLLAPSE_STEP:
                if (mergeN > 0 && stack.get(mergeN - 1).getRunLen() <= stack.get(mergeN).getRunLen() + stack.get(mergeN + 1).getRunLen()){
                    if (stack.get(mergeN - 1).getRunLen() < stack.get(mergeN + 1).getRunLen()){
                        mergeN--;
                    }
                    mergeI = mergeN;
                    state = State.MERGE_AT_INIT;
                } else if (stack.get(mergeN).getRunLen() <= stack.get(mergeN + 1).getRunLen()){
                    mergeI = mergeN;
                    state = State.MERGE_AT_INIT;
                } else {
                    state = State.MAIN_NEXT;
                }
                break;
            case MERGE_COLLAPSE_FORCE_NEXT:
                finalMerge = true;
                if (stack.size() <= 1){
                    state = State.DONE;
                } else {
                    mergeN = stack.size() - 2;
                    state = State.MERGE_COLLAPSE_FORCE_STEP;
                }
                break;
            case MERGE_COLLAPSE_FORCE_STEP:
                if (mergeN > 0 && stack.get(mergeN - 1).getRunLen() < stack.get(mergeN + 1).getRunLen()){
                    mergeN--;
                }
                mergeI = mergeN;
                state = State.MERGE_AT_INIT;
                break;
            case MERGE_AT_INIT:
                Pair p1 = stack.get(mergeI);
                mergeBase1 = p1.getRunBase();
                mergeLen1 = p1.getRunLen();
                Pair p2 = stack.get(mergeI + 1);
                mergeBase2 = p2.getRunBase();
                mergeLen2 = p2.getRunLen();
                p1.setRunLen(mergeLen1 + mergeLen2);
                if (mergeI == stack.size() - 3){
                    Pair p3 = stack.get(mergeI + 2);
                    p2.setRunBase(p3.getRunBase());
                    p2.setRunLen(p3.getRunLen());
                }
                stack.remove(stack.size() - 1);
                gallopKey = array.get(mergeBase2);
                gallopArray = array;
                gallopBase = mergeBase1;
                gallopLen = mergeLen1;
                gallopHint = 0;
                gallopOfs = 1;
                gallopLastOfs = 0;
                callReturn = State.MERGE_AT_INIT;
                state = State.GALLOP_RIGHT_INIT;
                break;
            case GALLOP_RIGHT_INIT:
                compareCount++;
                if (gallopKey < gallopArray.get(gallopBase + gallopHint)){
                    gallopMaxOfs = gallopHint + 1;
                    state = State.GALLOP_RIGHT_1_NEXT;
                } else {
                    gallopMaxOfs = gallopLen - gallopHint;
                    state = State.GALLOP_RIGHT_2_NEXT;
                }
                break;
            case GALLOP_RIGHT_1_NEXT:
                if (gallopOfs >= gallopMaxOfs){
                    gallopFinish1(State.GALLOP_RIGHT_3_NEXT);
                } else {
                    compareCount++;
                    if (gallopKey >= gallopArray.get(gallopBase + gallopHint - gallopOfs)){
                        gallopFinish1(State.GALLOP_RIGHT_3_NEXT);
                    } else {
                        state = State.GALLOP_RIGHT_1_STEP;
                    }
                }
                break;
            case GALLOP_RIGHT_1_STEP:
                gallopStep(State.GALLOP_RIGHT_1_NEXT);
                break;
            case GALLOP_RIGHT_2_NEXT:
                if (gallopOfs >= gallopMaxOfs){
                    gallopFinish2(State.GALLOP_RIGHT_3_NEXT);
                } else {
                    compareCount++;
                    if (gallopKey < gallopArray.get(gallopBase + gallopHint + gallopOfs)){
                        gallopFinish2(State.GALLOP_RIGHT_3_NEXT);
                    } else {
                        state = State.GALLOP_RIGHT_2_STEP;
                    }
                }
                break;
            case GALLOP_RIGHT_2_STEP:
                gallopStep(State.GALLOP_RIGHT_2_NEXT);
                break;
            case GALLOP_RIGHT_3_NEXT:
                if (gallopLastOfs >= gallopOfs){
                    if (callReturn == State.MERGE_AT_INIT){
                        mergeBase1 += gallopOfs;
                        mergeLen1 -= gallopOfs;
                        if (mergeLen1 == 0){
                            state = finalMerge ? State.MERGE_COLLAPSE_FORCE_NEXT : State.MERGE_COLLAPSE_NEXT;
                        } else {
                            gallopKey = array.get(mergeBase1 + mergeLen1 - 1);
                            gallopArray = array;
                            gallopBase = mergeBase2;
                            gallopLen = mergeLen2;
                            gallopHint = mergeLen2 - 1;
                            gallopOfs = 1;
                            gallopLastOfs = 0;
                            state = State.GALLOP_LEFT_INIT;
                        }
                    } else if (callReturn == State.MERGE_LO_INNER_2_STEP){
                        mergeCount1 = gallopOfs;
                        if (mergeCount1 != 0){
                            mergeCopySrc = mergeTempArray;
                            mergeCopySrcI = mergeCursor1;
                            mergeCopyDest = array;
                            mergeCopyDestI = mergeDest;
                            mergeCopyLen = mergeCount1;
                            callReturn = State.MERGE_LO_GALLOP_RIGHT_FINISH;
                            state = State.MERGE_COPY_NEXT;
                        } else {
                            state = State.MERGE_LO_GALLOP_RIGHT_FINISH2;
                        }
                    } else if (callReturn == State.MERGE_HI_INNER_2_STEP) {
                        mergeCount1 = mergeLen1 - gallopOfs;
                        if (mergeCount1 != 0) {
                            mergeDest -= mergeCount1;
                            mergeCursor1 -= mergeCount1;
                            mergeLen1 -= mergeCount1;
                            mergeFakeCopy.clear();
                            mergeFakeCopy.addAll(array);
                            mergeCopySrc = mergeFakeCopy;
                            mergeCopySrcI = mergeCursor1 + 1;
                            mergeCopyDest = array;
                            mergeCopyDestI = mergeDest + 1;
                            mergeCopyLen = mergeCount1;
                            callReturn = State.MERGE_HI_GALLOP_RIGHT_FINISH;
                            state = State.MERGE_COPY_NEXT;
                        } else {
                            state = State.MERGE_HI_GALLOP_RIGHT_FINISH2;
                        }
                    }
                } else {
                    state = State.GALLOP_RIGHT_3_STEP;
                }
                break;
            case GALLOP_RIGHT_3_STEP:
                gallopM = gallopLastOfs + ((gallopOfs - gallopLastOfs) >>> 1);
                compareCount++;
                if (gallopKey < gallopArray.get(gallopBase + gallopM)){
                    gallopOfs = gallopM;
                } else {
                    gallopLastOfs = gallopM + 1;
                }
                state = State.GALLOP_RIGHT_3_NEXT;
                break;
            case GALLOP_LEFT_INIT:
                compareCount++;
                if (gallopKey > gallopArray.get(gallopBase + gallopHint)){
                    gallopMaxOfs = gallopLen - gallopHint;
                    state = State.GALLOP_LEFT_1_NEXT;
                } else {
                    gallopMaxOfs = gallopHint + 1;
                    state = State.GALLOP_LEFT_2_NEXT;
                }
                break;
            case GALLOP_LEFT_1_NEXT:
                if (gallopOfs >= gallopMaxOfs){
                    gallopFinish2(State.GALLOP_LEFT_3_NEXT);
                } else {
                    compareCount++;
                    if (gallopKey <= gallopArray.get(gallopBase + gallopHint + gallopOfs)){
                        gallopFinish2(State.GALLOP_LEFT_3_NEXT);
                    } else {
                        state = State.GALLOP_LEFT_1_STEP;
                    }
                }
                break;
            case GALLOP_LEFT_1_STEP:
                gallopStep(State.GALLOP_LEFT_1_NEXT);
                break;
            case GALLOP_LEFT_2_NEXT:
                if (gallopOfs >= gallopMaxOfs){
                    gallopFinish1(State.GALLOP_LEFT_3_NEXT);
                } else {
                    compareCount++;
                    if (gallopKey > gallopArray.get(gallopBase + gallopHint - gallopOfs)){
                        gallopFinish1(State.GALLOP_LEFT_3_NEXT);
                    } else {
                        state = State.GALLOP_LEFT_2_STEP;
                    }
                }
                break;
            case GALLOP_LEFT_2_STEP:
                gallopStep(State.GALLOP_LEFT_2_NEXT);
                break;
            case GALLOP_LEFT_3_NEXT:
                if (gallopLastOfs >= gallopOfs){
                    if (callReturn == State.MERGE_AT_INIT){
                        mergeLen2 = gallopOfs;
                        if (mergeLen2 == 0){
                            state = finalMerge ? State.MERGE_COLLAPSE_FORCE_NEXT : State.MERGE_COLLAPSE_NEXT;
                        } else {
                            mergeTempArray.clear();
                            mergeCopySrc = array;
                            mergeCopyDest = mergeTempArray;
                            mergeCopyDestI = 0;
                            if (mergeLen1 <= mergeLen2){
                                mergeCursor1 = 0;
                                mergeCursor2 = mergeBase2;
                                mergeDest = mergeBase1;

                                mergeCopySrcI = mergeBase1;
                                mergeCopyLen = mergeLen1;
                                callReturn = State.MERGE_LO_INIT;
                                state = State.MERGE_COPY_NEXT;
                                mergingLo = true;
                            } else {
                                mergeCursor1 = mergeBase1 + mergeLen1 - 1;
                                mergeCursor2 = mergeLen2 - 1;
                                mergeDest = mergeBase2 + mergeLen2 - 1;

                                mergeCopySrcI = mergeBase2;
                                mergeCopyLen = mergeLen2;
                                callReturn = State.MERGE_HI_INIT;
                                state = State.MERGE_COPY_NEXT;
                                mergingLo = false;
                            }
                        }
                    } else if (callReturn == State.MERGE_LO_INNER_2_STEP){
                        mergeCount2 = gallopOfs;
                        if (mergeCount2 != 0){
                            mergeFakeCopy.clear();
                            mergeFakeCopy.addAll(array);
                            mergeCopySrc = mergeFakeCopy;
                            mergeCopySrcI = mergeCursor2;
                            mergeCopyDest = array;
                            mergeCopyDestI = mergeDest;
                            mergeCopyLen = mergeCount2;
                            callReturn = State.MERGE_LO_GALLOP_LEFT_FINISH;
                            state = State.MERGE_COPY_NEXT;
                        } else {
                            state = State.MERGE_LO_GALLOP_LEFT_FINISH2;
                        }
                    } else if (callReturn == State.MERGE_HI_INNER_2_STEP){
                        mergeCount2 = mergeLen2 - gallopOfs;
                        if (mergeCount1 != 0) {
                            mergeDest -= mergeCount2;
                            mergeCursor2 -= mergeCount2;
                            mergeLen2 -= mergeCount2;
                            mergeCopySrc = mergeFakeCopy;
                            mergeCopySrcI = mergeCursor2 + 1;
                            mergeCopyDest = array;
                            mergeCopyDestI = mergeDest + 1;
                            mergeCopyLen = mergeCount2;
                            callReturn = State.MERGE_HI_GALLOP_LEFT_FINISH;
                            state = State.MERGE_COPY_NEXT;
                        } else {
                            state = State.MERGE_HI_GALLOP_LEFT_FINISH2;
                        }
                    }
                } else {
                    state = State.GALLOP_LEFT_3_STEP;
                }
                break;
            case GALLOP_LEFT_3_STEP:
                gallopM = gallopLastOfs + ((gallopOfs - gallopLastOfs) >>> 1);
                compareCount++;
                if (gallopKey < gallopArray.get(gallopBase + gallopM)){
                    gallopOfs = gallopM;
                } else {
                    gallopLastOfs = gallopM + 1;
                }
                state = State.GALLOP_LEFT_3_NEXT;
                break;
            case MERGE_COPY_NEXT:
                if (mergeCopyLen <= 0){
                    if (mergeCopySrc == mergeFakeCopy){
                        if (mergingLo) {
                            array.set(mergeDest + mergeLen2, mergeTempArray.get(mergeCursor1));
                        } else {
                            array.set(mergeDest, mergeTempArray.get(mergeCursor2));
                        }
                    }
                    state = callReturn;
                } else {
                    state = State.MERGE_COPY_STEP;
                }
                break;
            case MERGE_COPY_STEP:
                if (mergeCopyDest.size() <= mergeCopyDestI) {
                    mergeCopyDest.add(mergeCopySrc.get(mergeCopySrcI));
                } else {
                    mergeCopyDest.set(mergeCopyDestI, mergeCopySrc.get(mergeCopySrcI));
                }
                mergeCopySrcI++;
                mergeCopyDestI++;
                mergeCopyLen--;
                state = State.MERGE_COPY_NEXT;
                break;
            case MERGE_LO_INIT:
                array.set(mergeDest++, array.get(mergeCursor2++));
                if (--mergeLen2 == 0){
                    mergeLoCopy1();
                } else if (mergeLen1 == 1){
                    mergeLoCopy2();
                } else {
                    state = State.MERGE_LO_OUTER_STEP;
                }
                break;
            case MERGE_LO_OUTER_STEP:
                mergeCount1 = 0;
                mergeCount2 = 0;
                state = State.MERGE_LO_INNER_1_STEP;
                break;
            case MERGE_LO_INNER_1_NEXT:
                if ((mergeCount1 | mergeCount2) >= minGallop){
                    state = State.MERGE_LO_INNER_2_STEP;
                } else {
                    state = State.MERGE_LO_INNER_1_STEP;
                }
                break;
            case MERGE_LO_INNER_1_STEP:
                compareCount++;
                if (array.get(mergeCursor2) < mergeTempArray.get(mergeCursor1)){
                    array.set(mergeDest++, array.get(mergeCursor2++));
                    mergeCount2++;
                    mergeCount1 = 0;
                    if (--mergeLen2 == 0) {
                        mergeLoOuterBreak();
                    } else {
                        state = State.MERGE_LO_INNER_1_NEXT;
                    }
                } else {
                    array.set(mergeDest++, mergeTempArray.get(mergeCursor1++));
                    mergeCount1++;
                    mergeCount2 = 0;
                    if (--mergeLen1 == 1){
                        mergeLoOuterBreak();
                    } else {
                        state = State.MERGE_LO_INNER_1_NEXT;
                    }
                }
                break;
            case MERGE_LO_INNER_2_NEXT:
                if (mergeCount1 < MIN_GALLOP | mergeCount2 < MIN_GALLOP){
                    if (minGallop < 0){
                        minGallop = 0;
                    }
                    minGallop += 2;
                    state = State.MERGE_LO_OUTER_STEP;
                } else {
                    state = State.MERGE_LO_INNER_2_STEP;
                }
                break;
            case MERGE_LO_INNER_2_STEP:
                gallopKey = array.get(mergeCursor2);
                gallopArray = mergeTempArray;
                gallopBase = mergeCursor1;
                gallopLen = mergeLen1;
                gallopHint = 0;
                callReturn = State.MERGE_LO_INNER_2_STEP;
                state = State.GALLOP_RIGHT_INIT;
                break;
            case MERGE_LO_GALLOP_RIGHT_FINISH:
                mergeDest += mergeCount1;
                mergeCursor1 += mergeCount1;
                mergeLen1 -= mergeCount1;
                if (mergeLen1 <= 1) {
                    mergeLoOuterBreak();
                } else {
                    state = State.MERGE_LO_GALLOP_RIGHT_FINISH2;
                }
                break;
            case MERGE_LO_GALLOP_RIGHT_FINISH2:
                array.set(mergeDest++, array.get(mergeCursor2++));
                if (--mergeLen2 == 0){
                    mergeLoOuterBreak();
                } else {
                    gallopKey = mergeTempArray.get(mergeCursor1);
                    gallopArray = array;
                    gallopBase = mergeCursor2;
                    gallopLen = mergeLen2;
                    gallopHint = 0;
                    callReturn = State.MERGE_LO_INNER_2_STEP;
                    state = State.GALLOP_LEFT_INIT;
                }
                break;
            case MERGE_LO_GALLOP_LEFT_FINISH:
                mergeDest += mergeCount2;
                mergeCursor2 += mergeCount2;
                mergeLen2 -= mergeCount2;
                if (mergeLen2 == 0){
                    mergeLoOuterBreak();
                } else {
                    state = State.MERGE_LO_GALLOP_LEFT_FINISH2;
                }
                break;
            case MERGE_LO_GALLOP_LEFT_FINISH2:
                array.set(mergeDest++, mergeTempArray.get(mergeCursor1++));
                if (--mergeLen1 == 1){
                    mergeLoOuterBreak();
                } else {
                    minGallop--;
                    state = State.MERGE_LO_INNER_2_NEXT;
                }
                break;
            case MERGE_HI_INIT:
                array.set(mergeDest--, array.get(mergeCursor1--));
                if (--mergeLen1 == 0){
                    mergeHiCopy1();
                } else if (mergeLen2 == 1){
                    mergeHiCopy2();
                } else {
                    state = State.MERGE_HI_OUTER_STEP;
                }
                break;
            case MERGE_HI_OUTER_STEP:
                mergeCount1 = 0;
                mergeCount2 = 0;
                state = State.MERGE_HI_INNER_1_STEP;
                break;
            case MERGE_HI_INNER_1_NEXT:
                if ((mergeCount1 | mergeCount2) >= minGallop){
                    state = State.MERGE_HI_INNER_2_STEP;
                } else {
                    state = State.MERGE_HI_INNER_1_STEP;
                }
                break;
            case MERGE_HI_INNER_1_STEP:
                compareCount++;
                if (mergeTempArray.get(mergeCursor2) < array.get(mergeCursor1)){
                    array.set(mergeDest--, array.get(mergeCursor1--));
                    mergeCount1++;
                    mergeCount2 = 0;
                    if (--mergeLen1 == 0) {
                        mergeHiOuterBreak();
                    } else {
                        state = State.MERGE_HI_INNER_1_NEXT;
                    }
                } else {
                    array.set(mergeDest--, mergeTempArray.get(mergeCursor2--));
                    mergeCount2++;
                    mergeCount1 = 0;
                    if (--mergeLen2 == 1){
                        mergeHiOuterBreak();
                    } else {
                        state = State.MERGE_HI_INNER_1_NEXT;
                    }
                }
                break;
            case MERGE_HI_INNER_2_NEXT:
                if (mergeCount1 < MIN_GALLOP | mergeCount2 < MIN_GALLOP){
                    if (minGallop < 0){
                        minGallop = 0;
                    }
                    minGallop += 2;
                    state = State.MERGE_HI_OUTER_STEP;
                } else {
                    state = State.MERGE_HI_INNER_2_STEP;
                }
                break;
            case MERGE_HI_INNER_2_STEP:
                gallopKey = mergeTempArray.get(mergeCursor2);
                gallopArray = array;
                gallopBase = mergeBase1;
                gallopLen = mergeLen1;
                gallopHint = mergeLen1 - 1;
                callReturn = State.MERGE_HI_INNER_2_STEP;
                state = State.GALLOP_RIGHT_INIT;
                break;
            case MERGE_HI_GALLOP_RIGHT_FINISH:
                if (mergeLen1 == 0){
                    mergeHiOuterBreak();
                } else {
                    state = State.MERGE_HI_GALLOP_RIGHT_FINISH2;
                }
                break;
            case MERGE_HI_GALLOP_RIGHT_FINISH2:
                array.set(mergeDest--, mergeTempArray.get(mergeCursor2--));
                if (--mergeLen2 == 1){
                    mergeHiOuterBreak();
                } else {
                    gallopKey = array.get(mergeCursor1);
                    gallopArray = mergeTempArray;
                    gallopBase = 0;
                    gallopLen = mergeLen2;
                    gallopHint = mergeLen2 - 1;
                    callReturn = State.MERGE_HI_INNER_2_STEP;
                    state = State.GALLOP_LEFT_INIT;
                }
                break;
            case MERGE_HI_GALLOP_LEFT_FINISH:
                if (mergeLen2 <= 1){
                    mergeHiOuterBreak();
                } else {
                    state = State.MERGE_HI_GALLOP_LEFT_FINISH2;
                }
                break;
            case MERGE_HI_GALLOP_LEFT_FINISH2:
                array.set(mergeDest--, array.get(mergeCursor1--));
                if (--mergeLen1 == 0){
                    mergeHiOuterBreak();
                } else {
                    minGallop--;
                    state = State.MERGE_HI_INNER_2_NEXT;
                }
                break;
            case DONE:
                stepCount--;
                break;
        }
    }

    private void countRunStart(){
        countRunHi = mainLo + 1;
        if (countRunHi == mainHi){
            mainRunLen = 1;
            if (!normal){
                binSortStart();
            } else {
                countRunFinishCheck();
            }
        } else {
            state = State.COUNT_RUN_CHECK;
        }
    }

    private void countRunFinish(){
        mainRunLen = countRunHi - mainLo;
        if (!normal){
            binSortStart();
        } else {
            countRunFinishCheck();
        }
    }

    private void countRunFinishCheck(){
        if (mainRunLen < mainMinRunLen){
            mainForce = mainNumRemaining <= mainMinRunLen ? mainNumRemaining : mainMinRunLen;
            binLo = mainLo;
            binHi = mainLo + mainForce;
            binStart = mainLo + mainRunLen;
            state = State.BINARY_SORT_INIT;
        } else {
            state = State.PUSH_RUN;
        }
    }

    private void binSortStart(){
        binLo = mainLo;
        binHi = mainHi;
        binStart = mainLo + mainRunLen;
        state = State.BINARY_SORT_INIT;
    }

    private void reverseStart(){
        reverseLo = mainLo;
        reverseHi = countRunHi - 1;
        state = State.REVERSE_NEXT;
    }

    private void gallopFinish1(State nextState){
        if (gallopOfs > gallopMaxOfs){
            gallopOfs = gallopMaxOfs;
        }
        int tmp = gallopLastOfs;
        gallopLastOfs = gallopHint - gallopOfs + 1;
        gallopOfs = gallopHint - tmp;
        state = nextState;
    }

    private void gallopFinish2(State nextState){
        if (gallopOfs > gallopMaxOfs){
            gallopOfs = gallopMaxOfs;
        }
        gallopLastOfs += gallopHint + 1;
        gallopOfs += gallopHint;
        state = nextState;
    }

    private void gallopStep(State nextState){
        gallopLastOfs = gallopOfs;
        gallopOfs = (gallopOfs << 1) + 1;
        if (gallopOfs <= 0){
            gallopOfs = gallopMaxOfs;
        }
        state = nextState;
    }

    private void mergeLoCopy1(){
        mergeCopySrc = mergeTempArray;
        mergeCopySrcI = mergeCursor1;
        mergeCopyDest = array;
        mergeCopyDestI = mergeDest;
        mergeCopyLen = mergeLen1;
        callReturn = finalMerge ? State.MERGE_COLLAPSE_FORCE_NEXT : State.MERGE_COLLAPSE_NEXT;
        state = State.MERGE_COPY_NEXT;
    }

    private void mergeLoCopy2(){
        mergeFakeCopy.clear();
        mergeFakeCopy.addAll(array);
        mergeCopySrc = mergeFakeCopy;
        mergeCopySrcI = mergeCursor2;
        mergeCopyDest = array;
        mergeCopyDestI = mergeDest;
        mergeCopyLen = mergeLen2;
        callReturn = finalMerge ? State.MERGE_COLLAPSE_FORCE_NEXT : State.MERGE_COLLAPSE_NEXT;
        state = State.MERGE_COPY_NEXT;
    }

    private void mergeLoOuterBreak(){
        minGallop = minGallop < 1 ? 1 : minGallop;
        if (mergeLen1 == 1){
            mergeLoCopy2();
        } else if (mergeLen1 < 0 || mergeLen1 > 1) {
            mergeLoCopy1();
        }
    }

    private void mergeHiCopy1(){
        mergeCopySrc = mergeTempArray;
        mergeCopySrcI = 0;
        mergeCopyDest = array;
        mergeCopyDestI = mergeDest - (mergeLen2 - 1);
        mergeCopyLen = mergeLen2;
        callReturn = finalMerge ? State.MERGE_COLLAPSE_FORCE_NEXT : State.MERGE_COLLAPSE_NEXT;
        state = State.MERGE_COPY_NEXT;
    }

    private void mergeHiCopy2(){
        mergeDest -= mergeLen1;
        mergeCursor1 -= mergeLen1;
        mergeFakeCopy.clear();
        mergeFakeCopy.addAll(array);
        mergeCopySrc = mergeFakeCopy;
        mergeCopySrcI = mergeCursor1 + 1;
        mergeCopyDest = array;
        mergeCopyDestI = mergeDest + 1;
        mergeCopyLen = mergeLen1;
        callReturn = finalMerge ? State.MERGE_COLLAPSE_FORCE_NEXT : State.MERGE_COLLAPSE_NEXT;
        state = State.MERGE_COPY_NEXT;
    }

    private void mergeHiOuterBreak(){
        minGallop = minGallop < 1 ? 1 : minGallop;
        if (mergeLen2 == 1){
            mergeHiCopy2();
        } else if (mergeLen2 < 0 || mergeLen2 > 1) {
            mergeHiCopy1();
        }
    }

    @Override
    public void stepBack() {

    }

    @Override
    public void reset() {
        state = State.INIT;
        array.clear();
        array.addAll(unsorted);
        normal = true;
    }

    @Override
    public boolean isDone() {
        return state == State.DONE;
    }

    @Override
    public String getName() {
        return "Tim Sort";
    }
}
