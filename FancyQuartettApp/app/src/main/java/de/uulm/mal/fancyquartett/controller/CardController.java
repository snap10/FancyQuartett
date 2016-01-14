package de.uulm.mal.fancyquartett.controller;

import java.util.ArrayList;
import java.util.Collections;

import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.data.Property;

/**
 * Created by Lukas on 14.01.2016.
 */
public class CardController {

    private GameActivity.GameEngine engine;

    /**
     *
     * @param engine
     */
    public CardController(GameActivity.GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Shuffles a given parameter ArrayList<Card> and returns shuffled ArrayList<Card>.
     * @param cards
     * @return
     */
    public ArrayList<Card> shuffleCards(ArrayList<Card> cards) {
        Collections.shuffle(cards);
        return cards;
    }

    /**
     * Spreads shuffled cards to player1 and player2.
     * @param cards
     */
    public void spreadCards(ArrayList<Card> cards, Player p1, Player p2) {
        ArrayList<Card> p1Cards = p1.getCards();
        ArrayList<Card> p2Cards = p2.getCards();
        for(int i=0; i<cards.size(); i++) {
            Card card = cards.get(i);
            if((i%2) != 0) {
                p1Cards.add(card);
            } else {
                p2Cards.add(card);
            }
        }
    }

    /**
     * Queues first (current) card of player1 or player2 at the end of his deck.
     * @param playerId
     */
    public void queueCard(int playerId) {
        Player player = engine.getPlayer(playerId);
        player.queueCard();
    }

    /**
     * Removes current card from given players card-deck.
     * @param playerId
     * @return
     */
    public Card removeCardFromPlayer(int playerId) {
        Player player = engine.getPlayer(playerId);
        return player.removeCurrentCard();
    }

    /**
     * Adds a given card to given players card-deck.
     * @param card
     * @param playerId
     */
    public void addCardToPlayer(Card card, int playerId) {
        Player player = engine.getPlayer(playerId);
        player.addNewCard(card);
    }

    /**
     * Adds a the current cards of player1 and player2 into stingStack.
     * @param p1Card
     * @param p2Card
     */
    public void addCardsToStingStag(Card p1Card, Card p2Card) {
        ArrayList<Card> stingStack = engine.getStingStack();
        stingStack.add(p1Card);
        stingStack.add(p2Card);
    }

    /**
     * Removes all cards from stingStack and adds them to given players card-deck.
     * @param playerId
     */
    public void removeCardsFromStingStag(int playerId) {
        Player player = engine.getPlayer(playerId);
        // add all cards from stingStack to winner
        player.addNewCards(engine.getStingStack());
        // clear stingStack
        engine.setStingStack(new ArrayList<Card>());
    }

    /**
     * Removes card from loser and adds it to winners card-deck. If stingStack contains some
     * cards, then they will be added to to winners card-deck.
     * @param winnerId
     */
    public void handlePlayerCards(int winnerId) {
        if(winnerId != engine.STANDOFF) {
            if(winnerId == engine.PLAYER1) {
                Card card = removeCardFromPlayer(engine.PLAYER2);
                queueCard(winnerId);
                addCardToPlayer(card, winnerId);
            }
            if(winnerId == engine.PLAYER2) {
                Card card = removeCardFromPlayer(engine.PLAYER1);
                queueCard(winnerId);
                addCardToPlayer(card, winnerId);
            }
            if(engine.getStingStack().size() > 0) {
                removeCardsFromStingStag(winnerId);
            }
        } else {
            Card p1Card = removeCardFromPlayer(engine.PLAYER1);
            Card p2Card = removeCardFromPlayer(engine.PLAYER2);
            addCardsToStingStag(p1Card, p2Card);
        }
    }

    /**
     * Compares a property of two given cards and returns the player who won. If both cards
     * have the same value, then return standoff (cards are not yet added to stingstack!).
     * @param property
     * @return
     */
    public int compareCardsProperty(Property property) {
        // first read property-values of both cards
        double p1Value = engine.getP1().getCurrentCard().getValue(property);
        double p2Value = engine.getP2().getCurrentCard().getValue(property);
        // now compare
        if(property.biggerWins()) {
            if(p1Value > p2Value) {
                return engine.PLAYER1;
            }
            if(p2Value > p1Value) {
                return engine.PLAYER2;
            }
        } else {
            if(p1Value < p2Value) {
                return engine.PLAYER1;
            }
            if(p2Value < p1Value) {
                return engine.PLAYER2;
            }
        }
        return engine.STANDOFF;
    }

    /**
     * Returns the current card of any players card-deck.
     * @param playerId
     * @return
     */
    public Card getCurPlayerCard(int playerId) {
        Player player = engine.getPlayer(playerId);
        return player.getCurrentCard();
    }



}
