package de.uulm.mal.fancyquartett.data;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.uulm.mal.fancyquartett.adapters.GalleryViewAdapter;
import de.uulm.mal.fancyquartett.utils.JsonDownloader;
import layout.GalleryFragment;

/**
 * Created by mk on 02.01.2016.
 */
public class GalleryModel implements JsonDownloader.OnJasonDownloaderFinished {

    private GalleryViewAdapter adapter;
    private ArrayList<OnlineDeck> onlineDecks;
    private ArrayList<OfflineDeck> offlineDecks;

    // host of online decks (including path to deck folder without trailing /)
    private String onlineDeckHost;

    // local deck folder
    private String offlineDeckFolder;

    /**
     * Dummy Constructor for empty galleryModel to prevent NullPointerExceptions.
     */
    public GalleryModel() {
        onlineDecks = new ArrayList<>();
        offlineDecks = new ArrayList<>();
    }

    public GalleryModel(GalleryViewAdapter adapter, String onlineDeckHost, String offlineDeckFolder) {
        onlineDecks = new ArrayList<>();
        offlineDecks = new ArrayList<>();
        this.adapter = adapter;
        this.onlineDeckHost = onlineDeckHost;
        this.offlineDeckFolder = offlineDeckFolder;
        //TODO check if needed when LocalDecksLoader is used scanOfflineDecks();
    }

    //Override GalleryModel Constructor for use without View
    //TODO remove after Testing
    public GalleryModel(String onlineDeckHost, String offlineDeckFolder) {
        onlineDecks = new ArrayList<>();
        offlineDecks = new ArrayList<>();
        this.onlineDeckHost = onlineDeckHost;
        this.offlineDeckFolder = offlineDeckFolder;
    }


    /**
     * @param offlineDeckList
     */
    public GalleryModel(ArrayList<OfflineDeck> offlineDeckList) {
        offlineDecks = offlineDeckList;
        onlineDecks = new ArrayList<>();
    }

    public GalleryViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(GalleryViewAdapter adapter) {
        this.adapter = adapter;
    }


    // searches for already downloaded decks in local storage and creates OfflineDecks

    /**
     * //TODO check if needed when LocalDecksLoader is used
     * @throws Exception
     */
    public void scanOfflineDecks() throws Exception {
        File decksDirectory = new File(adapter.getContext().getFilesDir() + Settings.localFolder);
        String[] decks = decksDirectory.list();
        if (decks.length > 0) {
            for (int i = 0; i < decks.length; i++) {
                String deckPath = decksDirectory + "/" + decks[i];
                try {
                    OfflineDeck offlineDeck = new OfflineDeck(deckPath, decks[i]);
                    offlineDecks.add(offlineDeck);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new Exception("No Local Decks found");
        }
    }

    //Method to provide application Context From Adapter
    public Context getContext() {
        return adapter.getContext();
    }


    /**
     * Overload of fetch Online Deck for Testing
     * //TODO possibly remove after Testing if not needed
     *
     * @param host
     * @param deckname
     */
    public void fetchOnlineDeck(String host, String deckname) {
        this.onlineDeckHost = host;
        new JsonDownloader("http://" + onlineDeckHost + "/" + deckname + "/" + deckname + ".json",this).execute();
    }

    private void add(OnlineDeck d) {
        onlineDecks.add(d);
        adapter.notifyDataSetChanged();
    }

    private void add(OfflineDeck d) {
        offlineDecks.add(d);
        adapter.notifyDataSetChanged();
    }

    public void move(OnlineDeck onlineDeck, OfflineDeck offlineDeck) {
        onlineDecks.remove(onlineDeck);
        offlineDecks.add(offlineDeck);
        adapter.notifyDataSetChanged();
    }

    /**
     * @return the Amount of Decks in the Model. (onlinedecks+offlinedecks)
     */
    public int getSize() {
        return onlineDecks.size() + offlineDecks.size();
    }

    public void addTestDecks() {
        for (int i = 0; i < 20; i++) {
            onlineDecks.add(new OnlineDeck("Testname" + i, "Testdescription" + i));
            //TODO notify that dataset has changed
        }

    }

    /**
     * Retruns a Deck Object.
     *
     * @param i
     * @return deck
     */
    public Deck getDeck(int i) {
        if (i < offlineDecks.size()) {
            return offlineDecks.get(i);
        } else if (i < getSize()) {
            return onlineDecks.get(i - offlineDecks.size());
        } else {
            return null;
        }
    }

    /**
     * returns null if index is out of Bounds. This means that there are not that many offline Decks available. Possibly getDeck works
     *
     * @param i
     * @return
     */
    public OfflineDeck getOfflineDeck(int i) {
        if (i < offlineDecks.size()) {
            return offlineDecks.get(i);
        } else {
            return null;
        }

    }

    /**
     * Callback Method for JsonDownloader, called when finished or exception is thrown.
     * Possible Exception is deliverd as parameter. Equals null if no exception was thrown
     *
     * @param possibleException
     * @param json The JsonString
     */
    @Override
    public void onDownloadFinished(Exception possibleException, String json) {
        if (possibleException==null) {
            try {
                add(new OnlineDeck(new JSONObject(json), onlineDeckHost, this));
            } catch (JSONException e) {
                System.out.println("Deck could not be added because downloaded JSON is invalid. " + e);
            }
        } else{
            Toast.makeText(getContext(),"No Connection to ServerRessource"+ possibleException.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
