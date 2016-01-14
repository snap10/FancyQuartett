package de.uulm.mal.fancyquartett.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mk on 01.01.2016.
 */
public class Property implements Serializable{


    private int precision;
    private double median;
    private int id;
    private int cardID;
    private String attributeName;
    private String unit;
    private int order;
    private Image attributeImage;
    private boolean higherWins;

    public Property(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.attributeName = json.getString("text");
        this.unit = json.getString("unit");
        this.higherWins = (json.getInt("compare")==1);
        this.precision = json.getInt("precision");
        if (json.has("median")){
            this.median = json.getDouble("median");
        }
    }

    public Property(boolean higherWins, Image attributeImage, int order, String unit, String attributeName, int cardID, int id, double median) {
        this.higherWins = higherWins;
        this.attributeImage = attributeImage;
        this.order = order;
        this.unit = unit;
        this.attributeName = attributeName;
        this.cardID = cardID;
        this.id = id;
        this.median = median;
    }

    public int id() { return id; }

    public boolean biggerWins() { return higherWins; }

    public String getAttributeName() { return attributeName; }

    public String getUnit() {
        return unit;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }
}
