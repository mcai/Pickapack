package net.pickapack.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CombinationHelper<T> {
    private class Pair {
        private int currentValue = 1;
        private int maxValue = 1;

        public Pair(int currentValue, int maxValue) {
            this.currentValue = currentValue;
            this.maxValue = maxValue;
        }
    }

    private List<List<T>> uncombinedList;
    private List<Pair> currentCombination = new LinkedList<Pair>();
    private boolean hasMoreCombinations = true;

    public CombinationHelper(List<List<T>> uncombinedList) {
        this.uncombinedList = uncombinedList;
        for (List<T> currentSubList : this.uncombinedList) {
            Pair p = new Pair(1, currentSubList.size());
            currentCombination.add(p);
        }
    }

    private List<T> getNextCombination() {
        List<T> result = new LinkedList<T>();
        Iterator<Pair> currentCombinationIterator = currentCombination.iterator();
        Iterator<List<T>> uncombinedListIterator = uncombinedList.iterator();

        while (currentCombinationIterator.hasNext() && uncombinedListIterator.hasNext()) {
            Pair currentPair = currentCombinationIterator.next();
            List<T> currentSubList = uncombinedListIterator.next();
            result.add(currentSubList.get(currentPair.currentValue - 1));
        }

        int overflow = 1;
        for (Pair currentPair : currentCombination) {
            if ((currentPair.currentValue + overflow) > currentPair.maxValue) {
                overflow = (currentPair.currentValue + overflow) - currentPair.maxValue;
                currentPair.currentValue = 1;
            } else {
                currentPair.currentValue = currentPair.currentValue + 1;
                overflow = 0;
                break;
            }
        }

        if (overflow != 0) {
            this.hasMoreCombinations = false;
        }
        return result;
    }

    public static <T> List<List<T>> getCombinations(List<List<T>> lists) {
        List<List<T>> combinations = new ArrayList<List<T>>();

        CombinationHelper<T> lcg = new CombinationHelper<T>(lists);
        while (lcg.hasMoreCombinations) {
            List<T> currentTuple = lcg.getNextCombination();
            combinations.add(currentTuple);
        }

        return combinations;
    }
}