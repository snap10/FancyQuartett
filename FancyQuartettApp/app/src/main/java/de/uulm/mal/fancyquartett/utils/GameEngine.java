package de.uulm.mal.fancyquartett.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.activities.MainActivity;
import de.uulm.mal.fancyquartett.controller.CardController;
import de.uulm.mal.fancyquartett.controller.PlayerController;
import de.uulm.mal.fancyquartett.controller.StatisticController;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.dialog.GameEndDialog;
import de.uulm.mal.fancyquartett.dialog.KiPlaysDialog;
import de.uulm.mal.fancyquartett.dialog.RoundEndDialog;
import de.uulm.mal.fancyquartett.enums.GameMode;
import de.uulm.mal.fancyquartett.enums.KILevel;
import de.uulm.mal.fancyquartett.interfaces.OnDialogButtonClickListener;
import de.uulm.mal.fancyquartett.interfaces.OnGameTimeUpdateListener;
import de.uulm.mal.fancyquartett.tasks.GameTimeTask;
import de.uulm.mal.fancyquartett.tasks.PlayerTimeOutTask;
import de.uulm.mal.fancyquartett.tasks.SoftKiTask;
import layout.CardFragment;

/**
 * Created by Lukas on 20.01.2016.
 */
public class GameEngine implements Serializable, OnDialogButtonClickListener, OnGameTimeUpdateListener {

    // TODO: coloring (point-mode)

    public static final int STANDOFF = 0;
    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;

    // view components
    private transient View rootView;
    private transient TextView tvCardQuantityP1, tvCardQuantityP2, tvCurPlayer, tvRoundsLeft, tvTimeLeft;
    private transient LinearLayout linLayoutRound, linLayoutTime;
    private transient ProgressBar pbBalance;
    private transient ProgressBar pbTimeout;
    private transient CardFragment cardFragment;

    // app attributes
    private transient GameActivity gameActivity;
    private transient Context context;
    private transient FragmentManager fragmentManager;

    // tasks
    private transient SoftKiTask softAiTask;
    /* TODO
    private transient MediumAITask mediumAiTask;
    private transient HardATTask hardAiTask;
    */
    private transient PlayerTimeOutTask playerTimeOutTask;
    private transient GameTimeTask gameTimeTask;

    // dialogues
    private transient KiPlaysDialog kiPlaysDialog;

    // player attributes
    private int curPlayer = 0;
    private int playerWonRound = 0;
    private Player p1;
    private Player p2;

    // game attributes
    private final OfflineDeck gameDeck;
    private GameMode gameMode;
    private Timestamp startTime = null;
    private Timestamp lastPlayed = null;
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
    private boolean gameover = false;   // flag used to prevent multiple dialoge showing and statistics counting

    // controller
    private CardController cardCtrl;
    private PlayerController playerCtrl;
    private StatisticController statisticCtrl;

    /**
     * PLEASE DONT USE THIS CONSTRUCTOR !!! IT WILL BE REMOVED SOME TIME :D
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
     * @param args
     */
    public GameEngine(GameActivity gameActivity, Context context, View rootView, Bundle args) {
        this.gameActivity = gameActivity;
        this.context = context;
        this.rootView = rootView;
        // start read bundle data
            // game deck
            gameDeck = (OfflineDeck) args.getSerializable("offlinedeck");
            // game mode
            gameMode = (GameMode) args.get("gamemode");
            // ki level
            kiLevel = (KILevel) args.get("kilevel");
            // is multiplayer?
            isMultiplayer = args.getBoolean("multiplayer");
            // players
            if(isMultiplayer) {
                String p1Name = args.getString("playername1");
                String p2Name = args.getString("playername2");
                initialisePlayers(p1Name, p2Name);
            } else {
                initialisePlayers(null, null);
            }
            // round timeout
            // *1000 for millis
            timeout = args.getInt("roundtimeout") * 1000;
            // max rounds
            maxRounds = args.getInt("maxrounds");
            // game time
            if(gameMode == GameMode.Time) {
                // *1000*60 for millis
                gameTime = args.getInt("gametime") * 1000 * 60;
            }
            // max points
            if(gameMode == GameMode.Points) {
                maxPoints = args.getInt("gamepoints");
            }
        // end read bundle data
        // initialise Game
        initialiseGame();
    }


    /*
    SETUP
     */


