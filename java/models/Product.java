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

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return name;
    }

    public void setProductName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}