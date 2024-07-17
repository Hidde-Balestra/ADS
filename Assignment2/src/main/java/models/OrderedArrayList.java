package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BinaryOperator;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> ordening;   // the comparator that has been used with the latest sort
    protected int nSorted;                      // the number of items that have been ordered by barcode in the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given ordening comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section

    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> ordening ) {
        super();
        this.ordening = ordening;
        this.nSorted = 0;
    }

    public Comparator<? super E> getOrdening() {
        return this.ordening;
    }

    @Override
    public void clear() {
        super.clear();
        this.nSorted = 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        this.ordening = c;
        this.nSorted = this.size();
    }

    //nSorted moet ook aangepast worden
    @Override
    public void add(int index, E element) {
        setnSorted(index);
        super.add(index, element);
    }

    @Override
    public E remove(int index) {
        setnSorted(index);
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.ordening);
        }
    }

    @Override
    public int indexOf(Object item) {

        if (item != null) {
            return  indexOfByRecursiveBinarySearch((E) item);
        } else {
            return -1;
        }
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            // some arbitrary choice to use the iterative or the recursive version
            return indexOfByRecursiveBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     * @param searchItem    the item to be searched on the basis of comparison by this.ordening
     * @return              the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {
        //https://dlo.mijnhva.nl/d2l/le/content/354181/viewContent/1088474/View (slide 28)

        //leftIndex is the lowest index the searchItem could be found in (this is naturally)
        //rightIndex is the highest index the searchItem could be found (taking into account an index starts at 0
        //the value of rightIndex will be equal to the sorted section of the array minus 1)
        int leftIndex = 0;
        int rightIndex= nSorted - 1;

        //if the leftIndex is equal to or greater than the rightIndex this means the method could not find the
        // searchItem in the sorted section of the array.
        while(leftIndex <= rightIndex){

            //middleIndex is the index approximately in the middle between the rightIndex and leftIndex. This value is
            //later used to find the possible index of the searchItem.
            int middleIndex = (int) ((int) (leftIndex + rightIndex) * 0.5);

            //if this.ordening.compare returns a value greater than 0 this means the possible index of searchItem
            //should be below the item found at the middleIndex. Hence the rightIndex (highest possible index searchItem
            //could be in) is smaller than the middleIndex so the value of rightIndex must be equal to the value of the
            //middleIndex minus 1;
            if(this.ordening.compare(this.get(middleIndex), searchItem) > 0){
                rightIndex = middleIndex - 1;
            }

            //the reverse applies if this.ordening.compare returns a value lesser than 0. This means the possible index
            // of searchItem is bigger than middleIndex. So the value of leftIndex must be the value of middleIndex plus
            // 1.
            else if(this.ordening.compare(this.get(middleIndex), searchItem) < 0){
                leftIndex = middleIndex + 1;
            }

            //if none of the previous conditions are true this means the this.ordening.compare() method must return a 0
            //(this is because we know it is not greater than 0 and also not smaller than 0, since the previous
            // conditions asked this. This must mean it is 0). therefore, we know the searchItem is equal to the item
            //retrieved at the middleIndex. This means the searchItem is the item found at the middleIndex and we can
            //return it.
            else return middleIndex;

        }

        //if the sorted section of the array yields no index (this happens when leftIndex is or is greater than
        // rightIndex) the function will call upon the linearSearch method which tries to locate the index of searchItem
        // beyond the sorted section of the array. This will be a linear search instead of an iterative one.
        return linearSearch(searchItem);
    }

    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     * @param searchItem    the item to be searched on the basis of comparison by this.ordening
     * @return              the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByRecursiveBinarySearch(E searchItem) {
        //https://dlo.mijnhva.nl/d2l/le/content/354181/viewContent/1088474/View (slide 27)

        //This method basically only returns the value given to it by the indexOfBinarySearchHelperMethod. This is
        //because this method needs more parameters to efficiently use recursive binary search. The helper method is
        //however recursive.
        return indexOfRecursiveBinarySearchHelperMethod(searchItem, 0, nSorted - 1);
    }

    /**
     * Helper method for indexOfByRecursiveBinarySearch. This method uses the divide and conquer strategy (also known
     * as binary search) recursively to efficiently find the index of an item in the array it is called upon.
     * @param searchItem the item to be found in the array.
     * @param leftIndex the lowest index the searchItem could possibly be in
     * @param rightIndex the highest index the searchItem could possibly be in
     * @return the index of the searchItem. If searchItem is not between leftIndex and rightIndex the method
     * linearSearch(E searchItem) shall try to find
     */
    private int indexOfRecursiveBinarySearchHelperMethod(E searchItem, int leftIndex, int rightIndex){
        //if the left index is greater than the rightIndex this means there was no item found in the sorted area of the
        //array. If this is the case the method linearSearch will search linearly through the unsorted portion of the
        //array
        if (leftIndex > rightIndex){
            return linearSearch(searchItem);
        }

        //middleIndex is an index in the middle of the right and left index. This value will be used to find the index
        //of the searchItem
        int middleIndex = (int) ((leftIndex + rightIndex) * 0.5);

        //this.ordening.compare() checks if the item on the index of middleIndex is bigger, equal or lesser than 0. If
        //it returns a number bigger than 0 this means the index of searchItem must be lower than the middleIndex. Hence,
        //the rightIndex can be lowered to the value of middleIndex - 1, since the index of searchItem must be higher
        //than the leftIndex and lower than the middleIndex.
        if(this.ordening.compare(this.get(middleIndex), searchItem) > 0){
            return indexOfRecursiveBinarySearchHelperMethod(searchItem, leftIndex, middleIndex - 1);
        }

        //the reverse is also true if this.ordening.compare() returns a value lesser than 0.
        else if(this.ordening.compare(this.get(middleIndex), searchItem) < 0){
            return indexOfRecursiveBinarySearchHelperMethod(searchItem, middleIndex + 1, rightIndex);
        }

        //If none of the previous condition are true this means the middleIndex is the index of the searchItem.
        //This is because this.ordening.compare() would return a 0 if the searchItem and the item on the middleIndex are
        //equal. Since the previous condition checked if this.ordening.compare() would return a value greater than or
        //lesser than 0 this means the value must be 0, and that the middle index is indeed the index of the searchItem.
        else return middleIndex;
    }

    /**
     * Helper method for indexOfByIterativeBinarySearch and indexOfByRecursiveBinarySearch. The method attempts to find
     * the index of searchItem linearly using a for loop in the unsorted section of the given array.
     * @param searchItem the item to be searched on the basis of comparison by this.ordening
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    private int linearSearch(E searchItem){

        //if nSorted is equal to 0 then the array is empty so there is no point to do a linear search
        if(nSorted == 0){
            return -1;
        }

        //does a for loop between the last possible index of the array (the size) and the end of the sorted array
        // (nSorted - 1). For every item in this unsorted section of the array it will look if the found item for the
        // index and the searchItem are equal and if the compare method will say they are also equal (if they are equal
        // according to the compare method the result will be equal to 0)
        for (int i = nSorted - 1; i < this.size(); i++) {
            if(this.ordening.compare(this.get(i), searchItem) == 0){
                return i;
            }
        }

        //if the for loop yields no index of the searchItem this means the searchItem is not in the array, for this
        //reason the method will return -1.
        return -1;
    }

    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     * @param newItem
     * @param merger    a function that takes two items and returns an item that contains the merged content of
     *                  the two items according to some merging rule.
     *                  e.g. a merger could add the value of attribute X of the second item
     *                  to attribute X of the first item and then return the first item
     * @return  whether a new item was added to the list or not
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        if (newItem == null) return false;
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);

        if (matchedItemIndex < 0) {
            this.add(newItem);
            return true;
        } else {

            //if this.ordening.compare() returns a value that is equal to 0 and the equals() method says newItem and the
            //item found at the matchedItemIndex this means these items are the same.
            if(this.ordening.compare(newItem, this.get(matchedItemIndex)) == 0){
                //merger.apply gives the possibility to use a lambda expression (or some other merging rule). In the
                // corresponding test (doubleThePricesByMerge) it gets the prices of both items and adds these together.
                merger.apply(newItem, this.get(matchedItemIndex));
                //when the merger has merged two items this function will return true, since there was a merge.
                return true;
            }

            //if the previous condition does not hold this means the items are not equal and therefore cannot be merged
            //since there is no merge this method will return false.
            return false;
        }
    }

    public void setnSorted(int nSorted) {
        this.nSorted = nSorted;
    }
}
