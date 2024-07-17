package models;

import java.util.Objects;

public class Product {
    private final long barcode;
    private String title;
    private double price;

    public Product(long barcode) {
        this.barcode = barcode;
    }
    public Product(long barcode, String title, double price) {
        this(barcode);
        this.title = title;
        this.price = price;
    }

    /**
     * parses product information from a textLine with format: barcode, title, price
     * @param textLine
     * @return  a new Product instance with the provided information
     *          or null if the textLine is corrupt or incomplete
     */
    public static Product fromLine(String textLine) {
        String[] splitTextLine = textLine.split(",");

        if(splitTextLine.length < 3 || Objects.equals(splitTextLine[0].trim(), "") ||
                Objects.equals(splitTextLine[1].trim(), "") || Objects.equals(splitTextLine[2].trim(), "")){
            return null;
        }

        return new Product(Long.parseLong(splitTextLine[0].trim()), splitTextLine[1].trim(),
                Double.parseDouble(splitTextLine[2].trim()));
    }

    public long getBarcode() {
        return barcode;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Product)) return false;
        return this.getBarcode() == ((Product)other).getBarcode();
    }

    @Override
    public String toString() {
        return barcode + "/" + title + "/" + price;
    }
}
