package info.androidhive.sample.model;

public class Offer {
    String name;
    String description;
    String imageUlr;

    public Offer(String name, String description, String imageUlr) {
        this.name = name;
        this.description = description;
        this.imageUlr = imageUlr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUlr() {
        return imageUlr;
    }

    public void setImageUlr(String imageUlr) {
        this.imageUlr = imageUlr;
    }
}
