package nl.hva.ict.ads;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class Archer {
    public static int MAX_ARROWS = 3;
    public static int MAX_ROUNDS = 10;

    //The id
    private static final AtomicInteger count = new AtomicInteger(135787);
    private final int id;
    private String firstName;
    private String lastName;
    private int[] totalArrows = new int[getMaxArrows() * getMaxRounds()];

    /**
     * Constructs a new instance of Archer and assigns a unique id to the instance.
     * Each new instance should be assigned a number that is 1 higher than the last one assigned.
     * The first instance created should have ID 135788;
     *
     * @param firstName the archers first name.
     * @param lastName the archers surname.
     */
    public Archer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

        id = count.incrementAndGet();
    }

    /**
     * Registers the points for each of the three arrows that have been shot during a round.
     *
     * @param round the round for which to register the points. First round has number 1.
     * @param points the points shot during the round, one for each arrow.
     */
    public void registerScoreForRound(int round, int[] points) {

        for (int i = 0; i < points.length; i++) {
            totalArrows[(round - 1) * getMaxArrows() + i] = points[i];
        }

    }

    /**
     * Calculates/retrieves the total score of all arrows across all rounds
     * @return
     */
    public int getTotalScore() {
        return Arrays.stream(totalArrows).sum();
    }

    /**
     * compares the scores/id of this archer with the scores/id of the other archer according to
     * the scoring scheme: highest total points -> least misses -> earliest registration
     * The archer with the lowest id has registered first
     * @param other     the other archer to compare against
     * @return  negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestTotalScoreWithLeastMissesAndLowestId(Archer other) {
        //Source I used: https://stackoverflow.com/questions/369512/how-to-compare-objects-by-multiple-fields

        return Comparator.comparing(Archer::getTotalScore).reversed()
                .thenComparingInt(Archer::getTotalMisses)
                .thenComparingInt(Archer::getId)
                .compare(this, other);
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getTotalMisses() {
        return totalMisses(totalArrows);
    }

    public static int getMaxArrows() {
        return MAX_ARROWS;
    }

    public static int getMaxRounds() {
        return MAX_ROUNDS;
    }

    @Override
    public String toString() {
        return getId() + " (" + getTotalScore() + ") " + getFirstName() + " " + getLastName();
    }

    /**
     * Helper method I created to help sort the total misses comparison of the
     * compareByHighestTotalScoreWithLeastMissesAndLowestId. This method counts how many arrows the archer missed
     * In the entire competition.
     * @param totalArrows the entire array of arrows shot by the archer
     * @return the total amount of arrows that missed
     */
    private int totalMisses(int[] totalArrows){
        int misses = 0;

        for (int i = 0; i < totalArrows.length; i++) {
            if(totalArrows[i] == 0){
                misses++;
            }
        }

        return misses;
    }
}
