package de.uulm.mal.fancyquartett.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mk on 01.01.2016.
 */
public class OfflineDeck extends Deck {

    private ArrayList<Card> cards;

    private ArrayList<Property> properties;

    public OfflineDeck(String name, String description, Card[] cards, Property[] properties) {
        super(name,description);
        this.cards = new ArrayList<Card>(Arrays.asList(cards));
        this.properties = new ArrayList<Property>(Arrays.asList(properties));
    }

    public OfflineDeck(File folder) throws Exception {
        super();
        File jsonFile = new File(folder, folder.getName()+".json");
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
        for(int i = 0; i < propsJson.length(); i++) {
            properties.add(new Property(propsJson.getJSONObject(i)));
        }
        for(int i = 0; i < cardsJson.length(); i++) {
            cards.add(new Card(
                    cardsJson.getJSONObject(i),
                    (Property[])properties.toArray(),
                    folder.getAbsolutePath(),
                    true
            ));
        }
    }
}
