package de.uulm.mal.fancyquartett.data;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import de.uulm.mal.fancyquartett.adapters.GalleryViewAdapter;
import de.uulm.mal.fancyquartett.utils.OnlineDeckLoader;

/**
 * Created by mk on 02.01.2016.
 */
public class GalleryModel implements OnlineDeckLoader.OnOnlineDeckLoaded {

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




    //Method to provide application Context From Adapter
    public Context getContext() {
        return adapter.getContext();
    }

    /**
     * @return the Amount of Decks in the Model. (onlinedecks+offlinedecks)
     */
    public int getSize() {
        return onlineDecks.size() + offlineDecks.size();
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
     * Retruns a Deck Object.
     *
     * @param i
     * @return deck
     */
    public OnlineDeck getOnlineDeck(int i) {
        if (i < offlineDecks.size()) {
            return onlineDecks.get(i);
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

    //TODO Check the efficiency of those Methods regarding the new DataModell
    public void addOfflineDecks(ArrayList<OfflineDeck> offlineDecks) {
        if (this.offlineDecks.size() == 0) {
            this.offlineDecks = offlineDecks;
            for (OfflineDeck offlineDeck : offlineDecks) {
                if (onlineDecks.contains(offlineDeck)) onlineDecks.remove(offlineDeck);
            }
        } else {
            for (OfflineDeck oD : offlineDecks) {
                if (!this.offlineDecks.contains(oD)) {
                    this.offlineDecks.add(oD);
                    if (onlineDecks.contains(oD)) onlineDecks.remove(oD);
                }
            }
        }
    }

    public void addOfflineDeck(OfflineDeck offlineDeck) {

        if (!this.offlineDecks.contains(offlineDeck)) {
            this.offlineDecks.add(offlineDeck);
            if (onlineDecks.contains(offlineDeck)) onlineDecks.remove(offlineDeck);
        }
    }

    public void addOnlineDecks(ArrayList<OnlineDeck> onlineDecks) {
        for (OnlineDeck oD : onlineDecks) {
            if (!this.onlineDecks.contains(oD) && !this.offlineDecks.contains(oD)) {
                this.onlineDecks.add(oD);
            }
        }
    }


    public void addOnlineDeck(OnlineDeck onlineDeck) {
        if (!this.onlineDecks.contains(onlineDeck) && !this.offlineDecks.contains(onlineDeck)) {
            this.onlineDecks.add(onlineDeck);
        }
    }

    /**
     * Callback Method for DeckDownloader, called when finished or exception is thrown.
     * Possible Exception is deliverd as parameter. Equals null if no exception was thrown
     *
     * @param possibleException
     * @param onlineDeck
     */
    @Override
    public void onDownloadFinished(Exception possibleException, OnlineDeck onlineDeck) {
        if (possibleException == null) {
            addOnlineDeck(onlineDeck);
        } else {
            Toast.makeText(getContext(), "No Connection to ServerRessource" + possibleException.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}


