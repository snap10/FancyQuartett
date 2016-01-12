package de.uulm.mal.fancyquartett.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by mk on 01.01.2016.
 */
public class Property implements Serializable{

    // id maps properties to card values
    private int id = 0;

    // name to be displayed to the user
    private String text = null;

    // true = bigger values win, false = smaller values win
    private boolean biggerWins = true;

    // unit string displayed after numeric value
    private String unit = null;

    // (?)
    private int precision = 0;

    // TODO:
    //private Bitmap icon;

    public Property(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.text = json.getString("text");
        this.unit = json.getString("unit");
        this.biggerWins = (json.getInt("compare")==1);
        this.precision = json.getInt("precision");
    }

    public Property(String text, boolean biggerWins, int id, String unit, int precision) {
        this.text = text;
        this.biggerWins = biggerWins;
        this.id = id;
        this.unit = unit;
        this.precision = precision;
    }

    public int id() { return id; }

    public boolean biggerWins() { return biggerWins; }

    public String getText() { return text; }
}
