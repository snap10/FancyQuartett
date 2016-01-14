package de.uulm.mal.fancyquartett.data;

import java.util.ArrayList;

/**
 * Created by Lukas on 08.01.2016.
 */
public class Player {

    private String name;
    private ArrayList<Card> cards;
    private int points = 0;
    private int roundsWon = 0;

    public Player(String name, ArrayList<Card> cards) {
        this.name = name;
        this.cards = cards;
    }

    /*
    NAME
     */

    public String getName() {
        return name;
    }

    /*
    STATISTICS
     */

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points) {
        this.points -= points;
    }

    public int getPoints() {
        return points;
    }

    public void increaseRoundsWon() {
        roundsWon++;
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
        return cards.remove(0);
    }

    public Card getCurrentCard() {
        return cards.get(0);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

}
