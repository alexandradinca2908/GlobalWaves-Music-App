package main.ArtistClasses;

public final class Merch {
    private String name;
    private String description;
    private int price;

    public Merch() {
    }

    public String getName() {
        return name;
    }

    public void setName(final  String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final  String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }
}
