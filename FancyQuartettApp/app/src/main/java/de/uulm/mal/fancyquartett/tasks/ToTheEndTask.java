package de.uulm.mal.fancyquartett.tasks;

import android.content.Context;
import android.os.AsyncTask;

import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.adapters.CardAttrViewAdapter;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.Property;

/**
 * Created by Lukas on 13.01.2016.
 */
public class ToTheEndTask extends AsyncTask implements CardAttrViewAdapter.OnCardAttrClickListener{

    private final long TASK_REPEAT_DURATION = 1000;

    private boolean isRunning = false;
    private boolean playerSelectedCartAttribute= false;

    private Property currentProperty;

    private GameActivity.GameEngine engine;
    private Context context;

    public ToTheEndTask(Context context, GameActivity.GameEngine engine) {
        this.context = context;
        this.engine = engine;
    }

    @Override
    protected synchronized Object doInBackground(Object[] params) {
        while(!isRunning) {
            // wait some fancy time
            try {

                wait(TASK_REPEAT_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // check curPlayer
            if( true /*engine.getCurPlayer() == engine.PLAYER1*/) {
                if(playerSelectedCartAttribute)  {
                    // lock if statement
                    playerSelectedCartAttribute = false;
                    // compare cards an give them to winning player
                    int playerWonRound = engine.compareCardsProperty(currentProperty);
                    System.out.println("Player " + playerWonRound + " wins current round!");
                    engine.handlePlayerCards(playerWonRound);
                    // check if player won
                    int playerWonGame = engine.checkPlayerWon();
                    if(playerWonGame != -1) {
                        isRunning = true;
                        System.out.println("Player" + playerWonGame + " wins game!");
                        // TODO: display won dialog
                    } else {
                        // show next card
                        engine.showPlayer1NextCard();
                    }
                }
            } else {
                // TODO: Ai stuff
            }
            // change curPlayer
            // engine.changeCurrentPlayer();
        }
        return null;
    }

    @Override
    public void onCardAttrClicked(Property property, double value, CardAttribute attribute) {
        currentProperty = property;
        playerSelectedCartAttribute = true;
    }
}