    /**
     * Initialise all necessary game data.
     */
    private void initialiseGame() {
        // fragmentmanager
        fragmentManager = gameActivity.getSupportFragmentManager();
        // start time
        Date date = new Date();
        startTime = new Timestamp(date.getTime());
        // empty sting stack
        stingStack = new ArrayList<Card>();
        // game-specific parameters
        if (maxRounds != 0) hasMaxRounds = true;
        if (this.timeout != 0) hasPlayerTimeout = true;
        if (this.maxPoints != 0) hasMaxPoints = true;
    }

    /**
     * Initialise all necessary player data.
     */
    public void initialisePlayers(String p1Name, String p2Name) {
        // create players
        if (p1Name != null && p2Name != null) {
            p1 = new Player(PLAYER1, p1Name);
            p2 = new Player(PLAYER2, p2Name);
        } else {
            p1 = new Player(PLAYER1, "You");
            p2 = new Player(PLAYER2, "KI");
        }
        // shuffle cards
        cardCtrl = new CardController(this);
        if(gameDeck == null) System.out.println("TEST");
        ArrayList<Card> cards = cardCtrl.shuffleCards(gameDeck.getCards());
        // spread cards
        cardCtrl.spreadCards(cards, p1, p2);
        // identify player for first move
        if (Math.random() < 0.5)  curPlayer = PLAYER1;
        else curPlayer = PLAYER2;
    }

    /**
     * Initialise all necessary view components.
     * This function should be used before game start!
     * @param v
     */
    private void initialiseUi(View v){
        linLayoutRound = (LinearLayout) v.findViewById(R.id.linLayout_Rounds);
        linLayoutTime = (LinearLayout) v.findViewById(R.id.linLayout_Time);
        tvCardQuantityP1 = (TextView) v.findViewById(R.id.textView_YourCards);
        tvCardQuantityP2 = (TextView) v.findViewById(R.id.textView_OpponendsCards);
        tvCurPlayer = (TextView) v.findViewById(R.id.textView_CurPlayer);
        tvRoundsLeft = (TextView) v.findViewById(R.id.textView_Rounds_Left);
        tvTimeLeft = (TextView) v.findViewById(R.id.textView_Time_Left);
        pbBalance = (ProgressBar) v.findViewById(R.id.progressBar_Balance);
        pbTimeout = (ProgressBar) v.findViewById(R.id.progressBar_Timeout);
        showCurrentPlayerCard();
    }

    /**
     * Initialise all necessary controller.
     * This function should be user before game start!
     */
    private void initialiseCtrl() {
        this.cardCtrl = new CardController(this);
        this.playerCtrl = new PlayerController(this);
        this.statisticCtrl = new StatisticController(this);
    }


    /*
    GAME
    */


    /**
     * Starts new or saved game.
     */
    public void startGame() {
        // initialise UI
        initialiseUi(rootView);
        // controller(s)
        initialiseCtrl();
        // task(s)
        gameTimeTask = new GameTimeTask(this);
        playerTimeOutTask = new PlayerTimeOutTask(gameActivity, this, pbBalance);
        // dialog(ues)
        kiPlaysDialog = new KiPlaysDialog().newInstance(this);
        kiPlaysDialog.setCancelable(false);
        // last played timestamp
        updateLastPlayed();
        // start game
        if (curPlayer != PLAYER1) {
            // start KI
            if(!isMultiplayer) startKiTask();
        } else {
            // workaround for later dismiss() on this dialog
            kiPlaysDialog.show(fragmentManager, "KiPlaysDialog");
            kiPlaysDialog.dismiss();
        }
        // start GameTimeTask if GameMode is Time
        if (gameMode == GameMode.Time) {
            gameTimeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }
        // balance
        handleBalance();
        // toolbar info
        handleToolbarInfo();
        // timeout if necessary
        handlePlayerTimeout();
    }

    /**
     * Initialise end of game, writes statistic and shows GameEndDialog.
     * @param playerWonGame
     */
    private void finishGame(int playerWonGame) {
        if (!gameover) {
            // show dialog
            showGameEndDialog(this, playerWonGame);
            // write statistic
            statisticCtrl.gamesPlayedPlusOne(playerWonGame == PLAYER1);
            gameover = true;
        }
    }

    /**
     * Stops current game.
     */
    public void stop() {
        stopAllTasks();
        //... are there more things to clean up?
    }

    /**
     * Stops all running tasks.
     */
    private void stopAllTasks() {
        stopAiTasks();
        stopPlayerTimeoutTask();
        stopGameTimeTask();
        //TODO cancel others too...
    }

    /**
     * Stops all running ai-tasks.
     */
    private void stopAiTasks() {
        if (softAiTask != null) softAiTask.cancel(true);
        /*
        if(mediumAiTask != null) mediumAiTask.cancel(true);
        if(hardAiTask != null) mediumAiTask.cancel(true);
        */
    }

