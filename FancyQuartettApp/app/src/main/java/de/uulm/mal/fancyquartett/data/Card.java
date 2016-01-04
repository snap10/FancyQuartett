package de.uulm.mal.fancyquartett.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by mk on 01.01.2016.
 */
public class Card {

    private int id = 0;

    private ArrayList<Image> images;

    private String description = null;

    // map properties onto card values. Use float for all value types.
    private Map<Property, Float> values;

    private String name = null;

    public Card(JSONObject json, Property[] props, String host) throws JSONException {
        this.id = json.getInt("id");
        this.name = json.getString("name");
        this.description = json.getString("description");
        JSONArray imgsJson = json.getJSONArray("images");
        for(int i = 0; i < imgsJson.length(); i++) {
            Image img = new Image(imgsJson.getJSONObject(i), host);
            img.download();
            images.add(img);
        }
        JSONArray valsJson = json.getJSONArray("values");
        for(int i = 0; i < valsJson.length(); i++) {
            JSONObject pair = valsJson.getJSONObject(i);
            int propId = pair.getInt("propertyId");
            float val = (float) pair.getDouble("value");
            for(int j = 0; j < props.length; j++) { // search property with given id for each value
                if(props[j].id()==propId) {
                    values.put(props[j], val);
                    break;
                }
            }
        }
    }

    public int id() { return id; }

    // overloaded add method
    public void add(Image image) {
        images.add(image);
    }
    public void add(Property p, float v) {
        values.put(p, v);
    }
    public void add(String description) {
        this.description = description;
    }

    // compare with other card's respective property and return true if this card wins
    public boolean compare(Property prop, float enemyValue) {
        return prop.biggerWins() ? values.get(prop)>enemyValue : values.get(prop)<enemyValue;
    }
}
