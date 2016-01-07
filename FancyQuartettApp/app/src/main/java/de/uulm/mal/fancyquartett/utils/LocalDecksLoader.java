package de.uulm.mal.fancyquartett.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import de.uulm.mal.fancyquartett.adapters.GalleryViewAdapter;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.Image;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.data.Settings;
import layout.GalleryFragment;


/**
 * Created by Snap10 on 05/01/16.
 *
 * @author snap10
 *         This Loader is responsible for collecting all available deckdata on local storage. Th Task is conducted in the background and first gets all available Jsons.
 *         When collected Jsons, a new OfflineDeck is created for every JSON.
 */
public class LocalDecksLoader extends AsyncTask<Void, Void, ArrayList<OfflineDeck>> {
    String path;
    String[] deckFolders;
    OnTaskCompleted listener;
    Context context;


    public LocalDecksLoader(String path, Context context){
        this.path = path;
        this.context = context.getApplicationContext();
    }


    public LocalDecksLoader(String path, Context context, OnTaskCompleted listener) {
        this.path = path;
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    public interface OnTaskCompleted {
        public void onTaskCompleted(Object object);
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param offlineDecks The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(ArrayList<OfflineDeck> offlineDecks) {
        listener.onTaskCompleted(offlineDecks);
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
    protected ArrayList<OfflineDeck> doInBackground(Void... params) {

        ArrayList<OfflineDeck> offlineDecks = new ArrayList<>();
        File decksDirectory = new File(context.getFilesDir()+Settings.localFolder);
        String[] decks= decksDirectory.list();

        for (int i = 0; i < decks.length; i++) {
            String deckPath = decksDirectory+"/"+decks[i];
            try {
                OfflineDeck offlineDeck = new OfflineDeck(deckPath,decks[i]);
                offlineDecks.add(offlineDeck);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
        return offlineDecks;
    }


}



