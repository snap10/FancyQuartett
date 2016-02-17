package de.uulm.mal.fancyquartett.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mk on 01.01.2016.
 */
public class OfflineDeck extends Deck implements Serializable {

    private ArrayList<Card> cards;

    /**
     * @param name
     * @param description
     * @param cards
     * @param properties
     */
    public OfflineDeck(String name, String description, ArrayList<Card> cards, ArrayList<Property> properties) {
        super(name, description);
        this.cards = cards;

    }

    /**
     * Used by LocalDecksLoader and LocalDeckLoader //TODO Rewrite for new Json specification
     *
     * @param folder
     * @param filename
     * @throws Exception
     */
    public OfflineDeck(String folder, String filename,boolean isLocal) throws Exception {
        super();
        this.cards = new ArrayList<Card>();

        File jsonFile = new File(folder, filename + ".json");
        BufferedReader br = null;
        StringBuffer buffer = new StringBuffer();
        br = new BufferedReader(new FileReader(jsonFile));
        String line;
        while ((line = br.readLine()) != null) {
            buffer.append(line);
            buffer.append('\n');
        }
        br.close();
        JSONObject deckjson = new JSONObject(buffer.toString());

        this.id = deckjson.getInt("id");
        this.name = deckjson.getString("name");
        this.description = deckjson.getString("description");
        this.deckimage = new Image(deckjson.getString("image"), true);
        this.misc = deckjson.getString("misc");
        this.misc_version = deckjson.getString("misc_version");
        this.cards = new ArrayList<Card>();

        JSONArray cardsJson = deckjson.getJSONArray("cards");
        for (int i = 0; i < cardsJson.length(); i++) {
            JSONObject card = cardsJson.getJSONObject(i);
            cards.add(new Card(card, isLocal));
        }

    }

    /**
     * Used by Deckdownloader to Download Decks and return OfflineDeck
     *
     * @param deckjson
     * @param isLocal  important to have the Images of the Cards downloaded if isLocal==false;
     * @throws JSONException
     */
    public OfflineDeck(JSONObject deckjson, boolean isLocal) throws JSONException {
        super(deckjson.getInt("id"), deckjson.getString("name"), deckjson.getString("description"), new Image(deckjson.getString("image"), true), deckjson.getString("misc"), deckjson.getString("misc_version"));
        this.cards = new ArrayList<Card>();
        
        JSONArray cardsJson = deckjson.getJSONArray("cards");
        for (int i = 0; i < cardsJson.length(); i++) {
            JSONObject card = cardsJson.getJSONObject(i);
            cards.add(new Card(card, isLocal));
        }

    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }


    /**
     * Should delete all offlineFiles of deck including JSON and Picture data
     *
     * @return
     */
    public void removeFromFileSystem(Context context) {
        File f = new File(context.getFilesDir()+Settings.localFolder+"/"+id);
        deleteRecursive(f);
    }

    /**
     *
     * @param dirOrFile
     */
    private void deleteRecursive(File dirOrFile) {

        if (dirOrFile.isDirectory())
            for (File child : dirOrFile.listFiles())
                deleteRecursive(child);

        dirOrFile.delete();

    }
}
