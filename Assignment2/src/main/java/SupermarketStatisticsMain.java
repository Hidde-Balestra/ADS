import models.Purchase;
import models.PurchaseTracker;

import java.util.Comparator;

public class SupermarketStatisticsMain {

    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Supermarket Statistics processor\n");

        PurchaseTracker purchaseTracker = new PurchaseTracker();

        purchaseTracker.importProductsFromVault("/products.txt");

        purchaseTracker.importPurchasesFromVault("/purchases");

        purchaseTracker.showTops(5, "worst sales volume",
                Comparator.comparing(Purchase::getCount)
        );
        purchaseTracker.showTops(5, "best sales revenue",
                Comparator.comparing(Purchase::getRevenue).reversed()
        );

        System.out.printf("Total volume of all purchases: %.0f\n", purchaseTracker.calculateTotalVolume());
        System.out.printf("Total revenue from all purchases: %.2f\n", purchaseTracker.calculateTotalRevenue());
    }


}
