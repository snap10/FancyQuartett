package de.uulm.mal.fancyquartett.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mk on 01.01.2016.
 */
public class Card implements Serializable{

    private int id = 0;

    private ArrayList<Image> images;

    private String description = null;
    private String deckname;

    // map properties onto card values. Use float for all value types.
    private HashMap<Property, Float> values;

    private String name = null;


    // constructor used by OnlineDecks when downloading

    /**
     *
     * @param json
     * @param props
     * @param hostadress
     * @param localFolder
     * @param deckname
     * @param isLocal
     * @throws JSONException
     */
    public Card(JSONObject json, ArrayList<Property> props, String hostadress, String localFolder, String deckname, boolean isLocal) throws JSONException {
        images = new ArrayList<Image>();
        values = new HashMap<>();
        this.id = json.getInt("id");
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.deckname = deckname;
        JSONArray imgsJson = json.getJSONArray("images");
        for (int i = 0; i < imgsJson.length(); i++) {
            Image img = new Image(imgsJson.getJSONObject(i), hostadress, localFolder, deckname, isLocal);
            if (!isLocal) img.download(false);
            images.add(img);
        }
        JSONArray valsJson = json.getJSONArray("values");
        for (int i = 0; i < valsJson.length(); i++) {
            JSONObject pair = valsJson.getJSONObject(i);
            int propId = pair.getInt("propertyId");
            float val = (float) pair.getDouble("value");
            for (int j = 0; j < props.size(); j++) {
            // search property with given id for each value
                if (props.get(j).id() == propId) {
                    values.put(props.get(j), val);
                    break;
                }
            }
        }
    }

    // constructor used by OfflineDecks

    /**
     *
     * @param json
     * @param props
     * @param localDeckFolder
     * @param deckname
     * @param isLocal
     * @throws JSONException
     */
    public Card(JSONObject json, ArrayList<Property> props, String localDeckFolder, String deckname, boolean isLocal) throws JSONException {
        images = new ArrayList<Image>();
        values = new HashMap<>();
        this.id = json.getInt("id");
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.deckname = deckname;
        JSONArray imgsJson = json.getJSONArray("images");
        for (int i = 0; i < imgsJson.length(); i++) {
            Image img = new Image(imgsJson.getJSONObject(i), localDeckFolder, deckname, isLocal);
            if (!isLocal) img.download(false);
            images.add(img);
        }
        JSONArray valsJson = json.getJSONArray("values");
        for (int i = 0; i < valsJson.length(); i++) {
            JSONObject pair = valsJson.getJSONObject(i);
            int propId = pair.getInt("propertyId");
            float val = (float) pair.getDouble("value");
            for (Property prop : props) {
                if (prop.id() == propId) {
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

    public int id() {
        return id;
    }

    // overloaded add method
    public void add(Image image) {
        images.add(image);
    }

    public void add(String description) {
        this.description = description;
    }

    // compare with other card's respective property and return true if this card wins
    public boolean compare(Property prop, float enemyValue) {
        return prop.biggerWins() ? values.get(prop) > enemyValue : values.get(prop) < enemyValue;
    }

    /**
     * This function converts Map<Property,Float> into List<CardAttribute>
     *
     * @param values
     * @return
     */
    private ArrayList<CardAttribute> makeAttrList(Map<Property, Float> values) {
        ArrayList<CardAttribute> list = new ArrayList<CardAttribute>();
        for (Property property : values.keySet()) {
            float value = values.get(property);
            list.add(new CardAttribute(property, value));
            //Sort based on PropertyID
            Collections.sort(list, new Comparator<CardAttribute>() {
                        @Override
                        public int compare(CardAttribute lhs, CardAttribute rhs) {
                            if (lhs.getProperty().id() < rhs.getProperty().id()) {
                                return -1;
                            } else if (lhs.getProperty().id() > rhs.getProperty().id()) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
            );
        }
        return list;
    }

    public ArrayList<CardAttribute> getAttributeList() {
        return makeAttrList(values);
    }

    /**
     * @return
     */

    public CharSequence getName() {
        return name;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    public int getID() {
        return id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeckname() {
        return deckname;
    }

    public void setDeckname(String deckname) {
        this.deckname = deckname;
    }

    public HashMap<Property, Float> getValues() {
        return values;
    }

    public void setValues(HashMap<Property, Float> values) {
        this.values = values;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue(Property prop) {
        return values.get(prop);
    }
}
