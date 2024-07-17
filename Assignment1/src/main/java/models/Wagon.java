package models;

public abstract class Wagon {
    protected int id;               // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
    // a.k.a. the successor of this wagon in a sequence
    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
    // a.k.a. the predecessor of this wagon in a sequence
    // set to null if no predecessor is connected

    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon(int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    /**
     * @return whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return getNextWagon() != null;
    }

    /**
     * @return whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return getPreviousWagon() != null;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     *
     * @return the last wagon
     */
    public Wagon getLastWagonAttached() {
        Wagon current = this;

        while (current.nextWagon != null) {
            current = current.nextWagon;
        }

        return current;
    }

    //helper method for the reverse method
    public Wagon getFirstWagonAttached() {
        Wagon current = this;

        while (current.previousWagon != null) {
            current = current.previousWagon;
        }

        return current;


    }

    /**
     * @return the length of the tail of wagons towards the end of the sequence
     * excluding this wagon itself.
     */
    public int getTailLength() {
        int tailLength = 0;
        Wagon currentWagon = this;

        while (currentWagon.nextWagon != null) {
            currentWagon = currentWagon.nextWagon;
            tailLength++;
        }

        return tailLength;
    }

    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     *
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *                               The exception should include a message that reports the conflicting connection,
     *                               e.g.: "%s is already pulling %s"
     *                               or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) {
        Wagon currentWagon = this;

        if (currentWagon.nextWagon != null) {
            throw new IllegalStateException(currentWagon + " has already been attached to " + currentWagon.nextWagon);
        }

        if (tail.previousWagon != null) {
            throw new IllegalStateException(tail.previousWagon + " is already pulling " + tail);
        }

        tail.previousWagon = getLastWagonAttached();
        getLastWagonAttached().nextWagon = tail;

    }

    //Helper method I created for train method insertAtFront
    public void attachFront(Wagon front) {
        Wagon currentWagon = this;

        if (currentWagon.previousWagon != null) {
            throw new IllegalStateException(currentWagon + " has already been attached to " +
                    currentWagon.previousWagon);
        }

        Wagon lastSequenceWagon = front.getLastWagonAttached();

        this.previousWagon = lastSequenceWagon;
        lastSequenceWagon.nextWagon = this;
    }

    //Helper method for test 19
    public void attachInMiddle(Wagon wagon, Wagon previousWagonInTrain, Wagon nextWagonInTrain) {

        Wagon possibleSequenceLastWagon = wagon.getLastWagonAttached();

        if (wagon == possibleSequenceLastWagon) {
            previousWagonInTrain.nextWagon = wagon;
            wagon.previousWagon = previousWagonInTrain;

            nextWagonInTrain.previousWagon = wagon;
            wagon.nextWagon = nextWagonInTrain;
        } else {
            previousWagonInTrain.nextWagon = wagon;
            wagon.previousWagon = previousWagonInTrain;

            nextWagonInTrain.previousWagon = possibleSequenceLastWagon;
            possibleSequenceLastWagon.nextWagon = nextWagonInTrain;
        }

    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     *
     * @return the first wagon of the tail that has been detached
     * or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        // TODO detach the tail from this wagon (sustaining the invariant propositions).
        //  and return the head wagon of that tail
        Wagon currentWagon = this;
        Wagon headWagonOfTail;

        if (currentWagon.nextWagon == null) {
            return null;
        } else {
            headWagonOfTail = currentWagon.nextWagon;
            currentWagon.nextWagon = null;
            headWagonOfTail.previousWagon = null;
        }

        return headWagonOfTail;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     *
     * @return the former previousWagon that has been detached from,
     * or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        Wagon currentWagon = this;
        Wagon predecessor = this.previousWagon;

        if (predecessor == null) {
            return null;
        } else {
            predecessor.nextWagon = null;
            currentWagon.previousWagon = null;
        }

        return predecessor;
    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     *
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        Wagon currentWagon = this;

        if (front.nextWagon != null) {
            front.nextWagon = null;
        } else if (currentWagon.previousWagon != null) {
            previousWagon.nextWagon = null;
        }

        if (currentWagon != front) {
            front.nextWagon = currentWagon;
            currentWagon.previousWagon = front;
        }
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {
        Wagon currentWagon = this;

        if (hasPreviousWagon() && hasNextWagon()) {
            previousWagon.nextWagon = nextWagon;
            nextWagon.previousWagon = previousWagon;

            currentWagon.nextWagon = null;
            currentWagon.previousWagon = null;
        } else if (!hasNextWagon()) {
            previousWagon.nextWagon = null;
            currentWagon.previousWagon = null;
        } else if (!hasPreviousWagon()) {
            nextWagon.previousWagon = null;
            currentWagon.nextWagon = null;
        }

    }

    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     *
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        Wagon currentWagon = this;
        Wagon lastWagon = this.getLastWagonAttached();
        Wagon wagonToReturn = lastWagon;
        Wagon wagonBeforeCurrentWagon = null;

        //disconnects the previous wagons from this wagon if the reverse sequence is partial
        if (currentWagon.hasPreviousWagon()) {
            wagonBeforeCurrentWagon = currentWagon.previousWagon;
            currentWagon.detachFront();
        }

        final int amountOfWagons = currentWagon.getTailLength();

        //detatches the last wagon and inserts it at the front
        lastWagon.previousWagon.detachTail();
        currentWagon.attachFront(lastWagon);
        currentWagon = lastWagon;
        lastWagon = currentWagon.getLastWagonAttached();

        //places the last wagon in the sequence in the tail position of the last wagon that was placed there
        for (int i = 0; i < amountOfWagons; i++) {
            lastWagon.previousWagon.detachTail();

            if (currentWagon.nextWagon != null) {
                currentWagon.nextWagon.previousWagon = lastWagon;
                lastWagon.nextWagon = currentWagon.nextWagon;
            }

            currentWagon.nextWagon = lastWagon;
            lastWagon.previousWagon = currentWagon;

            currentWagon = lastWagon;
            lastWagon = currentWagon.getLastWagonAttached();

        }

        //re-attaches the previous wagons to the partially reversed sequence
        if (wagonBeforeCurrentWagon != null) {
            Wagon wagonToReAttach = currentWagon.getFirstWagonAttached();
            wagonToReAttach.previousWagon = wagonBeforeCurrentWagon;
            wagonBeforeCurrentWagon.nextWagon = wagonToReAttach;
        }

        return wagonToReturn;
    }

    @Override
    public String toString() {
        return "[Wagon-" + id + "]";
    }
}
