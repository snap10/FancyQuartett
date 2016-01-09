package de.uulm.mal.fancyquartett.utils;

import android.content.Context;
import android.widget.LinearLayout;

import java.security.Timestamp;
import java.util.List;

import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.Deck;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.enums.GameMode;

/**
 * Created by Lukas on 07.01.2016.
 */
public class GameEngine {

    // app attributes
    private Context mContext;

    // game attributes
    private final OfflineDeck offlinedeck;
    private GameMode gameMode;
    private Timestamp startTime, lastPlayed, endTime;
    private int maxRounds = 0;
    private int maxPoints = 0;
    private int timeout = 0;
    private boolean currentPlayer = false;
    private boolean isMultiplayer = false;

    // player1 attributes
    private Player p1;
    private int p1Points;
    private List<Card> p1Cards;

    // player2 attributes
    private Player p2;
    private int p2Points;
    private List<Card> p2Cards;


    /**
     *
     * @param context
     * @param offlineDeck
     */
    public GameEngine(Context context, OfflineDeck offlineDeck) {
        this.mContext = context;
        this.offlinedeck = offlineDeck;
    }

    /**
     *
     * @param maxRounds
     * @param maxPoints
     * @param timeout
     * @param currentPlayer
     * @param isMultiplayer
     */
    public void initialiseGame(int maxRounds, int maxPoints, int timeout,boolean currentPlayer, boolean isMultiplayer) {
        this.maxRounds = maxRounds;
        this.maxPoints = maxPoints;
        this.timeout = timeout;
        this.isMultiplayer = isMultiplayer;
    }

    /**
     *
     * @param p1
     * @param p2
     * @param p1Points
     * @param p2Points
     * @param p1Cards
     * @param p2Cards
     */
    public void initialisePlayer(Player p1, Player p2, int p1Points, int p2Points, List<Card> p1Cards, List<Card> p2Cards) {
        this.p1 = p1;
        this.p2 = p2;
        this.p1Points = p1Points;
        this.p2Points = p2Points;
        this.p1Cards = p1Cards;
        this.p2Cards = p2Cards;
    }

    public void initialiseGUI() {

    }

    //TODO some stuff with Deckdata


}


