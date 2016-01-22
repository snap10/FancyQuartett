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
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import de.uulm.mal.fancyquartett.R;

/**
 * Created by mk on 01.01.2016.
 */
public class Card implements Serializable {

    private int id;
    private int deckID;
    private int order;
    private String name = "";
    private ArrayList<CardAttribute> attributes;
    private ArrayList<Image> images;

    private String description = null;
    private String deckname;


    // constructor used by AssetsInstaller //TODO Rewrite Assetsinstaller

    /**
     * @param json
     * @param props
     * @param localDeckFolder
     * @param deckname
     * @param isLocal
     * @throws JSONException
     */
    public Card(JSONObject json, ArrayList<Property> props, String localDeckFolder, String deckname, boolean isLocal) throws JSONException {
        images = new ArrayList<Image>();
        attributes = new ArrayList<>();
        this.id = json.getInt("id");
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.deckname = deckname;
        JSONArray imgJsonArr = json.getJSONArray("images");
        JSONArray attrJsonArr = json.getJSONArray("attributes");
        for (int i = 0; i < imgJsonArr.length(); i++) {
            JSONObject imgJson = imgJsonArr.getJSONObject(i);
            images.add(new Image(imgJson, isLocal));
        }
        for (int i = 0; i < attrJsonArr.length(); i++) {
            JSONObject attrJson = attrJsonArr.getJSONObject(i);
            attributes.add(new CardAttribute(attrJson));
        }

    }

    public Card(JSONObject cardJson, boolean isLocal) throws JSONException {
        images = new ArrayList<Image>();
        attributes = new ArrayList<CardAttribute>();
        this.id = cardJson.getInt("id");
        this.deckID = cardJson.getInt("deck");
        this.order = cardJson.getInt("order");
        this.name = cardJson.getString("name");
        JSONArray imgJsonArr = cardJson.getJSONArray("images");
        JSONArray attrJsonArr = cardJson.getJSONArray("attributes");
        for (int i = 0; i < imgJsonArr.length(); i++) {
            JSONObject imgJson = imgJsonArr.getJSONObject(i);
            images.add(new Image(imgJson, isLocal));
        }
        for (int i = 0; i < attrJsonArr.length(); i++) {
            JSONObject attrJson = attrJsonArr.getJSONObject(i);
            attributes.add(new CardAttribute(attrJson));
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

    public ArrayList<CardAttribute> getAttributes() {
        return attributes;
    }


    public int getOrder() {
        return order;
    }

    public int getDeckID() {
        return deckID;

    }

    public int getPoints() {
        // TODO: calculate Points (check biggerWins!!!)
        // +-5% better than Median -> 2Points
        // > +-5% better than Median --> 1Point
        // -+5% worse than Median --> 5Points
        return 0;
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

    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param prop
     * @return
     * @throws IllegalArgumentException
     */
    public double getValue(Property prop) throws IllegalArgumentException {
        double value;
        for (CardAttribute attribute : attributes) {
            if (attribute.getId() == prop.getId()) {
                value = attribute.getValue();
                return value;
            }
        }
        throw new IllegalArgumentException("No specified value found in CardAttributeList for Property ID" + prop.getId() + "!");
    }


}
