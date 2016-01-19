package de.uulm.mal.fancyquartett.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import de.uulm.mal.fancyquartett.dialog.KiPlaysDialog;
import de.uulm.mal.fancyquartett.dialog.RoundEndDialog;
import de.uulm.mal.fancyquartett.enums.GameMode;
import de.uulm.mal.fancyquartett.enums.KILevel;
import de.uulm.mal.fancyquartett.interfaces.OnDialogButtonClickListener;
import de.uulm.mal.fancyquartett.tasks.SoftKiTask;
import de.uulm.mal.fancyquartett.tasks.PlayerTimeOutTask;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;
import layout.CardFragment;


public class GameActivity extends AppCompatActivity implements CardFragment.OnFragmentInteractionListener, LocalDeckLoader.OnLocalDeckLoadedListener {

    // view components
    private TextView tvCardQuantityP1, tvCardQuantityP2, tvCurPlayer, tvRoundsLeft, tvTimeLeft;
    private LinearLayout linLayoutRound, linLayoutTime;
    private ProgressBar pbBalance;
    private ProgressBar pbTimeout;
    private CardFragment cardFragment;
    private Menu menu;

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

        // build toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gameActivity_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // find view components
        linLayoutRound = (LinearLayout) findViewById(R.id.linLayout_Rounds);
        linLayoutTime = (LinearLayout) findViewById(R.id.linLayout_Time);
        tvCardQuantityP1 = (TextView) findViewById(R.id.textView_YourCards);
        tvCardQuantityP2 = (TextView) findViewById(R.id.textView_OpponendsCards);
        tvCurPlayer = (TextView) findViewById(R.id.textView_CurPlayer);
        tvRoundsLeft = (TextView) findViewById(R.id.textView_Rounds_Left);
        tvTimeLeft = (TextView) findViewById(R.id.textView_Time_Left);
        pbBalance = (ProgressBar) findViewById(R.id.progressBar_Balance);
        pbTimeout = (ProgressBar) findViewById(R.id.progressBar_Timeout);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        engine.stop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_rules) {
            // TODO: show Dialog with Rules
            return true;
        }
        return false;
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


    /**
     * Callback for computer player thread
     * @param property
     * @param value
     * @param cardAttribute
     */
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

        // tasks
        private transient SoftKiTask softAiTask;
        //private transient MediumAITask mediumAiTask;
        //private transient HardATTask hardAiTask;
        private transient PlayerTimeOutTask playerTimeOutTask;

        // dialogues
        private transient KiPlaysDialog kiPlaysDialog;

        // game attributes
        private final OfflineDeck gameDeck;
        private GameMode gameMode;
        private Timestamp startTime = null;
        private Timestamp lastPlayed = null;
        private Timestamp endTime = null;
        private int curRound = 0;
        private long curTime = 0;
        private int maxRounds = 0;
        private int maxPoints = 0;
        private int gameTime = 0;
        private int timeout = 0;
        private KILevel kiLevel;
        private boolean isMultiplayer = false;
        private boolean hasPlayerTimeout = false;
        private boolean hasMaxPoints = false;
        private boolean hasMaxRounds = false;
        private ArrayList<Card> stingStack;

        // flag used to prevent multiple dialoge showing and statistics counting
        private boolean gameover = false;

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
            // create Tasks
            this.playerTimeOutTask = new PlayerTimeOutTask(GameActivity.this, this, pbBalance);
            // create StingStack
            this.stingStack = new ArrayList<Card>();
            // get game-specific parameters
            this.gameMode = bundleGameMode;
            this.kiLevel = bundleKILevel;
            this.isMultiplayer = bundleIsMultiplayer;
            this.maxRounds = bundleMaxRounds;
            if(this.maxRounds != 0) {
                this.hasMaxRounds = true;
            }
            this.timeout = bundleRoundTimeout*1000; // in ms
            if(this.timeout != 0) {
                this.hasPlayerTimeout = true;
                System.out.println(timeout);
            }
            this.maxPoints = bundleGamePoints;
            if(this.maxPoints != 0) {
                this.hasMaxPoints = true;
            }
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
            cardFragment = CardFragment.newInstance(getPlayer(curPlayer).getCurrentCard());
            getSupportFragmentManager().beginTransaction().replace(R.id.linLayout_Container, cardFragment).commit();
        }

    /*
        GAME - CONTROLLING
    */

        public void startGame() {
            // create KiPlaysDialog
            kiPlaysDialog = new KiPlaysDialog().newInstance(this);
            kiPlaysDialog.setCancelable(false);
            // set lastPlayer Timestamp
            setLastPlayed();
            // start game
            if(curPlayer != PLAYER1) {
                // start KI
               startKiTask();
            } else {
                // workaround for later dismiss()
                kiPlaysDialog.show(getFragmentManager(), "KiPlaysDialog");
                kiPlaysDialog.dismiss();
            }
            // display balance
            handleBalance();
            // display toolbar info
            handleToolbarInfo();
            // start timeout timer if necessary
            handlePlayerTimeout();
        }

        /**
         * Starts a new Ki-Task.
         */
        private void startKiTask(){
            if(kiLevel == KILevel.Soft) {
                softAiTask = new SoftKiTask(p2.getCurrentCard(), GameActivity.this);
                softAiTask.execute();
            }
            if(kiLevel == KILevel.Medium) {
                // TODO: MediumKiTask
            }
            if(kiLevel == KILevel.Hard) {
                // TODO: HardKiTask
            }
        }

        public void exitGame() {
            // TODO: show confirm-dialog
            stop();
        }

        private void stopTasks() {
            if(!playerTimeOutTask.isCancelled()) playerTimeOutTask.cancel(true);
            if(softAiTask != null) softAiTask.cancel(true);
            //TODO cancel others too...
        }

        public void stop() {
            stopTasks();
            //... are there more things to clean up?
        }

        public void handleCardAttrSelect(CardAttribute cardAttribute) {
            // stop all running tasks
            stopTasks();
            // identify winner of round
            int playerWonRound = cardCtrl.compareCardsProperty(cardAttribute.getProperty());
            if(playerWonRound == -1) {
                // standoff (TODO: bug if player only has 1 card left)
            }
            engine.setPlayerWonRound(playerWonRound);
            // increase rounds played
            curRound++;
            // dismiss KiPlaysDialog
            if(!kiPlaysDialog.isHidden()) kiPlaysDialog.dismiss();
            // show RoundEndDialog
            engine.showRoundEndDialog(cardAttribute, playerWonRound);
            statisticCtrl.duelsMadePlusOne(playerWonRound == PLAYER1);
        }

        /**
         * Next round will be initialised if there isn't any winner.
         */
        public void initialiseNextRound(){
            // handleCards
            if(!cardCtrl.handlePlayerCards(playerWonRound)) {
                // one player's deck is empty
            }
            // check if player won game
            int playerWonGame = playerCtrl.checkPlayerWon();
            if(playerWonGame == PLAYER1 || playerWonGame == PLAYER2) {
                if(!gameover) {
                    // show dialog
                    engine.showGameEndDialog(this, playerWonGame);
                    // write statistic
                    statisticCtrl.gamesPlayedPlusOne(playerWonGame==PLAYER1);
                    gameover = true;
                }
            } else {
                // change current player if necessary
                if(curPlayer != playerWonRound && playerWonRound != STANDOFF) {
                    playerCtrl.changeCurrentPlayer();
                }
                // initialise next round
                if(curPlayer != PLAYER1) {
                    // show p2 next card
                    showPlayer2NextCard();
                    // start KI
                    startKiTask();
                } else {
                    showPlayer1NextCard();
                }
                // show new Card-Balance
                handleBalance();
                // show new toolbar info
                handleToolbarInfo();
                // update timeout progressbar
                handlePlayerTimeout();
            }
        }

        /**
         * Sets Balance UI-Elements and displays it.
         */
        public void handleBalance() {
            // calculation requires double values, don't ask why -.-
            double gameDeckSize = gameDeck.getCards().size();
            double p1Size = p1.getCards().size();
            double p2Size = p2.getCards().size();
            double progress = 100 * ( (gameDeckSize - p2Size) / gameDeckSize );
            tvCardQuantityP1.setText(p1.getName() + ": " + (int) p1Size);
            tvCardQuantityP2.setText(p2.getName() + ": " + (int) p2Size);
            pbBalance.setProgress((int) progress);
        }

        /**
         * Sets up timeout-progressbar and starts TimeOutTask. If current player is KI,
         * then it will show a waiting-dialog.
         */
        public void handlePlayerTimeout() {
            tvCurPlayer.setText("It's " + getPlayer(curPlayer).getName() + " turn!");
            if(curPlayer == PLAYER1) {
                if(hasPlayerTimeout) {
                    pbTimeout.setMax(timeout);
                    pbTimeout.setProgress(timeout);
                    // start task
                    playerTimeOutTask.cancel(false);
                    playerTimeOutTask = new PlayerTimeOutTask(GameActivity.this, this, pbTimeout);
                    playerTimeOutTask.execute();
                } else {
                    pbTimeout.setMax(0);
                    pbTimeout.setProgress(0);
                }
            } else {
                if(isMultiplayer) {
                    // TODO: set player2 timeout (copy from above?!)
                } else {
                    pbTimeout.setMax(0);
                    pbTimeout.setProgress(0);
                    // show dialog
                    kiPlaysDialog.show(getFragmentManager(), "KiPlaysDialog");
                }

            }
        }

        /**
         * Displays Time-Left and Rounds-Left in Toolbar if necessary.
         */
        public void handleToolbarInfo() {
            // check if info is necessary
            if(gameMode != GameMode.Time) {
                linLayoutTime.setVisibility(View.GONE);
            } else {
                tvTimeLeft.setText("" + (gameTime - curTime));
                // TODO: format time
            }
            if(!hasMaxRounds){
                linLayoutRound.setVisibility(View.GONE);
            } else {
                tvRoundsLeft.setText((curRound+1) + " / " + maxRounds);
            }
        }



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


        public void disableCard(){
            // TODO
        }

        public void enableCard(){
            // TODO
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
        }

        /*
        LISTENER
         */

        @Override
        public void OnDialogPositiveClick(DialogFragment dialog) {
            // check if Callback is from GameEndDialog
            if(dialog instanceof GameEndDialog) {
                stopTasks();
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

        public boolean getHasMaxRounds() {
            return hasMaxRounds;
        }

        public int getCurRound() {
            return curRound;
        }

        public void increaseMaxRounds() {
            maxRounds++;
        }

        public boolean getHasPlayerTimeout() {
            return hasPlayerTimeout;
        }

        public int getTimeout() {
            return timeout;
        }

        public int getMaxRounds() {
            return maxRounds;
        }

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

        public Context getContext() { return context; }

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
