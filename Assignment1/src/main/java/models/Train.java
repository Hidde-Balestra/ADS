package models;

public class Train {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;

//        if (firstWagon == null || firstWagon.getPreviousWagon() == null || engine != null){
//
//        }
    }

    /* three helper methods that are usefull in other methods */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    public boolean isPassengerTrain() {
        Wagon wagon = firstWagon;

        return wagon instanceof PassengerWagon;
    }

    public boolean isFreightTrain() {
        Wagon wagon = firstWagon;

        return wagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     * @param wagon the first wagon of a sequence of wagons to be attached
     *              (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        this.firstWagon = wagon;
    }

    /**
     * @return  the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        Wagon wagon = firstWagon;
        int totalWagons = 0;

        if(wagon == null){
            return totalWagons;
        }
        else {
            while (wagon != null){
                totalWagons++;
                wagon = wagon.getNextWagon();
            }
        }

        return totalWagons;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        Wagon wagon = firstWagon;
        while (wagon != null){

            if(wagon.getNextWagon() == null){
                return wagon;
            }

            wagon = wagon.getNextWagon();

        }
        return null;
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        int totalSeats = 0;

        if(isFreightTrain()){
            return totalSeats;
        } else {
            PassengerWagon passengerWagon = (PassengerWagon) this.firstWagon;

            while(passengerWagon != null){
                totalSeats += passengerWagon.getNumberOfSeats();
                passengerWagon = (PassengerWagon) passengerWagon.getNextWagon();
            }

        }

        return totalSeats;
    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {
        int totalSeats = 0;

        if(isPassengerTrain()){
            return totalSeats;
        } else {
            FreightWagon freightWagon = (FreightWagon) this.firstWagon;

            while(freightWagon != null){
                totalSeats += freightWagon.getMaxWeight();
                freightWagon = (FreightWagon) freightWagon.getNextWagon();
            }
        }

        return totalSeats;
    }

    /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        Wagon wagon = firstWagon;

        if(wagon == null){
            return null;
        }

        int lenght = wagon.getTailLength() + 1;

        if(position < 1 || position > lenght){
            return null;
        }

        for (int i = 0; i < lenght; i++) {

            if(position - 1  == i){
                return wagon;
            }

            wagon = wagon.getNextWagon();
        }

        return null;
    }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon wagon = firstWagon;

        if(wagon == null){
            return null;
        }

        while (wagonId != wagon.id){
            if(wagon.getNextWagon() == null){
                return null;
            }
            wagon = wagon.getNextWagon();
        }

        return wagon;
    }

    /**
     * Determines if the given sequence of wagons can be attached to this train
     * Verifies if the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons
     * Verifies that the wagon is not part of the train already
     * Ignores the predecessors before the head wagon, if any
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {
        if(this.firstWagon == null){
            return true;
        }
        if(this.isPassengerTrain() && wagon instanceof PassengerWagon && this.engine.getMaxWagons() >
                (this.getNumberOfWagons() + wagon.getTailLength()) && findWagonById(wagon.id) == null){
            return true;
        } else return this.isFreightTrain() && wagon instanceof FreightWagon && this.engine.getMaxWagons() >
                this.getNumberOfWagons() + wagon.getTailLength() && findWagonById(wagon.id) == null;

    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if attachment is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {

        if(!canAttach(wagon)){
            return false;
        } else if(getLastWagonAttached() == null){

            if(wagon.hasPreviousWagon()){
                wagon.getPreviousWagon().detachTail();
            }

            this.firstWagon = wagon;

        } else {

            if(wagon.hasPreviousWagon()){
                wagon.getPreviousWagon().detachTail();
            }

            this.getLastWagonAttached().attachTail(wagon);
        }

        return true;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
        if(!canAttach(wagon)){
            return false;
        } else if(this.firstWagon == null){

            if(wagon.hasPreviousWagon()){
                wagon.getPreviousWagon().detachTail();
            }

            this.firstWagon = wagon;

        } else {

            if(wagon.hasPreviousWagon()){
                wagon.getPreviousWagon().detachTail();
            }

            firstWagon.attachFront(wagon);
            this.firstWagon = wagon;

        }

        return true;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given position in the train.
     * (The current wagon at given position including all its successors shall then be reattached
     *    after the last wagon of the given sequence.)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity
     *    or the given position is not valid for insertion into this train)
     * if insertion is possible, the head wagon of the sequence is first detached from its predecessors, if any
     * @param position the position where the head wagon and its successors shall be inserted
     *                 1 <= position <= numWagons + 1
     *                 (i.e. insertion immediately after the last wagon is also possible)
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        final int positionBehindLastWagon = 2;

        //checks if the wagon hasn't already been attached, the position isnt bigger then the last position in the train
        //(and also not null) and if it is possible to attach according to the canAttach method
        if(this.findWagonById(wagon.id) != null || !canAttach(wagon) || position < 1 ||
                this.firstWagon != null && position > this.firstWagon.getTailLength() + positionBehindLastWagon){
            return false;
        }

        //detaches previous wagons of the currently selected wagon
        if(wagon.getPreviousWagon() != null){
            wagon.detachFront();
        }

        //insert into empty train
        if(this.firstWagon == null){
            this.firstWagon = wagon;
            return true;
        }

        //insert into first position
        if(position == 1){
            this.insertAtFront(wagon);
            return true;
        }

        //insert into last possible position
        if(position == this.firstWagon.getTailLength() + positionBehindLastWagon){
            this.firstWagon.getLastWagonAttached().attachTail(wagon);
            return true;
        }

        //insert wagon (or possible sequence) in the middle
        if(position > 1 && position < this.firstWagon.getTailLength() + positionBehindLastWagon){
            Wagon wagonAtPostion = findWagonAtPosition(position);
            Wagon wagonAtPreviousPosition = findWagonAtPosition(position - 1);
            this.firstWagon.attachInMiddle(wagon, wagonAtPreviousPosition, wagonAtPostion);
            return true;
        }

        return false;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param wagonId   the id of the wagon to be removed
     * @param toTrain   the train to which the wagon shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        Wagon wagon = findWagonById(wagonId);
        final int oneWagon = 1;

        //tries to find a restriction in which case the movement of the wagon cannot be made
        if(wagon == null || this.isFreightTrain() && toTrain.isPassengerTrain() ||
                this.isPassengerTrain() && toTrain.isFreightTrain() || toTrain.engine.getMaxWagons() <
                toTrain.getNumberOfWagons() + oneWagon){
            return false;
        }

        //wagon is somewhere in the middle
        if(wagon.hasPreviousWagon() && wagon.hasNextWagon()){
            wagon.removeFromSequence();

            if(toTrain.getLastWagonAttached() == null) {
                toTrain.setFirstWagon(wagon);
            } else {
                toTrain.getLastWagonAttached().attachTail(wagon);
            }

            return true;
        }

        //wagon is the first wagon
        if(this.firstWagon == wagon){
            this.setFirstWagon(wagon.getNextWagon());
            wagon.removeFromSequence();

            if(toTrain.getLastWagonAttached() == null) {
                toTrain.setFirstWagon(wagon);
            } else {
                toTrain.getLastWagonAttached().attachTail(wagon);
            }

            return true;
        }

        //wagon is the last wagon
        if(this.getLastWagonAttached() == wagon){
            wagon.removeFromSequence();

            if(toTrain.getLastWagonAttached() == null) {
                toTrain.setFirstWagon(wagon);
            } else {
                toTrain.getLastWagonAttached().attachTail(wagon);
            }

            return true;
        }

        return false;
    }

    /**
     * Tries to split this train before the wagon at given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param position  1 <= position <= numWagons
     * @param toTrain   the train to which the split sequence shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        Wagon wagon = this.findWagonAtPosition(position);
        final int extraWagon = 1;

        if(wagon == null || toTrain.engine.getMaxWagons() < toTrain.getNumberOfWagons() + wagon.getTailLength()
                + extraWagon || this.isFreightTrain() && toTrain.isPassengerTrain() || this.isPassengerTrain() &&
                toTrain.isFreightTrain()){
            return false;
        }

        if(this.firstWagon != wagon){
            wagon.detachFront();
        } else {
            this.firstWagon = null;
        }

        if(toTrain.firstWagon == null){
            toTrain.firstWagon = wagon;
        } else {
            toTrain.getLastWagonAttached().attachTail(wagon);
        }

        return true;

    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        final int accountForPosition = 1;

        if(this.firstWagon != null && this.firstWagon.hasNextWagon()) {

            final int wagonLength = this.firstWagon.getTailLength();
            Wagon lastWagon = firstWagon.getLastWagonAttached();

            for (int i = 0; i < wagonLength; i++) {
                lastWagon.getPreviousWagon().detachTail();
                insertAtPosition(i + accountForPosition, lastWagon);
                lastWagon = lastWagon.getLastWagonAttached();
            }

        }
    }

    @Override
    public String toString() {
        if (firstWagon == null){
            return "Train has no wagons!";
        }

        Wagon currentWagon = firstWagon;
        StringBuilder allWagons = new StringBuilder(currentWagon.toString());

        while (currentWagon.hasNextWagon()){
            currentWagon = currentWagon.getNextWagon();
            allWagons.append(currentWagon.toString());
        }

        return engine.toString() + " " + allWagons + " with " + getNumberOfWagons() +
                " wagons from " + this.origin + " to " + this.destination;
    }
}


