package de.uulm.mal.fancyquartett.data;


import java.io.Serializable;

/**
 * WrapperClass for OnlineDecks, compatibilityIssues
 */
public class OnlineDeck extends Deck implements Serializable {

    public OnlineDeck(int deckid, String name, String description, Image image, String misc, String misc_version) {
        super(deckid,name,description,image,misc,misc_version);
    }
}
