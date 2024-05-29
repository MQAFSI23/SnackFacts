package models;

public class Product {
    protected long id;
    protected String name;
    protected String category;
    protected byte[] image;

    public Product(long id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public Product(long id, String name, String category, byte[] image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.image = image;
    }

    public long getProductId() {
        return id;
    }

    public String getProductName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public byte[] getImage() {
        return image;
    }
}