    /**
     * Stops PlayerTimeoutTask if running.
     */
    private void stopPlayerTimeoutTask() {
        if (playerTimeOutTask!=null&&!playerTimeOutTask.isCancelled()) playerTimeOutTask.cancel(true);
    }

    /**
     * Stops GameTimeTask if running.
     */
    private void stopGameTimeTask() {
        if (gameTimeTask!=null&&!gameTimeTask.isCancelled()) gameTimeTask.cancel(true);
    }

    /**
     * Starts a new Ki-Task.
     */
    private void startKiTask() {
        if (kiLevel == KILevel.Soft) {
            softAiTask = new SoftKiTask(p2.getCurrentCard(), gameActivity);
            softAiTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }
        if (kiLevel == KILevel.Medium) {
            // TODO: MediumKiTask
        }
        if (kiLevel == KILevel.Hard) {
            // TODO: HardKiTask
        }
    }

    /**
     * This function identify which player is winner of current round and shows RoundEndDialog.
     * Call this function after a player selected any card attribute.
     * @param cardAttribute
     */
    public void handleCardAttrSelect(CardAttribute cardAttribute) {
        // stop PlayerTimeoutTask
        stopPlayerTimeoutTask();
        // identify round winner
        playerWonRound = cardCtrl.compareCardsProperty(cardAttribute.getProperty());
        if (playerWonRound == -1) {
            // standoff (TODO: bug if player only has 1 card left)
        }
        // increase rounds played
        if(hasMaxRounds) curRound++;
        // dismiss KiPlaysDialog
        if (!kiPlaysDialog.isHidden()) kiPlaysDialog.dismiss();
        // show RoundEndDialog
        showRoundEndDialog(cardAttribute, playerWonRound);
        // statistic
        statisticCtrl.duelsMadePlusOne(playerWonRound == PLAYER1);
    }

