package de.uulm.mal.fancyquartett.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    AssetManager manager;
    OnTaskCompleted listener;
    Context context;


    public LocalDecksLoader(String path, Context context) throws IOException {
        this.path = path;
        this.context=context.getApplicationContext();
        manager = context.getAssets();
        deckFolders = manager.list(path);


    }

    public LocalDecksLoader(String path, Context context, OnTaskCompleted listener) throws IOException {
        this.path = path;
        this.context=context.getApplicationContext();
        manager = context.getAssets();
        deckFolders = manager.list(path);
        this.listener=listener;
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
        String[] jsonStringArray = new String[deckFolders.length];
        for (int i = 0; i < deckFolders.length; i++) {
            String deckPath = deckFolders[i];
            StringBuffer response;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(manager.open(path + "/"+ deckPath + "/" + deckPath + ".json")));
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                    response.append("\n");
                }
                //DONE READING
                in.close();
                jsonStringArray[i] = response.toString();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
        for (int i = 0; i < jsonStringArray.length; i++) {
            JSONObject deckJson = null;
            try {
                deckJson = new JSONObject(jsonStringArray[i]);
                JSONArray cardsJson = deckJson.getJSONArray("cards");
                JSONArray propsJson = deckJson.getJSONArray("properties");
                Property[] props = new Property[propsJson.length()];
                for (int j = 0; j < props.length; j++) {
                    props[j] = new Property(propsJson.getJSONObject(j));
                }
                Card[] cards = new Card[cardsJson.length()];
                for (int k = 0; k < cards.length; k++) {
                    // creating card objects initiates image downloads
                    cards[k] = new Card(cardsJson.getJSONObject(k), props, context.getFilesDir()+Settings.localFolder,deckFolders[i],false);
                    for (Image image: cards[k].getImages()) {
                        //TODO
                        //TODO image.saveBitmap(BitmapFactory.decodeFile(manager.(path + "/"+ deckFolders[i] + "/" + image.getFileName())));

                    }
                }

                offlineDecks.add(new OfflineDeck(deckJson.getString("name"), deckJson.getString("description"), cards, props))
                ;
            } catch (JSONException e) {
                System.out.println("Deck could not be added because downloaded JSON is invalid. " + e);
            }
        }
        return offlineDecks;
    }

}



