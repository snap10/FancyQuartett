package de.uulm.mal.fancyquartett.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mk on 01.01.2016.
 */
public class OfflineDeck extends Deck implements Serializable{

    private ArrayList<Card> cards;

    private ArrayList<Property> properties;

    public OfflineDeck(String name, String description, Card[] cards, Property[] properties) {
        super(name, description);
        this.cards = new ArrayList<Card>(Arrays.asList(cards));
        this.properties = new ArrayList<Property>(Arrays.asList(properties));
    }


    public OfflineDeck(String folder, String name) throws Exception {
        super();
        this.cards = new ArrayList<Card>();
        this.properties = new ArrayList<Property>();
        File jsonFile = new File(folder, name + ".json");
        BufferedReader br = null;
        StringBuffer buffer = new StringBuffer();
        br = new BufferedReader(new FileReader(jsonFile));
        String line;
        while ((line = br.readLine()) != null) {
            buffer.append(line);
            buffer.append('\n');
        }
        br.close();
        JSONObject json = new JSONObject(buffer.toString());
        super.name = json.getString("name");
        super.description = json.getString("description");
        JSONArray cardsJson = json.getJSONArray("cards");
        JSONArray propsJson = json.getJSONArray("properties");
        for (int i = 0; i < propsJson.length(); i++) {
            properties.add(new Property(propsJson.getJSONObject(i)));
        }
        for (int i = 0; i < cardsJson.length(); i++) {
            cards.add(new Card(cardsJson.getJSONObject(i), properties,folder,name,true));
        }

    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<Property> properties) {
        this.properties = properties;
    }
}
