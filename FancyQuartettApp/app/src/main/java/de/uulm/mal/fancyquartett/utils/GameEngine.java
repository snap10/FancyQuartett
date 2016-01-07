package de.uulm.mal.fancyquartett.utils;

import android.content.Context;
import android.widget.LinearLayout;

import java.security.Timestamp;

import de.uulm.mal.fancyquartett.data.Deck;
import de.uulm.mal.fancyquartett.enums.GameMode;

/**
 * Created by Lukas on 07.01.2016.
 */
public class GameEngine {

    // app attributes
    private Context mContext;

    // game attributes
    GameMode gameMode;

    int maxRounds = 0;
    int maxPoints = 0;
    int timeout = 0;
    // TODO: Player player1, player2;
    Deck deck;
    Timestamp startTime, lastPlayed, endTime;
    //Boolean isMultiplayer = false;

    public GameEngine(Context context) {
        this.mContext = context;
    }

}


