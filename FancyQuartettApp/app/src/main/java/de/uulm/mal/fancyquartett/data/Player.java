package de.uulm.mal.fancyquartett.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lukas on 08.01.2016.
 */
public class Player implements Serializable {

    private int id = -1;
    private String name;
    private ArrayList<Card> cards;
    private int points = 0;
    private int roundsWon = 0;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.cards = new ArrayList<Card>();
    }

    /*
    PLAYER
     */

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    /*
    STATISTICS
     */

    public void setPoints(int points) {this.points = points;}



    public int getPoints() {
        return points;
    }

    public void setRoundsWon(int roundsWon) {
        this.roundsWon = roundsWon;
    }

    public int getRoundsWon() {
        return roundsWon;
    }

    /*
    CARDS
     */

    public void addNewCard(Card card) {
        cards.add(card);
    }

    public void addNewCards(ArrayList<Card> cards) {
        this.cards.addAll(cards);
    }

    public void queueCard() {
        Card card = cards.remove(0);
        cards.add(card);
    }

    public Card removeCurrentCard() {
        if(cards.size() == 0) return null;
        return cards.remove(0);
    }

    public Card getCurrentCard() {
        if(cards.size() == 0) return null;
        return cards.get(0);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

}
