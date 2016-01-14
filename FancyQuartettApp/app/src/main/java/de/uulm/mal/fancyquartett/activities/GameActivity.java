package de.uulm.mal.fancyquartett.activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.dialog.RoundEndDialog;
import de.uulm.mal.fancyquartett.enums.GameMode;
import de.uulm.mal.fancyquartett.enums.KILevel;
import de.uulm.mal.fancyquartett.tasks.SoftKiTask;
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
    private boolean bundleIsMultiplayer;
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
            bundleIsMultiplayer = intentbundle.getBoolean("multiplayer");
            if(bundleIsMultiplayer){
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
            int deckID = getIntent().getExtras().getInt("deckid");
            new LocalDeckLoader(getFilesDir() + Settings.localFolder, deckID, this).execute();
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
        engine.initialiseGame();
        engine.startGame();
    }

    @Override
<<<<<<< HEAD
    public void onCardFragmentAttributeInteraction(Property property, float value, CardAttribute cardAttribute) {
        // identify playerWonRound
        int playerWonRound = engine.compareCardsProperty(property);
        // show RoundEndDialog
        engine.showRoundEndDialog(cardAttribute, playerWonRound);
        // handleCards
        engine.handlePlayerCards(playerWonRound);
        // check if player won game
        int playerWonGame = engine.checkPlayerWon();
        System.out.println("player " + playerWonRound + "won round");
        if(playerWonGame == engine.PLAYER1 || playerWonGame == engine.PLAYER2) {
            //engine.showGameEndDialog(playerWonRound, statistics);
        } else {
            // chance current player
            if(engine.getCurPlayer() != playerWonRound) {
                engine.changeCurrentPlayer();
            }
            // initialise next round
            int curPlayer = engine.getCurPlayer();
            if(curPlayer != engine.PLAYER1) {
                // show p2 next card
                engine.showPlayer2NextCard();
                // start ki task
                KILevel kiLevel = engine.getKiLevel();
                if(kiLevel == KILevel.Soft) {
                    SoftKiTask softKiTask = new SoftKiTask(engine.getCurPlayerCard(curPlayer), this);
                    softKiTask.execute();
                }
                if(kiLevel == KILevel.Medium) {
                    //MediumKiTask mediumKiTask = new MediumKiTast(enginge.getCurPlayerCard(curPlayer), this);
                    //mediumKiTask.execute();
                }
                if(kiLevel == KILevel.Hard) {
                    //HardmKiTask hardKiTask = new HardKiTast(enginge.getCurPlayerCard(curPlayer), this);
                    //hardKiTask.execute();
                }
            } else {
                engine.showPlayer1NextCard();
            }
=======
    public void onCardFragmentAttributeInteraction(Property property, double value, CardAttribute cardAttribute) {
        if(engine.toTheEndTask != null) {
            engine.toTheEndTask.onCardAttrClicked(property, value, cardAttribute);
>>>>>>> RestAPICALLS
        }
    }


    /**
     * Inner Class of GameActivity - GameEngine
     */
    public class GameEngine implements Serializable {

        public static final int STANDOFF = 0;
        public static final int PLAYER1 = 1;
        public static final int PLAYER2 = 2;

        // player attributes
        private int curPlayer = 0;
        private Player p1;
        private Player p2;

        // ki attributes
        SoftKiTask softAiTask;
        //MediumAITask mediumAiTask;
        //HardATTask hardAiTask;

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
        private boolean isMultiplayer = false;
        private ArrayList<Card> stingStack;

        // app attributes
        private final Context context;

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
         * Reads all necessary data from GameActivity for Game.
         */
        public void initialiseGame() {
            this.gameMode = bundleGameMode;
            this.kiLevel = bundleKILevel;
            this.stingStack = new ArrayList<Card>();
            this.isMultiplayer = bundleIsMultiplayer;
            if(Math.random() < 0.5) {
                this.curPlayer = PLAYER1;
            } else {
                this.curPlayer = PLAYER2;
            }
            System.out.println("Player " + curPlayer + " beginns!");
            this.maxRounds = bundleMaxRounds;
            if(gameMode == GameMode.Time) {
                this.timeout = bundleRoundTimeout;
            }
            if(gameMode == GameMode.Points) {
                this.maxPoints = bundleGamePoints;
            }
            if(gameMode == GameMode.Hotseat) {
            }

            // initialise players
            initialisePlayers();
            // initialise GUI
            initialiseGUI();
        }

        /**
         * Initialise all necessary player data.
         */
        public void initialisePlayers() {
            // shuffle cards
            ArrayList<Card> cards = shuffleCards(gameDeck.getCards());
            // spread cards
            ArrayList<Card> p1Cards = new ArrayList<Card>();
            ArrayList<Card> p2Cards = new ArrayList<Card>();
            for(int i=0; i<cards.size(); i++) {
                Card card = cards.get(i);
                if((i%2) != 0) {
                    p1Cards.add(card);
                } else {
                    p2Cards.add(card);
                }
            }
            // create players
            if(isMultiplayer) {
                this.p1 = new Player(bundleP1Name, p1Cards);
                this.p2 = new Player(bundleP2Name, p2Cards);
            } else {
                this.p1 = new Player("You", p1Cards);
                this.p2 = new Player("Ki", p2Cards);
            }
        }

        /**
         * Initialise all necessary GUI-elements.
         */
        public void initialiseGUI() {
            // create cardFragment
            cardFragment = CardFragment.newInstance(offlineDeck.getCards().get(0));
            getSupportFragmentManager().beginTransaction().replace(R.id.linLayout_Container, cardFragment).commit();
            progressBar.setProgress(50);
        }

    /*
        GAME - CONTROLLING
    */

        public void startGame() {
            // set lastPlayer Timestamp
            setLastPlayed();
            // start game
            if(curPlayer != PLAYER1) {
                // TODO: diable cardButtons
                if(kiLevel == KILevel.Soft) {
                    SoftKiTask softKiTask = new SoftKiTask(p2.getCurrentCard(), GameActivity.this);
                    softKiTask.execute();
                }
                if(kiLevel == KILevel.Medium) {
                    // TODO: MediumKiTask
                }
                if(kiLevel == KILevel.Hard) {
                    // TODO: HardKiTask
                }
            }

        }

        public void exitSingleplayerGame() {

        }

        public void startSoftAiTask() {

        }

        public void changeCurrentPlayer() {
            if(curPlayer == PLAYER1) {
                curPlayer = PLAYER2;
            } else {
                curPlayer = PLAYER1;
            }
        }

        /**
         * Checks if player won. Returns -1 if no player has yet won.
         * @return
         */
        public int checkPlayerWon() {
            if(gameMode == GameMode.Points) {
                if(p1.getPoints() >= maxPoints) return PLAYER1;
                if(p2.getPoints() >= maxPoints) return PLAYER2;
            } else if(gameMode == GameMode.Time) {
                if(p1.getCards().size() > p2.getCards().size()) return PLAYER1;
                if(p2.getCards().size() > p1.getCards().size()) return PLAYER2;
            } else {
                if(p1.getCards().size() == 0) return PLAYER2;
                if(p2.getCards().size() == 0) return PLAYER1;
            }
            return -1;
        }

    /*
        GUI - CONTROLLING
    */

        /**
         * Shows the first card in player1s card-deck.
         * Be sure you've done handlePlayerCards(...) before!
         */
        public void showPlayer1NextCard() {
            cardFragment = cardFragment.newInstance(p1.getCurrentCard());
            getSupportFragmentManager().beginTransaction().replace(R.id.linLayout_Container, cardFragment).commit();
        }

        /**
         * Shows the first card in player2s card-deck.
         * Be sure you've done handlePlayerCards(...) before!
         */
        public void showPlayer2NextCard() {
            cardFragment = cardFragment.newInstance(p2.getCurrentCard());
            getSupportFragmentManager().beginTransaction().replace(R.id.linLayout_Container, cardFragment).commit();
        }

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
         * Queues first (current) card of player1 or player2 at the end of his deck.
         * @param player
         */
        public void queueCard(int player) {
            if(player == PLAYER1) {
                p1.queueCard();
            } else {
                Card card = p2.removeCurrentCard();
                p2.queueCard();
            }
        }

        /**
         * Removes current card from given players card-deck.
         * @param player
         * @return
         */
        public Card removeCardFromPlayer(int player) {
            if(player == PLAYER1) {
                return p1.removeCurrentCard();
            } else {
                return p2.removeCurrentCard();
            }
        }

        /**
         * Adds a given card to given players card-deck.
         * @param card
         * @param player
         */
        public void addCardToPlayer(Card card, int player) {
            if(player == PLAYER1) {
                p1.addNewCard(card);
            } else {
                p2.addNewCard(card);
            }
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
                p1.addNewCards(stingStack);
            } else {
                p2.addNewCards(stingStack);
            }
            stingStack = new ArrayList<Card>();
        }

        /**
         * Removes card from loser and adds it to winners card-deck. If stingStack contains some
         * cards, then they will be addet to to winners card-deck.
         * @param playerWon
         */
        public void handlePlayerCards(int playerWon) {
            if(playerWon != engine.STANDOFF) {
                if(playerWon == engine.PLAYER1) {
                    Card card = engine.removeCardFromPlayer(engine.PLAYER2);
                    engine.queueCard(playerWon);
                    engine.addCardToPlayer(card, playerWon);
                }
                if(playerWon == engine.PLAYER2) {
                    Card card = engine.removeCardFromPlayer(engine.PLAYER1);
                    engine.queueCard(playerWon);
                    engine.addCardToPlayer(card, playerWon);
                }
                if(stingStack.size() > 0) {
                    removeCardsFromStingStag(playerWon);
                }
            } else {
                Card p1Card = engine.removeCardFromPlayer(engine.PLAYER1);
                Card p2Card = engine.removeCardFromPlayer(engine.PLAYER2);
                engine.addCardsToStingStag(p1Card, p2Card);
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
            float p1Value = p1.getCurrentCard().getValues().get(property);
            float p2Value = p2.getCurrentCard().getValues().get(property);
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
            return STANDOFF;
        }

    /*
        DIALOG - CONTROLLING
     */

        public void showRoundEndDialog(CardAttribute cardAttribute, int playerWonRound) {
            // TODO: show dialog if player won any round
            Card p1Card = p1.getCurrentCard();
            Card p2Card = p2.getCurrentCard();
            String p1Name = p1.getName();
            String p2Name = p2.getName();
            DialogFragment dialog = new RoundEndDialog().newInstance(p1Card, p2Card, cardAttribute, playerWonRound, p1Name, p2Name);
            dialog.show(getFragmentManager(), "bla");
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

    /*
        GETTERS AND SETTERS
     */

        public int getCurPlayer() {
            return curPlayer;
        }

        public void setLastPlayed() {
            Date date = new Date();
            this.lastPlayed = new Timestamp(date.getTime());
        }

        public KILevel getKiLevel() {
            return kiLevel;
        }

        public Card getCurPlayerCard(int player) {
            if(player == PLAYER1) {
                return p1.getCurrentCard();
            } else {
                return p2.getCurrentCard();
            }
        }
    }


}
