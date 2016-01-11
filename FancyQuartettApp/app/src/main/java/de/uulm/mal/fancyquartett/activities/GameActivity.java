package de.uulm.mal.fancyquartett.activities;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.security.Timestamp;
import java.util.List;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.enums.GameMode;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;
import layout.CardFragment;


public class GameActivity extends AppCompatActivity implements LocalDeckLoader.OnLocalDeckLoadedListener{

    // view components
    public TextView cardQuantityP1, cardQuantityP2;
    public ProgressBar progressBar;

    // game components
    private GameEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // find view components
        cardQuantityP1 = (TextView) findViewById(R.id.textView_YourCards);
        cardQuantityP2 = (TextView) findViewById(R.id.textView_OpponendsCards);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_Balance);

        // load gameDeck
        String deckname = getIntent().getExtras().getString("deckname");
        new LocalDeckLoader(getFilesDir()+ Settings.localFolder,deckname.toLowerCase(),this).execute();
    }

    /**
     * Callback Method for LocalDeckLoader
     *
     * @param offlineDeck
     */
    @Override
    public void onDeckLoaded(OfflineDeck offlineDeck) {
        // create gameEngine
        engine = new GameEngine(getApplicationContext(), offlineDeck);
        engine.initialiseGUI();
        // create cardFragment
        CardFragment fragment = CardFragment.newInstance(offlineDeck.getCards().get(0));
        getSupportFragmentManager().beginTransaction().add(R.id.linLayout_Container,fragment).commit();
    }


    /**
     * Inner Class of GameActivity - GameEngine
     */
    public class GameEngine{

        private final String PLAYER1 = "player1";
        private final String PLAYER2 = "player2";

        // app attributes
        private final Context context;

        // game attributes
        private final OfflineDeck gameDeck;
        private GameMode gameMode;
        private Timestamp startTime, lastPlayed, endTime;
        private int maxRounds = 0;
        private int maxPoints = 0;
        private int timeout = 0;
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
         * @param gameDeck
         */
        public GameEngine(Context context, OfflineDeck gameDeck) {
            this.context = context;
            this.gameDeck = gameDeck;
        }

        /**
         *
         * @param maxRounds
         * @param maxPoints
         * @param timeout
         * @param currentPlayer
         * @param isMultiplayer
         */
        public void initialiseGame(int maxRounds, int maxPoints, int timeout, boolean currentPlayer, boolean isMultiplayer) {
            this.maxRounds = maxRounds;
            this.maxPoints = maxPoints;
            this.timeout = timeout;
            this.isMultiplayer = isMultiplayer;
            // TODO: mix gameDeck
            // TODO: spread cards to p1 and p2
            // initialisePlayer(p1, p2, p1Points, p2Points, p1Cards, p2Cards);

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
            progressBar.setProgress(50);
        }

    /*
        GUI - CONTROLLING
    */

        // TODO: some button enable/disable stuff, coloring (point-mode), progress bar, textViews (cardQuantity)

    /*
        CARD - CONTROLLING
    */

        public void mixDeck() {
            // TODO: implement some random sorting algorithm that mix the gameDeck
        }

        public void spreadCards() {
            // TODO: spread cards from mixed gameDeck to p1Cards and p2Cards
        }

        public void queueCard(Card card, String player) {
            // TODO: remove Card from p1Cards or p2Cards
            // TODO: add Card to p1Cards or p2Cards
        }

        public void removeCardFromPlayer(Card card, String player) {
            // TODO: remove a card from p1Cards or p2Cards
        }

        public void addCardToPlayer(Card card, String player) {
            // TODO: add a card to p1Cards or p2Cards
        }

    /*
        POINTS - CONTROLLING
     */

        // TODO: some point stuff

    /*
        DIALOG - CONTROLLING
     */

        public void showRoundWonDialog(Player p, CardAttribute attrP1, CardAttribute attrP2, Card newCard) {
            // TODO: show dialog if player won any round
        }

        public void showRoundLostDialog(Player p, CardAttribute attrP1, CardAttribute attrP2) {
            // TODO: show dialog if player lost any round
        }

        public void showGameWonDialog(Player p1, Player p2) {
            // TODO: show dialog if player won the game
        }

        public void showGameLostDialog(Player p1, Player p2) {
            // TODO: show dialog if player list the game
        }

    }
}
