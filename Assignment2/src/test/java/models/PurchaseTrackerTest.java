package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PurchaseTrackerTest {

    PurchaseTracker purchaseTracker;

    @BeforeEach
    private void setup() {
        purchaseTracker = new PurchaseTracker();

        purchaseTracker.importProductsFromVault("/products.txt");

        purchaseTracker.importPurchasesFromVault("/purchases");
    }

    @Test
    public void importVaultCheck() {
        assertEquals(61, purchaseTracker.getProducts().size());
        assertEquals(61, purchaseTracker.getPurchases().size());
        assertEquals(16730, purchaseTracker.getPurchases().stream().mapToInt(Purchase::getCount).sum());
    }

    @Test
    public void totalVolumeCheck() {
        //The total volume of all products should be equal to 16730
        assertEquals(16730, purchaseTracker.calculateTotalVolume());

        //The total volume of a new purchaseTracker instance should be 0
        purchaseTracker = new PurchaseTracker();
        assertEquals(0, purchaseTracker.calculateTotalVolume());
    }

    @Test
    public void revenueCheck() {
        //The total revenue should be equal to 38120.38
        assertEquals(38120.37999999999, purchaseTracker.calculateTotalRevenue());

        //The total revenue of a new purchaseTracker instance should be 0
        purchaseTracker = new PurchaseTracker();
        assertEquals(0, purchaseTracker.calculateTotalRevenue());
    }

}
