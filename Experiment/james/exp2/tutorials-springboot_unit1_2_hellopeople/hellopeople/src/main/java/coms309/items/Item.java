package coms309.items;

/**
 * @author James Joseph
 */
public class Item {
    private String owner;
    private String name;
    private float price;

    public Item() {

    }

    public Item(String name, float price){
        this.owner = "Available";
        this.name = name;
        this.price = price;
    }

    public Item(String owner, String name, float price){
        this.owner = owner;
        this.name = name;
        this.price = price;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
