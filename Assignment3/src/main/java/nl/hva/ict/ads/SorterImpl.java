package nl.hva.ict.ads;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SorterImpl<E> implements Sorter<E> {

    /**
     * Sorts all items by selection or insertion sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    public List<E> selInsSort(List<E> items, Comparator<E> comparator) {
        //For this method I chose to use an insertion sort
        //I modified this code for this method: https://www.geeksforgeeks.org/insertion-sort/

        //This for loop will do an iteration for each item once, so that all items are sure to be sorted correctly.
        for (int i = 1; i < items.size(); i++) {

            //The object occupying the index 'i'
            E nextItem = items.get(i);

            //The current position/value of the index
            int currentPositionIndex = i;

            //While the object in the index before the current index is greater than the object in the current index,
            //and the current index is not 0, swap these two objects and lower the current index by one and repeat.
            //This only stops when the object in the index before the current index is smaller than that object.
            while (currentPositionIndex > 0 && comparator.compare(items.get(currentPositionIndex - 1), nextItem) > 0){
                items.set(currentPositionIndex, items.get(currentPositionIndex - 1));
                currentPositionIndex--;
            }

            //The last step after this sorting is to set the object which has index i at the index of the
            //currentPositionIndex.
            items.set(currentPositionIndex, nextItem);
        }

        //Returns the newly sorted items
        return items;
    }


    /**
     * Sorts all items by quick sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    public List<E> quickSort(List<E> items, Comparator<E> comparator) {
        // sort the complete list of items from position 0 till size-1, encluding position size

        //This method calls upon its helper method "quickSortPart" to do the actual logic.
        this.quickSortPart(items, 0, items.size()-1, comparator);

        //Returns the items after they have been sorted.
        return items;
    }

    /**
     * Sorts all items between index positions 'from' and 'to' inclusive by quick sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array or other positions in items
     *
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    private void quickSortPart(List<E> items, int low, int high, Comparator<E> comparator) {
        //I modified this code for this method: https://www.geeksforgeeks.org/quick-sort/

        //If the index of high is lower than 'low' every object has been iterated through so the items are sorted.
        if(high < low){
            return;
        }

        //The pivot is an object that will be sorted in the correct position, every other object sorted in the array will
        //be sorted relative from the pivot.
        int pivot = partition(items, low, high, comparator);

        //quickSort the left part of the array that is before the pivot
        quickSortPart(items, low, pivot - 1, comparator);

        //quickSort the right part of the array that is behind the pivot
        quickSortPart(items, pivot + 1, high, comparator);

    }

    /**
     * This helper method for quickSortPart. It uses the last index of the sorted part as the pivot and puts every object
     * that is smaller than the pivot before it and every object that is bigger after it.
     * @param items
     * @param low
     * @param high
     * @param comparator
     * @return
     */
    private int partition(List<E> items, int low, int high, Comparator<E> comparator){
        //I modified this code for this method: https://www.geeksforgeeks.org/quick-sort/

        //There are many ways to pick a pivot point. I chose for the highest index.
        int pivot = high;

        int index = (low - 1);
        System.out.println(index);
        for(int i = low; i <= high - 1; i++)
        {

            if(comparator.compare(items.get(i), items.get(pivot)) < 0)
            {
                index++;
                Collections.swap(items, index, i);
            }
        }

        Collections.swap(items, index + 1, high);

        return (index + 1);
    }

    /**
     * Identifies the lead collection of numTops items according to the ordening criteria of comparator
     * and organizes and sorts this lead collection into the first numTops positions of the list
     * with use of (zero-based) heapSwim and heapSink operations.
     * The remaining items are kept in the tail of the list, in arbitrary order.
     * Items are sorted 'in place' without use of an auxiliary list or array or other positions in items
     * @param numTops       the size of the lead collection of items to be found and sorted
     * @param items
     * @param comparator
     * @return              the items list with its first numTops items sorted according to comparator
     *                      all other items >= any item in the lead collection
     */
    public List<E> topsHeapSort(int numTops, List<E> items, Comparator<E> comparator) {
        // check 0 < numTops <= items.size()

        if (numTops <= 0) return items;
        else if (numTops > items.size()) return quickSort(items, comparator);

        // the lead collection of numTops items will be organised into a (zero-based) heap structure
        // in the first numTops list positions using the reverseComparator for the heap condition.
        // that way the root of the heap will contain the worst item of the lead collection
        // which can be compared easily against other candidates from the remainder of the list
        Comparator<E> reverseComparator = comparator.reversed();

        // initialise the lead collection with the first numTops items in the list
        for (int heapSize = 2; heapSize <= numTops; heapSize++) {
            // repair the heap condition of items[0..heapSize-2] to include new item items[heapSize-1]
            heapSwim(items, heapSize, reverseComparator);
        }

        // insert remaining items into the lead collection as appropriate
        for (int i = numTops; i < items.size(); i++) {
            // loop-invariant: items[0..numTops-1] represents the current lead collection in a heap data structure
            //  the root of the heap is the currently trailing item in the lead collection,
            //  which will lose its membership if a better item is found from position i onwards
            E item = items.get(i);
            E worstLeadItem = items.get(0);
            if (comparator.compare(item, worstLeadItem) < 0) {
                // item < worstLeadItem, so shall be included in the lead collection
                items.set(0, item);
                // demote worstLeadItem back to the tail collection, at the orginal position of item
                items.set(i, worstLeadItem);
                // repair the heap condition of the lead collection
                heapSink(items, numTops, reverseComparator);
            }
        }

        // the first numTops positions of the list now contain the lead collection
        // the reverseComparator heap condition applies to this lead collection
        // now use heapSort to realise full ordening of this collection
        for (int i = numTops-1; i > 0; i--) {
            // loop-invariant: items[i+1..numTops-1] contains the tail part of the sorted lead collection
            // position 0 holds the root item of a heap of size i+1 organised by reverseComparator
            // this root item is the worst item of the remaining front part of the lead collection
            Collections.swap(items, 0, i);
            heapSink(items, i, reverseComparator);
        }

        // alternatively we can realise full ordening with a partial quicksort:
        // quickSortPart(items, 0, numTops-1, comparator);

        return items;
    }

    /**
     * Repairs the zero-based heap condition for items[heapSize-1] on the basis of the comparator
     * all items[0..heapSize-2] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
    private void heapSwim(List<E> items, int heapSize, Comparator<E> comparator) {
        //I used the following video as a guide: https://www.youtube.com/watch?v=t0Cq6tVNRBA&list=LL&index=4

        //The index of an object in swim is always the last object in the heap, since this is a 0 based heap the index
        //for this object is heapsize - 1;
        int index = heapSize - 1;

        //An object will continue swimming up the heap if its index is not the first index and the parent object is
        //greater than the child object
        while (index > 0 && comparator.compare(items.get(getParentIndex(index)) , items.get(index)) > 0){

            //Since the object occupying the parentIndex is greater than the object occupying the child index these
            //items must swap position to maintain the heap order.
            Collections.swap(items, index, getParentIndex(index));

            //Since the child object now has a new parent object the child object could still swim up. For this reason
            //the index of the childObject will be the index of its previous parent object. After this the while loop
            //Will keep repeating until its conditions can't be met anymore.
            index = getParentIndex(index);
        }

    }

    /**
     * Repairs the zero-based heap condition for its root items[0] on the basis of the comparator
     * all items[1..heapSize-1] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
    private void heapSink(List<E> items, int heapSize, Comparator<E> comparator) {
        int index = 0;

        //The object can only sink down if it has children. It might not have a right child but if it has a child that
        //child will firstly always be in the left position. For this reason it is checked that the index of the left
        //child is smaller then the size of the heap. An object cant sink further then the size of the heap, after all.
        while (getLeftChildIndex(index) < heapSize){

            //smallChildIndex refers to the smallest of the children of the parent index.
            //In this case we take the left child.
            int smallChildIndex = getLeftChildIndex(index);

            //Since the right child could be smaller we compare the left and the right child. We also check if the index
            //Of the right child is smaller than the heapsize. If both of these conditions are the case the smallest
            //child of the parent is the right child and this child shall become the smallest child of the parent.
            if(getRightChildIndex(index) < heapSize &&
                    comparator.compare(items.get(getRightChildIndex(index)), items.get(getLeftChildIndex(index))) < 0){
                smallChildIndex = getRightChildIndex(index);
            }

            //If the parent object is smaller than the object in the smallChildIndex then everything is in place and no
            //Further actions are required
            if(comparator.compare(items.get(index), items.get(smallChildIndex)) < 0){
                break;
            }

            //If this is not the case the parent object and the child object will switch places and the index on which
            //the while loop functions will now be the index of the smallChildIndex. This is to see if the child needs
            //To sink further down below the heap.
            Collections.swap(items, index, smallChildIndex);
            index = smallChildIndex;
        }

    }

    /**
     * This method gets the parent index using its child index
     * @param childIndex
     * @return the index of the parent object
     */
    private int getParentIndex(int childIndex){
        return (childIndex - 1) / 2;
    }

    /**
     * This method gets the left childs index using its parent index
     * @param parentIndex
     * @return the index of the left child object
     */
    private int getLeftChildIndex(int parentIndex){
        return (2 * parentIndex) + 1;
    }

    /**
     * This method gets the right childs index using its parent index
     * @param parentIndex
     * @return the index of the right child object
     */
    private int getRightChildIndex(int parentIndex){
        return (2 * parentIndex) + 2;
    }

}
