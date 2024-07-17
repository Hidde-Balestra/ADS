package models;

import java.util.List;
import java.util.Objects;

public class Purchase {
    private final Product product;
    private int count;

    public Purchase(Product product, int count) {
        this.product = product;
        this.count = count;
    }

    /**
     * parses purchase summary information from a textLine with format: barcode, amount
     * @param textLine
     * @param products  a list of products ordered and searchable by barcode
     *                  (i.e. the comparator of the ordered list shall consider only the barcode when comparing products)
     * @return  a new Purchase instance with the provided information
     *          or null if the textLine is corrupt or incomplete
     */
    public static Purchase fromLine(String textLine, List<Product> products) {

        String[] splitTextLine = textLine.split(",");
        Product productFromTextLine = new Product(Long.parseLong(splitTextLine[0]), "", 0);
        Product foundProduct = products.get(products.indexOf(productFromTextLine));

        //error detection
        if(splitTextLine.length < 2 || foundProduct == null || Objects.equals(splitTextLine[0].trim(), "") ||
                Objects.equals(splitTextLine[1].trim(), "")){
            return null;
        }

        return new Purchase(foundProduct, Integer.parseInt(splitTextLine[1].trim()));
    }

    /**
     * add a delta amount to the count of the purchase summary instance
     * @param delta
     */
    public void addCount(int delta) {
        this.count += delta;
    }

    @Override
    public String toString() {
        String totalPrice = String.format("%.2f", product.getPrice() * getCount());
        totalPrice = totalPrice.replace(",",".");

        return product.getBarcode() + "/" + product.getTitle() + "/" + count + "/" + totalPrice;
    }

    public long getBarcode() {
        return this.product.getBarcode();
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public Product getProduct() {
        return product;
    }

    public double getRevenue(){
        return getCount() * product.getPrice();
    }


}
