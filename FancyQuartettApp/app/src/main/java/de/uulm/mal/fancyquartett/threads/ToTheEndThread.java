package de.uulm.mal.fancyquartett.threads;

import de.uulm.mal.fancyquartett.activities.GameActivity;

/**
 * Created by Lukas on 12.01.2016.
 */
public class ToTheEndThread implements Runnable {

    private final int DELAY = 1000; // 1s = 1000

    private Thread thread = null;
    private boolean isRunning = false;
    private GameActivity.GameEngine engine;

    public ToTheEndThread(GameActivity.GameEngine engine){
        this.engine = engine;
    }

    public synchronized void start() {
        if(thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public synchronized void run() {
        while(!isRunning) {
            try {
                thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("test-thread-durchlauf-jede-sekunde");
        }
    }

    public synchronized void stop(){
        thread.interrupt();
    }
}

    
