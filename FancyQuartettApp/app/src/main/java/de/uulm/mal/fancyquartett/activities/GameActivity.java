package de.uulm.mal.fancyquartett.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.CardAttrViewAdapter;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.enums.GameMode;
import de.uulm.mal.fancyquartett.enums.KILevel;
import de.uulm.mal.fancyquartett.threads.ToTheEndThread;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;
import layout.CardFragment;


public class GameActivity extends AppCompatActivity implements CardFragment.OnFragmentInteractionListener, LocalDeckLoader.OnLocalDeckLoadedListener {

    // view components
    public TextView cardQuantityP1, cardQuantityP2;
    public ProgressBar progressBar;
    CardFragment cardFragment;

    // game components
    private GameEngine engine;
    private OfflineDeck offlineDeck;

    // bundle settings
    private GameMode bundleGameMode;
    private KILevel bundleKILevel;
    private int bundleRoundTimeout;
    private int bundleMaxRounds;
    private int bundleGameTime;
    private int bundleGamePoints;
    private boolean bundlemultiplayer;
    private String bundleP1Name;
    private String bundleP2Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // find view components
        cardQuantityP1 = (TextView) findViewById(R.id.textView_YourCards);
        cardQuantityP2 = (TextView) findViewById(R.id.textView_OpponendsCards);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_Balance);

        // check Bundle
        Bundle intentbundle = getIntent().getExtras();
        offlineDeck = (OfflineDeck) intentbundle.getSerializable("offlinedeck");
        if (offlineDeck != null) {
            bundleGameMode = (GameMode) intentbundle.get("gamemode");
            bundleKILevel = (KILevel) intentbundle.get("kilevel");
            bundlemultiplayer = intentbundle.getBoolean("multiplayer");
            if(bundlemultiplayer){
                bundleP1Name = intentbundle.getString("playername1");
                bundleP2Name = intentbundle.getString("playername2");
            }
            bundleRoundTimeout = intentbundle.getInt("roundtimeout");
            bundleMaxRounds = intentbundle.getInt("maxrounds");
            if (bundleGameMode == GameMode.Time) {
                bundleGameTime = intentbundle.getInt("gametime");
            } else if (bundleGameMode == GameMode.Points) {
                bundleGamePoints = intentbundle.getInt("gamepoints");
            }
            onDeckLoaded(offlineDeck);
        } else {
            //sth went wrong with the Intent or an old Method is used ...
            String deckname = getIntent().getExtras().getString("deckname");
            new LocalDeckLoader(getFilesDir() + Settings.localFolder, deckname.toLowerCase(), this).execute();
        }
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
        engine.initialiseSingleplayerGame();
        engine.startSingleplayerGame();
    }



    /**
     * //TODO @Lukas: Hier kannst du die Engine mit methoden ansteuern...
     * If a card attribute is clicked, the event is routed to this Method
     *
     * @param property
     * @param value
     * @param cardAttribute
     */
    @Override
    public void onCardFragmentAttributeInteraction(Property property, float value, CardAttribute cardAttribute) {
        //TODO sth stuff with your engine z.B
        engine.compareCardsProperty(property);
    }


    /**
     * Inner Class of GameActivity - GameEngine
     */
    public class GameEngine implements Serializable {

        private final int STINGSTACK = -1;
        private final int PLAYER1 = 1;
        private final int PLAYER2 = 2;
        private final boolean PLAYER1_BEGINS = false;
        private final boolean PLAYER2_BEGINS = true;

        // app attributes
        private final Context context;

        // game attributes
        private final OfflineDeck gameDeck;
        private GameMode gameMode;
        private Timestamp startTime = null;
        private Timestamp lastPlayed = null;
        private Timestamp endTime = null;
        private int curRound = 0;
        private int maxRounds = 0;
        private int maxPoints = 0;
        private int timeout = 0;
        private KILevel kiLevel;
        private boolean playerBegins = false; // false (player1) , true (player2)
        private boolean currentPlayer = false; // false (player1) , true (player2)
        private boolean isMultiplayer = false;
        private List<Card> stingStack;

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
         * @param context
         * @param gameDeck
         * @param lastPlayed
         */
        public GameEngine(Context context, OfflineDeck gameDeck, Timestamp lastPlayed) {
            this.context = context;
            this.gameDeck = gameDeck;
            this.lastPlayed = lastPlayed;
        }


        /**
         * Reads all necessary data from GameActivity.
         */
        public void initialiseSingleplayerGame() {
            this.gameMode = bundleGameMode;
            this.kiLevel = bundleKILevel;
            this.playerBegins = Math.random() < 0.5;
            this.maxRounds = bundleMaxRounds;
            if(gameMode == GameMode.Time) {
                this.timeout = bundleRoundTimeout;
            }
            if(gameMode == GameMode.Points) {
                this.maxPoints = bundleGamePoints;
            }
            if(gameMode == GameMode.Hotseat) {
            }
            initialisePlayer();
            initialiseGUI();
        }

        public void initialiseMultiplayerGame() {
            // TODO
        }

        /**
         * Initialise all necessary player data.
         */
        public void initialisePlayer() {
            this.p1Points = 0;
            this.p2Points = 0;
            ArrayList<Card> cards = shuffleCards(gameDeck.getCards());
            spreadCards(cards);
        }

        public void initialiseGUI() {
            // create cardFragment
            cardFragment = CardFragment.newInstance(offlineDeck.getCards().get(0));
            getSupportFragmentManager().beginTransaction().add(R.id.linLayout_Container, cardFragment).commit();
            progressBar.setProgress(50);
        }

    /*
        GAME - CONTROLLING
    */

        public void startSingleplayerGame() {
            // set lastPlayer Timestamp
            setLastPlayed();
            // start game
            if(gameMode == GameMode.ToTheEnd) {
                ToTheEndThread thread = new ToTheEndThread(this);
                thread.start();
            }
        }

        public void exitSingleplayerGame() {

        }

        public int checkPlayerWon(GameMode gameMode) {
            if(gameMode == GameMode.Points) {
                if(p1Points >= maxPoints) return PLAYER1;
                if(p2Points >= maxPoints) return PLAYER2;
            } else if(gameMode == GameMode.Time) {
                if(p1Cards.size() > p2Cards.size()) return PLAYER1;
                if(p2Cards.size() > p1Cards.size()) return PLAYER2;
            } else {
                if(p1Cards.size() == 0) return PLAYER2;
                if(p2Cards.size() == 0) return PLAYER1;
            }
            return -1;
        }

        public void setLastPlayed() {
            Date date = new Date();
            this.lastPlayed = new Timestamp(date.getTime());
        }
    /*
        GUI - CONTROLLING
    */

        // TODO: some button enable/disable stuff, coloring (point-mode), progress bar, textViews (cardQuantity)

    /*
        CARD - CONTROLLING
    */

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
         * Spreads game-card-deck into player1-deck and player2-deck.
         * @param cards
         */
        public void spreadCards(ArrayList<Card> cards) {
            // first create new ArrayLists (in case they're filled with some fancy stuff)
            p1Cards = new ArrayList<Card>();
            p2Cards = new ArrayList<Card>();
            // now spread cards to player1 and player2
            for(int i=0; i<cards.size(); i++) {
                Card card = cards.get(i);
                if((i%2) != 0) {
                    addCardToPlayer(card, PLAYER1);
                } else {
                    addCardToPlayer(card, PLAYER2);
                }
            }
        }

        /**
         * Queues first (current) card of player1 or player2 at the end of his deck.
         * @param player
         */
        public void queueCard(int player) {
            if(player == PLAYER1) {
                Card card = p1Cards.remove(0);
                p1Cards.add(card);
            } else {
                Card card = p2Cards.remove(0);
                p2Cards.add(card);
            }
        }

        /**
         * Removes a given card from given players card-deck.
         * @param card
         * @param player
         */
        public void removeCardFromPlayer(Card card, int player) {
            if(isCardOfPlayer(card, player)) {
                if(player == PLAYER1) {
                    p1Cards.remove(p1Cards.indexOf(card));
                } else {
                    p2Cards.remove(p2Cards.indexOf(card));
                }
            }
        }

        /**
         * Adds a given card to given players card-deck.
         * @param card
         * @param player
         */
        public void addCardToPlayer(Card card, int player) {
            if(!isCardOfPlayer(card, player)) {
                if(player == PLAYER1) {
                    p1Cards.add(card);
                } else {
                    p2Cards.add(card);
                }
            }
        }

        /**
         * Checks if a player owns a given card.
         * @param card
         * @param player
         * @return
         */
        public boolean isCardOfPlayer(Card card, int player) {
            if(player == PLAYER1) {
               if(p1Cards.contains(card)) {
                   return true;
               }
            } else {
                if(p2Cards.contains(card)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Adds a the current cards of player1 and player2 into stingStack.
         * @param p1Card
         * @param p2Card
         */
        public void addCardsToStingStag(Card p1Card, Card p2Card) {
            stingStack.add(p1Card);
            stingStack.add(p2Card);
        }

        /**
         * Removes all cards from stingStack and adds them to given players card-deck.
         * @param player
         */
        public void removeCardsFromStingStag(int player) {
            if(player == PLAYER1) {
                p1Cards.addAll(stingStack);
            } else {
                p2Cards.addAll(stingStack);
            }
            stingStack = new ArrayList<Card>();
        }

        /**
         * Compares a property of two given cards and returns the player who won. If both cards
         * have the same value, then return stingstack (they are not yet added to stingstack).
         * @param property
         * @return
         */
        public int compareCardsProperty(Property property) {
            // first read property-values of both cards
            float p1Value = p1Cards.get(0).getValues().get(property);
            float p2Value = p2Cards.get(0).getValues().get(property);
            // now compare
            if(property.biggerWins()) {
                if(p1Value > p2Value) {
                    return PLAYER1;
                }
                if(p2Value > p1Value) {
                    return PLAYER2;
                }
            } else {
                if(p1Value < p2Value) {
                    return PLAYER1;
                }
                if(p2Value < p1Value) {
                    return PLAYER2;
                }
            }
            return STINGSTACK;
        }

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
