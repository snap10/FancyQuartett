package de.uulm.mal.fancyquartett.data;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import layout.GalleryFragment;

/**
 * Created by mk on 02.01.2016.
 */
public class GalleryModel {

    private GalleryFragment view;
    private ArrayList<OnlineDeck> onlineDecks;
    private ArrayList<OfflineDeck> offlineDecks;

    // host of online decks (including path to deck folder without trailing /)
    private String onlineDeckHost;

    // local deck folder
    private String offlineDeckFolder;



    public GalleryModel(GalleryFragment view, String onlineDeckHost, String offlineDeckFolder) throws Exception {
        this.view = view;
        this.onlineDeckHost = onlineDeckHost;
        this.offlineDeckFolder = offlineDeckFolder;
        scanOfflineDecks();
    }

    //Override GalleryModel Constructor for use without View
    //TODO remove after Testing
    public GalleryModel(String onlineDeckHost, String offlineDeckFolder) {
        onlineDecks= new ArrayList<>();
        offlineDecks= new ArrayList<>();
        this.onlineDeckHost=onlineDeckHost;
        this.offlineDeckFolder= offlineDeckFolder;
    }

    // searches for already downloaded decks in local storage and creates OfflineDecks
    public void scanOfflineDecks() throws Exception {
        File deckFolder = new File(offlineDeckFolder);
        File[] decks = deckFolder.listFiles();
        if(decks != null) {
            for(int i = 0; i < decks.length; i++) {
                add(new OfflineDeck(decks[i]));
            }
        } else {
            throw new Exception("Path to local deck folder is invalid!");
        }
    }

    // initiates the download of a json file and the creation of an OnlineDeck
    public void fetchOnlineDeck(String deckname) {
        new JsonDownloader("http://"+onlineDeckHost+"/"+deckname+".json").execute();
    }

    private void add(OnlineDeck d) {
        onlineDecks.add(d);
        //TODO notify view to show the new deck
    }
    private void add(OfflineDeck d) {
        offlineDecks.add(d);
        //TODO notify view
    }
    public void move(OnlineDeck onlineDeck, OfflineDeck offlineDeck) {
        onlineDecks.remove(onlineDeck);
        offlineDecks.add(offlineDeck);
        //TODO notify view
    }

    protected void downloadCallback(String json) {
        try {
            add(new OnlineDeck(new JSONObject(json), onlineDeckHost, this));
        } catch (JSONException e) {
            System.out.println("Deck could not be added because downloaded JSON is invalid. "+e);
        }
    }

    /**
     *
     * @return the Amount of Decks in the Model. (onlinedecks+offlinedecks)
     */
    public int getSize(){
        return onlineDecks.size()+offlineDecks.size();
    }

    public void addTestDecks(){
        for (int i = 0; i < 20; i++) {
            onlineDecks.add(new OnlineDeck("Testname" + i, "Testdescription" + i));
            //TODO notify that dataset has changed
        }

    }




    private class JsonDownloader extends AsyncTask<Void, Void, Void> {

        private String url;
        private String json;

        public JsonDownloader(String url) {
            super();
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... v) {
            try {
                URL u = new URL(url);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                    response.append("\n");
                }
                in.close();
                json = response.toString();
            } catch (IOException e) {System.out.println(e);}
            return null;
        }

        protected void onPostExecute(Void v) {
            downloadCallback(json);
        }
    }
}
