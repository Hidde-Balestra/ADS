package nl.hva.ict.ads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class quickSortEfficiencyTest {

    public static void main(String[] args) {

        ChampionSelector championSelector = new ChampionSelector(1_000_009);
        Sorter<Archer> sorter = new ArcherSorter();
        List<Archer> archers;

        System.out.println("\n" + "quickSort efficiency" + "\n" + "____________________________" );

        for (int i = 50; i < 1_000_000_0; i = i * 2) {

            archers = new ArrayList<>(championSelector.enrollArchers(i));

            System.gc();
            long started = System.nanoTime();

            sorter.quickSort(archers, Comparator.comparing(Archer::getLastName));

            double duration = 1E-6*(System.nanoTime() - started);

            System.out.println("sorted " + i + " in: " + duration + " milliseconds");

        }

    }

}
