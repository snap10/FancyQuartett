package de.uulm.mal.fancyquartett.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mk on 01.01.2016.
 */
public class Card {

    private int id = 0;

    private ArrayList<Image> images;

    private String description = null;
    private String deckname;

    // map properties onto card values. Use float for all value types.
    private Map<Property, Float> values;

    private String name = null;


    // constructor used by OnlineDecks when downloading
    public Card(JSONObject json, Property[] props, String pathToOnlinePictures,String localFolder,String deckname,  boolean isLocal) throws JSONException {
        images = new ArrayList<Image>();
        values= new HashMap<>();
        this.id = json.getInt("id");
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.deckname=deckname;
        JSONArray imgsJson = json.getJSONArray("images");
        for(int i = 0; i < imgsJson.length(); i++) {
            Image img = new Image(imgsJson.getJSONObject(i), pathToOnlinePictures,localFolder,deckname, isLocal);
            if(!isLocal) img.download();
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

    // constructor used by OfflineDecks
    public Card(JSONObject json, ArrayList<Property> props, String localFolder,String deckname,  boolean isLocal) throws JSONException {
        images = new ArrayList<Image>();
        values= new HashMap<>();
        this.id = json.getInt("id");
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.deckname=deckname;
        JSONArray imgsJson = json.getJSONArray("images");
        for(int i = 0; i < imgsJson.length(); i++) {
            Image img = new Image(imgsJson.getJSONObject(i), localFolder,deckname, isLocal);
            if(!isLocal) img.download();
            images.add(img);
        }
        JSONArray valsJson = json.getJSONArray("values");
        for(int i = 0; i < valsJson.length(); i++) {
            JSONObject pair = valsJson.getJSONObject(i);
            int propId = pair.getInt("propertyId");
            float val = (float) pair.getDouble("value");
            for (Property prop:props){
                if(prop.id()==propId) {
                    values.put(prop, val);
                    break;
                }
            }

        }
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public int id() { return id; }

    // overloaded add method
    public void add(Image image) {
        images.add(image);
    }
    public void add(String description) {
        this.description = description;
    }

    // compare with other card's respective property and return true if this card wins
    public boolean compare(Property prop, float enemyValue) {
        return prop.biggerWins() ? values.get(prop)>enemyValue : values.get(prop)<enemyValue;
    }

    /**
     * This function converts Map<Property,Float> into List<CardAttribute>
     * @param values
     * @return
     */
    private List<CardAttribute> makeAttrList(Map<Property, Float> values) {
        List<CardAttribute> list = new ArrayList<CardAttribute>();
        for(Property property : values.keySet()) {
            float value = values.get(property);
            list.add(new CardAttribute(property, value));
        }
        return list;
    }

    /**
     *
     * @return
     */
    public CharSequence getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getDescription(){
        return description;
    }
}
