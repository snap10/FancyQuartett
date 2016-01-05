package de.uulm.mal.fancyquartett.data;

/**
 * Created by mk on 01.01.2016.
 */
public class Deck {


    protected String name = null;

    protected String description = null;

    public Deck(){};
    public Deck(String name, String description) {
        this.name = name;
        this.description = description;
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
}
