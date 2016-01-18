package de.uulm.mal.fancyquartett.tasks;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import de.uulm.mal.fancyquartett.activities.GameActivity;

/**
 * Created by Lukas on 18.01.2016.
 */
public class TimeOutTask extends AsyncTask<Void, Integer, Void> {

    private GameActivity.GameEngine engine;
    private ProgressBar progressBar;

    public TimeOutTask(GameActivity.GameEngine engine, ProgressBar progressBar){
        this.engine = engine;
        this.progressBar = progressBar;
    }

    @Override
    protected Void doInBackground(Void... params) {
        int progressStatus;
        int playerTimeout  = engine.getTimeout();
        if(engine.getCurPlayer() == engine.PLAYER1){
            progressStatus = playerTimeout;
        } else {
            progressStatus = 5000;
        }
        while(progressStatus > 0) {
            progressStatus = progressStatus - 500;
            publishProgress(progressStatus);
            try {
                Thread.sleep(500);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        System.out.println(values[0]);
        // display progress
        progressBar.setProgress(values[0]);
        progressBar.invalidate();
    }
}