    /**
     * Next round will be started if there isn't any winner.
     */
    public void startNextRound() {
        // handleCards
        if (!cardCtrl.handlePlayerCards(playerWonRound)) {
            // one player's deck is empty
        }
        // check if player won game
        int playerWonGame = playerCtrl.checkPlayerWon();
        if (playerWonGame == PLAYER1 || playerWonGame == PLAYER2) {
            finishGame(playerWonGame);
        } else {
            // change current player if necessary
            if (curPlayer != playerWonRound && playerWonRound != STANDOFF) {
                playerCtrl.changeCurrentPlayer();
            }
            // show next card
            showCurrentPlayerCard();
            // start next round
            if (curPlayer != PLAYER1) {
                // start KI
                if(!isMultiplayer) startKiTask();
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
     * Shows the first card in current players card-deck.
     */
    public void showCurrentPlayerCard() {
        cardFragment = CardFragment.newInstance(getPlayer(curPlayer).getCurrentCard());
        fragmentManager.beginTransaction().replace(R.id.linLayout_Container, cardFragment).commit();
    }


    /*
    HANDLER
     */


    /**
     * Sets Balance UI-Elements and displays it.
     */
    public void handleBalance() {
        // calculation requires double values, don't ask why -.-
        double gameDeckSize = gameDeck.getCards().size();
        double p1Size = p1.getCards().size();
        double p2Size = p2.getCards().size();
        double progress = 100 * ((gameDeckSize - p2Size) / gameDeckSize);
        tvCardQuantityP1.setText(p1.getName() + ": " + (int) p1Size);
        tvCardQuantityP2.setText(p2.getName() + ": " + (int) p2Size);
        pbBalance.setProgress((int) progress);
    }

    /**
     * Sets up timeout-progressbar and starts TimeOutTask. If current player is KI,
     * then it will show kiPlaysDialog.
     */
    public void handlePlayerTimeout() {
        // display current players name
        tvCurPlayer.setText("Current Player: " + getPlayer(curPlayer).getName());
        if (curPlayer == PLAYER1) {
            if (hasPlayerTimeout) { // start timeout
                pbTimeout.setMax(timeout);
                pbTimeout.setProgress(timeout);
                // start task
                playerTimeOutTask.cancel(false);
                playerTimeOutTask = new PlayerTimeOutTask(gameActivity, this, pbTimeout);
                playerTimeOutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            } else { // 'disable' progressbar
                pbTimeout.setMax(0);
                pbTimeout.setProgress(0);
            }
        } else {
            if (isMultiplayer) {
                // TODO: set player2 timeout (copy from above?!)
            } else { // 'disable' progressbar
                pbTimeout.setMax(0);
                pbTimeout.setProgress(0);
                // show dialog
                kiPlaysDialog.show(fragmentManager, "KiPlaysDialog");
            }

        }
    }

    /**
     * Displays Time-Left and Rounds-Left in Toolbar if necessary.
     */
    public void handleToolbarInfo() {
        // check if time info is necessary
        if (gameMode != GameMode.Time) {
            linLayoutTime.setVisibility(View.GONE);
        } else {
            long timeLeft = gameTime - curTime;
            // calculate hours / minutes / seconds
            int hours = (int) TimeUnit.MILLISECONDS.toHours(timeLeft);
            timeLeft = timeLeft - (hours * 3600000);
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeLeft);
            timeLeft = timeLeft - (minutes * 60000);
            int secons = (int) TimeUnit.MILLISECONDS.toSeconds(timeLeft);
            String time = "";
            if (hours > 0) {
                time = hours + "h " + minutes + "m";
            } else if (hours == 0 && minutes > 0) {
                time = minutes + "m";
            } else {
                time = secons + "s";
            }
            tvTimeLeft.setText(time);
        }
        // check if rounds info is necessary
        if (!hasMaxRounds) linLayoutRound.setVisibility(View.GONE);
        else tvRoundsLeft.setText((curRound + 1) + " / " + maxRounds);
    }


    /*
    DIALOGUES
     */


    /**
     * Creates and shows a dialog, in which the winner and the loser of the current round will be
     * displayed. Game continues after callback from this dialog.
     *
     * @param cardAttribute
     * @param playerWonRound
     */
    public void showRoundEndDialog(CardAttribute cardAttribute, int playerWonRound) {
        Card p1Card = p1.getCurrentCard();
        Card p2Card = p2.getCurrentCard();
        String p1Name = p1.getName();
        String p2Name = p2.getName();
        DialogFragment dialog = new RoundEndDialog().newInstance(this, p1, p2, cardAttribute, playerWonRound);
        dialog.show(fragmentManager, "RoundEndDialog");
    }

    /**
     * Creates and shows a dialog, in which the game winner will be displayed. Game terminates
     * after callback from this dialog.
     * @param engine
     * @param playerWon
     */
    public void showGameEndDialog(GameEngine engine, int playerWon) {
        Player playerWonGame = getPlayer(playerWon);
        DialogFragment dialog = new GameEndDialog().newInstance(this, playerWonGame);
        dialog.show(fragmentManager, "GameEndDialog");
    }


    /*
    LISTENER
     */


    @Override
    public void OnDialogPositiveClick(DialogFragment dialog) {
        // check if Callback is from GameEndDialog
        if (dialog instanceof GameEndDialog) {
            stopAllTasks();
            // close game and go back to main_activity
            Intent intent = new Intent(gameActivity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            gameActivity.startActivity(intent);
        } else {
            // initialise next Round
            startNextRound();
        }
    }

    @Override
    public void OnDialogNevativeClick(DialogFragment dialog) {
        // do nothing!
    }

    @Override
    public void OnDialogNeutralClick(DialogFragment dialog) {
        // do nothing!
    }

    @Override
    public void onGameTimeUpdate(long time) {
        if (time == 0) {
            // game ends
            int playerWonGame = playerCtrl.checkPlayerWon();
            if (playerWonGame == PLAYER1 || playerWonGame == PLAYER2) {
                finishGame(playerWonGame);
            } // TODO: else play +1 round
        } else {
            // display curTime
            curTime = time;
            handleToolbarInfo();
        }
    }


    /*
    GETTER
     */


    public int getGameTime() {
        return gameTime;
    }

    public long getCurTime() {
        return curTime;
    }

    public boolean getHasMaxRounds() {
        return hasMaxRounds;
    }

    public int getCurRound() {
        return curRound;
    }

    public void increaseMaxRounds() {
        maxRounds++;
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

    public Player getP1() {
        return p1;
    }

    public Player getP2() {
        return p2;
    }

    public Player getPlayer(int playerId) {
        if (playerId == PLAYER1) {
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

    public Context getContext() {
        return context;
    }


    /*
    SETTER
     */


    public void updateLastPlayed() {
        Date date = new Date();
        this.lastPlayed = new Timestamp(date.getTime());
    }

    public void setStingStack(ArrayList<Card> stingStack) {
        this.stingStack = stingStack;
    }

    public void setCurPlayer(int curPlayer) {
        this.curPlayer = curPlayer;
    }

    public OfflineDeck getGameDeck() {
        return gameDeck;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

}
