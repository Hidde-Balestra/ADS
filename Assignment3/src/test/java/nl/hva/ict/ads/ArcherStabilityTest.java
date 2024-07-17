package nl.hva.ict.ads;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.*;

public class ArcherStabilityTest {

    private static Comparator<Archer> scoringScheme;
    Archer archer1, archer2, archer1Copy;
    int[] scores1, scores2, scores1Copy;

    @BeforeEach
    void setup() {
        archer1 = new Archer("Sjef van den", "Berg");
        scores1 = new int[]{10,8,0};
        archer2 = new Archer("Nico", "Tromp");
        scores2 = new int[]{9,9,0};
        archer1Copy = new Archer("Sjef van den", "Berg");
        scores1Copy = new int[]{10,8,0};

    }

    @BeforeAll
    static void setupClass() {
        scoringScheme = Archer::compareByHighestTotalScoreWithLeastMissesAndLowestId;
    }

    @Test
    void sameArcherReturnsZero(){
        //If you compare an archer with itself it should return 0;
        int sameArcher = scoringScheme.compare(archer1, archer1);
        assertEquals(0, sameArcher);
    }

    @Test
    void differentArchersReturnsNotZero(){
        //If you compare two different archers it should return anything but 0;
        int differentArchers = scoringScheme.compare(archer1, archer2);
        assertNotEquals(0, differentArchers);
    }

    @Test
    void sameValueArchersAreDifferentInstances(){
        //Two archers with identical names and scores should still be different instances
        //So if you compare them, the outcome should not be 0;
        int identicalArchers = scoringScheme.compare(archer1, archer1Copy);
        assertNotEquals(0, identicalArchers);
    }

}


