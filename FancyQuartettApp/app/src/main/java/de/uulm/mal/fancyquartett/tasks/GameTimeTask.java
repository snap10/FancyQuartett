package de.uulm.mal.fancyquartett.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;

import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.utils.GameEngine;

/**
 * Created by Lukas on 19.01.2016.
 */
public class GameTimeTask extends AsyncTask<Void, Long, Void> {

    private final long SECOND = 1000;
    private final long MINUTE = 60000;

    private long breakpoint = 0;

    private GameEngine engine;

    public GameTimeTask(GameEngine engine) {
        this.engine = engine;
        if(engine.getCurTime() >= (engine.getGameTime() - MINUTE)) {
            breakpoint = 1000;
        } else {
            breakpoint = 60000;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        long progressStatus = engine.getCurTime();
        long counter = 0;
        // start counting
        while(progressStatus < engine.getGameTime()) {
            // check if cancelled
            if(isCancelled()) break;
            // increase counter
            counter += 1000;
            System.out.println("COUNTER: " + counter);
            // check if breakpoint is reached
            if(counter == breakpoint) {
                progressStatus += breakpoint;
                publishProgress(progressStatus);
                counter = 0;
            }
            try {
                Thread.sleep(SECOND);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        System.out.println("UPDATE: " + values[0]);
        engine.onGameTimeUpdate(values[0]);
    }
}
