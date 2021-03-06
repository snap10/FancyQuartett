package de.uulm.mal.fancyquartett.controller;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.utils.GameEngine;
import layout.StatisticFragment;

/**
 * Created by Lukas on 14.01.2016.
 * This controller is used by the game activity and the main activity, more specifically the statistic fragment.
 * Through this controller the game writes to shared preferences and the statistic reads from them.
 * Shared preferences handling is encapsulated here. (mk)
 */
public class StatisticController implements Serializable {

    /**
     * Context of either the game activity or the main activity
     */
    private transient Context context = null;

    private transient SharedPreferences sharedPref = null;

    /**
     * Constructor used by game
     * @param game
     */
    public StatisticController(GameEngine game) {
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

    public void gamesPlayedPlusOne(boolean won, boolean isMultiplayer, boolean isSpectatorMode) {
        if(isSpectatorMode) return;
        if(isMultiplayer) {
            plusOne(R.string.statisticNumMultiplayerGamesPlayedKey);
            if(won) plusOne(R.string.statisticNumMultiplayerGamesWonKey);
            else plusOne(R.string.statisticNumMultiplayerGamesLostKey);
        } else {
            plusOne(R.string.statisticNumSingleplayerGamesPlayedKey);
            if(won) plusOne(R.string.statisticNumSingleplayerGamesWonKey);
            else plusOne(R.string.statisticNumSingleplayerGamesLostKey);
        }
    }

    public void duelsMadePlusOne(boolean won, boolean isMultiplayer, boolean isSpectatorMode) {
        if(isSpectatorMode) return;
        if(isMultiplayer) {
            plusOne(R.string.statisticNumMultiplayerDuelsMadeKey);
            if(won) plusOne(R.string.statisticNumMultiplayerDuelsWonKey);
            else plusOne(R.string.statisticNumMultiplayerDuelsLostKey);
        } else {
            plusOne(R.string.statisticNumSingleplayerDuelsMadeKey);
            if(won) plusOne(R.string.statisticNumSingleplayerDuelsWonKey);
            else plusOne(R.string.statisticNumSingleplayerDuelsLostKey);
        }
    }


    //== READER METHODS ==

    public int gamesPlayed(boolean isMultiplayer) {
        if(isMultiplayer) {
            return read(R.string.statisticNumMultiplayerGamesPlayedKey);
        } else {
            return read(R.string.statisticNumSingleplayerGamesPlayedKey);
        }
    }

    public int gamesWon(boolean isMultiplayer) {
        if(isMultiplayer) {
            return read(R.string.statisticNumMultiplayerGamesWonKey);
        } else {
            return read(R.string.statisticNumSingleplayerGamesWonKey);
        }
    }

    public int gamesLost(boolean isMultiplayer) {
        if(isMultiplayer) {
            return read(R.string.statisticNumMultiplayerGamesLostKey);
        } else {
            return read(R.string.statisticNumSingleplayerGamesLostKey);
        }
    }

    public int duelsMade(boolean isMultiplayer) {
        if(isMultiplayer) {
            return read(R.string.statisticNumMultiplayerDuelsMadeKey);
        } else {
            return read(R.string.statisticNumSingleplayerDuelsMadeKey);
        }
    }

    public int duelsWon(boolean isMultiplayer) {
        if(isMultiplayer) {
            return read(R.string.statisticNumMultiplayerDuelsWonKey);
        } else {
            return read(R.string.statisticNumSingleplayerDuelsWonKey);
        }
    }

    public int duelsLost(boolean isMultiplayer) {
        if(isMultiplayer) {
            return read(R.string.statisticNumMultiplayerDuelsLostKey);
        } else {
            return read(R.string.statisticNumSingleplayerDuelsLostKey);
        }
    }
}
