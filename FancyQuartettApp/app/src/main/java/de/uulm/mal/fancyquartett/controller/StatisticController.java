package de.uulm.mal.fancyquartett.controller;

import android.content.Context;
import android.content.SharedPreferences;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.GameActivity;
import layout.StatisticFragment;

/**
 * Created by Lukas on 14.01.2016.
 * This controller is used by the game activity and the main activity, more specifically the statistic fragment.
 * Through this controller the game writes to shared preferences and the statistic reads from them.
 * Shared preferences handling is encapsulated here. (mk)
 */
public class StatisticController {

    /**
     * Context of either the game activity or the main activity
     */
    Context context = null;

    private SharedPreferences sharedPref = null;

    /**
     * Constructor used by game
     * @param game
     */
    public StatisticController(GameActivity.GameEngine game) {
        context = game.getContext();
        init();
    }

    /**
     * Constructor used by statistic
     */
    public StatisticController(StatisticFragment statistic) {
        context = statistic.getContext();
        init();
    }

    private void init() {
        String sharedPrefName = context.getString(R.string.statisticSharedPreferencesKey);
        sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
    }

    private int read(int key) {
        String keyString = context.getString(key);
        return sharedPref.getInt(keyString, 0); // return zero if key is not yet present
    }

    private void plusOne(int key) {
        String keyString = context.getString(key);
        int value = sharedPref.getInt(keyString, 0);
        value++;
        sharedPref.edit().putInt(keyString, value).commit();
    }


    //== WRITER METHODS ==

    /**
     * Call after singleplayer game was completed.
     */
    public void gamesPlayedPlusOne(boolean won) {
        plusOne(R.string.statisticNumSingleplayerGamesPlayedKey);
        if(won) {
            plusOne(R.string.statisticNumSingleplayerGamesWonKey);
            System.out.println("won " + read(R.string.statisticNumSingleplayerGamesWonKey));
        }
        else {
            plusOne(R.string.statisticNumSingleplayerGamesLostKey);
            System.out.println("lost " + read(R.string.statisticNumSingleplayerGamesLostKey));
        }
        System.out.println("won="+gamesWon()+" lost="+gamesLost());
    }

    public void duelsMadePlusOne(boolean won) {
        plusOne(R.string.statisticNumSingleplayerDuelsMadeKey);
        if(won) plusOne(R.string.statisticNumSingleplayerDuelsWonKey);
        else plusOne(R.string.statisticNumSingleplayerDuelsLostKey);
    }

    public void multiplayerGamesPlayedPlusOne() {

    }


    //== READER METHODS ==

    public int gamesPlayed() {
        return read(R.string.statisticNumSingleplayerGamesPlayedKey);
    }

    public int gamesWon() {
        return read(R.string.statisticNumSingleplayerGamesWonKey);
    }

    public int gamesLost() {
        return read(R.string.statisticNumSingleplayerGamesLostKey);
    }

    public int duelsMade() {
        return read(R.string.statisticNumSingleplayerDuelsMadeKey);
    }

    public int duelsWon() {
        return read(R.string.statisticNumSingleplayerDuelsWonKey);
    }

    public int duelsLost() {
        return read(R.string.statisticNumSingleplayerDuelsLostKey);
    }
}
