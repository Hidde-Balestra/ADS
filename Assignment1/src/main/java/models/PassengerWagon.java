package models;
// TODO
public class PassengerWagon extends Wagon{

    public int numberOfSeats;

    public PassengerWagon(int wagonId, int numberOfSeats) {
        super(wagonId);
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {
        // TODO
        return numberOfSeats;
    }


}
