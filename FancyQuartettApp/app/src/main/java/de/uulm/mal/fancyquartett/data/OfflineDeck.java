package de.uulm.mal.fancyquartett.data;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mk on 01.01.2016.
 */
public class OfflineDeck extends Deck {

    private ArrayList<Card> cards;

    private ArrayList<Property> properties;

    public OfflineDeck(String name, String description, Card[] cards, Property[] properties) {
        super.name = name;
        super.description = description;
        this.cards = new ArrayList<Card>(Arrays.asList(cards));
        this.properties = new ArrayList<Property>(Arrays.asList(properties));
    }
}
