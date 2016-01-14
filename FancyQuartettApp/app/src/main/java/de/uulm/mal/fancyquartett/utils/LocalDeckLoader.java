package de.uulm.mal.fancyquartett.utils;

/**
 * Created by Snap10 on 07/01/16.
 */

import android.content.Context;
import android.os.AsyncTask;
import de.uulm.mal.fancyquartett.data.OfflineDeck;



/**
 * Created by Snap10 on 05/01/16.
 *
 * @author snap10
 *         This Loader is responsible for collecting all available deckdata on local storage. Th Task is conducted in the background and first gets all available Jsons.
 *         When collected Jsons, a new OfflineDeck is created for every JSON.
 */
public class LocalDeckLoader extends AsyncTask<Void, Void, OfflineDeck> {
    private int deckid;
    String path;

    OnLocalDeckLoadedListener listener;

    /**
     *
     * @param path
     * @param deckid
     */
    public LocalDeckLoader(String path, int deckid) {
        this.path = path;
        this.deckid=deckid;
    }

    /**
     *
     * @param path
     * @param deckid
     * @param listener
     */
    public LocalDeckLoader(String path, int deckid, OnLocalDeckLoadedListener listener) {
        this.path = path;
        this.deckid =deckid;
        this.listener = listener;
    }

    public interface OnLocalDeckLoadedListener {
        /**
         * Callback Method for LocalDeckLoader
         * @param offlineDeck
         */
        public void onDeckLoaded(OfflineDeck offlineDeck);
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param offlineDeck The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(OfflineDeck offlineDeck) {
        listener.onDeckLoaded(offlineDeck);
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected OfflineDeck doInBackground(Void... params) {

        OfflineDeck offlineDeck = null;
        String deckPath = path + "/" + deckid;
        try {
            offlineDeck = new OfflineDeck(deckPath, deckid+"",true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return offlineDeck;
    }


}



