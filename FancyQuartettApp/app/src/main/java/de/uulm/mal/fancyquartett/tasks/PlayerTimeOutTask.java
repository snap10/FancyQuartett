package de.uulm.mal.fancyquartett.tasks;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import java.util.ArrayList;

import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.utils.GameEngine;

/**
 * Created by Lukas on 18.01.2016.
 */
public class PlayerTimeOutTask extends AsyncTask<Void, Integer, Void> {

    private GameActivity listener;
    private GameEngine engine;
    private ProgressBar progressBar;

    public PlayerTimeOutTask(GameActivity listener, GameEngine engine, ProgressBar progressBar){
        this.listener = listener;
        this.engine = engine;
        this.progressBar = progressBar;
    }

    @Override
    protected Void doInBackground(Void... params) {
        int progressStatus = engine.getTimeout();
        while(progressStatus > 0) {
            if(isCancelled()) break;
            progressStatus = progressStatus - 500;
            publishProgress(progressStatus);
            try {
                Thread.sleep(500);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!isCancelled()) {
            // time over, select random attribute
            Player player = engine.getPlayer(engine.getCurPlayer());
            ArrayList<CardAttribute> attrList = player.getCurrentCard().getAttributes();
            int random = (int)((Math.random()) * (attrList.size()-1) + 0);
            CardAttribute attr = attrList.get(random);
            listener.onCardFragmentAttributeInteraction(attr.getProperty(), attr.getValue(), attr);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // display progress
        progressBar.setProgress(values[0]);
        progressBar.invalidate();
    }
}
