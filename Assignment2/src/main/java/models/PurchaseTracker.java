package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;

public class PurchaseTracker {
    private final String PURCHASE_FILE_PATTERN = ".*\\.txt";

    private OrderedList<Product> products;        // the reference list of all Products available from the SuperMarket chain
    private OrderedList<Purchase> purchases;      // the aggregated volumes of all purchases of all products across all branches

    public PurchaseTracker() {
        purchases = new OrderedArrayList<>(Comparator.comparing(Purchase::getBarcode));
        products = new OrderedArrayList<>(Comparator.comparing(Product::getBarcode));
    }

    /**
     * imports all products from a resource file that is common to all branches of the Supermarket chain
     * @param resourceName
     */
    public void importProductsFromVault(String resourceName) {
        this.products.clear();

        // load all products from the text file
        importItemsFromFile(this.products,
                PurchaseTracker.class.getResource(resourceName).getPath(),
                Product::fromLine);

        // sort the products for efficient later retrieval
        this.products.sort();

        System.out.printf("Imported %d products from %s.\n", products.size(), resourceName);
    }

    /**
     * imports and merges all raw purchase data of all branches from the hierarchical file structure of the vault
     * @param resourceName
     */
    public void importPurchasesFromVault(String resourceName) {
        this.purchases.clear();

        mergePurchasesFromVaultRecursively(
                PurchaseTracker.class.getResource(resourceName).getPath());

        System.out.printf("Accumulated purchases of %d products from files in %s.\n", this.purchases.size(), resourceName);
    }

    /**
     * traverses the purchases vault recursively and processes every data file that it finds
     * @param filePath
     */
    private void mergePurchasesFromVaultRecursively(String filePath) {

        File file = new File(filePath);

        if (file.isDirectory()) {
            // the file is a folder (a.k.a. directory)
            //  retrieve a list of all files and sub folders in this directory
            File[] filesInDirectory = Objects.requireNonNullElse(file.listFiles(), new File[0]);

            for (File foundFile: filesInDirectory){
                mergePurchasesFromVaultRecursively(String.valueOf(foundFile));
            }


        } else if (file.getName().matches(PURCHASE_FILE_PATTERN)) {
            // the file is a regular file that matches the target pattern for raw purchase files
            // merge the content of this file into this.purchases
            this.mergePurchasesFromFile(file.getAbsolutePath());
        }
    }

    /**
     * imports another batch of raw purchase data from the filePath text file
     * and merges the purchase amounts with the earlier imported and accumulated collection in this.purchases
     * @param filePath
     */
    private void mergePurchasesFromFile(String filePath) {
        int originalNumPurchases = purchases.size();

        // create a temporary ordered list for the additional purchases, ordered by same comparator as the main list
        OrderedList<Purchase> newPurchases = new OrderedArrayList<>(this.purchases.getOrdening());
        // re-sort the accumulated purchases for efficient searching
        this.purchases.sort();

        importItemsFromFile(newPurchases, filePath, (String textLine) -> Purchase.fromLine(textLine, products)
        );

        for (Purchase purchase : newPurchases) {
            this.purchases.merge(purchase, (p1,p2) -> {
                purchases.get(purchases.indexOf(p1)).setCount(p1.getCount() + p2.getCount());
                return p1;
            });
        }

        int addedCount = purchases.size() - originalNumPurchases;

        System.out.printf("Merged %d, added %d new purchases from %s.\n", newPurchases.size() - addedCount, addedCount, filePath);
    }

    /**
     * show the top n purchases according to the ranking criterium specified by ranker
     * @param n             the number of top purchases to be shown
     * @param subTitle      some title text that clarifies the list
     * @param ranker        the comparator used to rank the purchases
     */
    public void showTops(int n, String subTitle, Comparator<Purchase> ranker) {
        System.out.printf("%d purchases with %s:\n", n, subTitle);
        // helper list to rank the purchases without disturbing the ordening of the original list
        OrderedList<Purchase> tops = new OrderedArrayList<>(ranker);

        // add all purchases to the new tops list, and sort the list
        tops.addAll(this.purchases);
        tops.sort();

        // show the top items
        for (int rank = 0; rank < n && rank < tops.size(); rank++) {
            System.out.printf("%d: %s\n", rank+1, tops.get(rank));
        }
    }

    /**
     * calculates the total volume of all purchases
     * @return  the total amount of product items purchased across all purchases
     */
    public double calculateTotalVolume() {
        return purchases.aggregate(Purchase::getCount);
    }

    /**
     * calculates the total revenue of all purchases
     * @return  the total amount of money paid across all purchases
     */
    public double calculateTotalRevenue() {
        double totalRevenue = 0;

        for (Purchase purchase : purchases) {
            totalRevenue += purchase.getRevenue();
        }

        return totalRevenue;
    }

    /**
     * imports a collection of items from a text file which provides one line for each item
     * @param items         the list to which imported items shall be added
     * @param filePath      the file path of the source text file
     * @param converter     a function that can convert a text line into a new item instance
     * @param <E>           the (generic) type of each item
     */
    public static <E> void importItemsFromFile(List<E> items, String filePath, Function<String,E> converter) {
        int originalNumItems = items.size();

        Scanner scanner = createFileScanner(filePath);

        while (scanner.hasNext()) {
            // input another line with author information
            String line = scanner.nextLine();
            E item = converter.apply(line);
            items.add(item);
        }

        System.out.printf("Imported %d items from %s.\n", items.size() - originalNumItems, filePath);
    }

    /**
     * helper method to create a scanner on a file an handle the exception
     * @param filePath
     * @return
     */
    private static Scanner createFileScanner(String filePath) {
        try {
            return new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound exception on path: " + filePath);
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(OrderedList<Purchase> purchases) {
        this.purchases = purchases;
    }
}
