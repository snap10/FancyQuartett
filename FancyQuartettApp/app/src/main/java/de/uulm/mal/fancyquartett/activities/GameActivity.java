package de.uulm.mal.fancyquartett.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.controller.CardController;
import de.uulm.mal.fancyquartett.controller.PlayerController;
import de.uulm.mal.fancyquartett.controller.StatisticController;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.dialog.GameEndDialog;
import de.uulm.mal.fancyquartett.dialog.RoundEndDialog;
import de.uulm.mal.fancyquartett.enums.GameMode;
import de.uulm.mal.fancyquartett.enums.KILevel;
import de.uulm.mal.fancyquartett.interfaces.OnDialogButtonClickListener;
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
    public void onCardFragmentAttributeInteraction(Property property, double value, CardAttribute cardAttribute) {
        engine.handleCardAttrSelect(cardAttribute);
    }


    /**
     * Inner Class of GameActivity - GameEngine
     */
    public class GameEngine implements Serializable, OnDialogButtonClickListener {

        public static final int STANDOFF = 0;
        public static final int PLAYER1 = 1;
        public static final int PLAYER2 = 2;

        // player attributes
        private int curPlayer = 0;
        private int playerWonRound = 0;
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
        private int gameTime = 0;
        private int timeout = 0;
        private KILevel kiLevel;
        private boolean isMultiplayer = false;
        private ArrayList<Card> stingStack;

        // app attributes
        private final Context context;

        // controller
        private CardController cardCtrl;
        private PlayerController playerCtrl;
        private StatisticController statisticCtrl;

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
            // create Controller
            this.cardCtrl = new CardController(this);
            this.playerCtrl = new PlayerController(this);
            this.statisticCtrl = new StatisticController(this);
            // create StingStack
            this.stingStack = new ArrayList<Card>();
            // get game-specific parameters
            this.gameMode = bundleGameMode;
            this.kiLevel = bundleKILevel;
            this.isMultiplayer = bundleIsMultiplayer;
            this.maxRounds = bundleMaxRounds;
            this.timeout = bundleRoundTimeout;
            this.maxPoints = bundleGamePoints;
            // identify player for first move
            if(Math.random() < 0.5) {
                this.curPlayer = PLAYER1;
            } else {
                this.curPlayer = PLAYER2;
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
            // create players
            if(isMultiplayer) {
                p1 = new Player(PLAYER1, bundleP1Name);
                p2 = new Player(PLAYER2, bundleP2Name);
            } else {
                p1 = new Player(PLAYER1, "You");
                p2 = new Player(PLAYER2, "KI");
            }
            // shuffle cards
            ArrayList<Card> cards = cardCtrl.shuffleCards(gameDeck.getCards());
            // spread cards
            cardCtrl.spreadCards(cards, p1, p2);
        }

        /**
         * Initialise all necessary GUI-elements.
         */
        public void initialiseGUI() {
            // display current card of beginning player
            // TODO: disable items if KI begins
            cardFragment = CardFragment.newInstance(getPlayer(curPlayer).getCurrentCard());
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

        public void exitGame() {
            // TODO: show confirm-dialog
        }

        public void handleCardAttrSelect(CardAttribute cardAttribute) {
            // identify winner of round
            int playerWonRound = cardCtrl.compareCardsProperty(cardAttribute.getProperty());
            engine.setPlayerWonRound(playerWonRound);
            // show RoundEndDialog
            engine.showRoundEndDialog(cardAttribute, playerWonRound);
        }

        public void initialiseNextRound(){
            // handleCards
            cardCtrl.handlePlayerCards(playerWonRound);
            // check if player won game
            int playerWonGame = playerCtrl.checkPlayerWon();
            if(playerWonGame == PLAYER1 || playerWonGame == PLAYER2) {
                engine.showGameEndDialog(this, playerWonGame);
            } else {
                // change current player
                if(getCurPlayer() != playerWonRound) {
                    playerCtrl.changeCurrentPlayer();
                }
                // initialise next round
                if(curPlayer != PLAYER1) {
                    // show p2 next card
                    showPlayer2NextCard();
                    // TODO: disable item clicks from p1
                    // start ki task
                    if(kiLevel == KILevel.Soft) {
                        SoftKiTask softKiTask = new SoftKiTask(cardCtrl.getCurPlayerCard(curPlayer), GameActivity.this);
                        softKiTask.execute();
                    }
                    if(kiLevel == KILevel.Medium) {
                        //MediumKiTask mediumKiTask = new MediumKiTast(enginge.getCurPlayerCard(curPlayer), GameActivity.this);
                        //mediumKiTask.execute();
                    }
                    if(kiLevel == KILevel.Hard) {
                        //HardmKiTask hardKiTask = new HardKiTast(enginge.getCurPlayerCard(curPlayer), GameActivity.this);
                        //hardKiTask.execute();
                    }
                } else {
                    showPlayer1NextCard();
                }
            }
            // TODO: display KI Playtime (Progressbar)
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
        DIALOG
         */

        /**
         * Creates and shows a dialog, in which the winner and the loser of a game-round will be
         * displayed. Game continues after callback from this dialog.
         * @param cardAttribute
         * @param playerWonRound
         */
        public void showRoundEndDialog(CardAttribute cardAttribute, int playerWonRound) {
            Card p1Card = p1.getCurrentCard();
            Card p2Card = p2.getCurrentCard();
            String p1Name = p1.getName();
            String p2Name = p2.getName();
            DialogFragment dialog = new RoundEndDialog().newInstance(this, p1, p2, cardAttribute, playerWonRound);
            dialog.show(getFragmentManager(), "RoundEndDialog");
        }

        public void showGameEndDialog(GameEngine engine, int playerWon) {
            Player playerWonGame = getPlayer(playerWon);
            DialogFragment dialog = new GameEndDialog().newInstance(this, playerWonGame);
            dialog.show(getFragmentManager(), "GameEndDialog");
            // TODO: show statistics
        }

        /*
        LISTENER
         */

        @Override
        public void OnDialogPositiveClick(DialogFragment dialog) {
            // check if Callback is from GameEndDialog
            if(dialog instanceof GameEndDialog) {
                // TODO: write statistics
                // close game and go back to main_activity
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                // initialise next Round
                initialiseNextRound();
            }
        }

        @Override
        public void OnDialogNevativeClick(DialogFragment dialog) {
            // Do nothing
        }

        @Override
        public void OnDialogNeutralClick(DialogFragment dialog) {
            // Do nothing
        }

        /*
        GETTERS
         */

        public int getCurPlayer() {
            return curPlayer;
        }

        public KILevel getKiLevel() {
            return kiLevel;
        }

        public Player getP1() {
            return p1;
        }

        public Player getP2() {
            return p2;
        }

        public Player getPlayer(int playerId) {
            if(playerId == PLAYER1) {
                return p1;
            } else {
                return p2;
            }
        }

        public ArrayList<Card> getStingStack() {
            return stingStack;
        }

        public GameMode getGameMode() {
            return gameMode;
        }

        public int getMaxPoints() {
            return maxPoints;
        }

        /*
        SETTERS
         */

        public void setLastPlayed() {
            Date date = new Date();
            this.lastPlayed = new Timestamp(date.getTime());
        }

        public void setStingStack(ArrayList<Card> stingStack) {
            this.stingStack = stingStack;
        }

        public void setCurPlayer(int curPlayer) {
            this.curPlayer = curPlayer;
        }

        public void setPlayerWonRound(int playerWonRound) {
            this.playerWonRound = playerWonRound;
        }

    }


}
