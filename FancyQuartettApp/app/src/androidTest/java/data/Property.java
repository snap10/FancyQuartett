package data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mk on 01.01.2016.
 */
public class Property {

    // id maps properties to card values
    private int id = 0;

    // name to be displayed to the user
    private String text = null;

    // true = bigger values win, false = smaller values win
    private boolean compare = true;

    // unit string displayed after numeric value
    private String unit = null;

    // (?)
    private int precision = 0;

    public Property(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.text = json.getString("text");
        this.unit = json.getString("unit");
        this.compare = (json.getInt("compare")==1);
        this.precision = json.getInt("precision");
    }

    public Property(String text, boolean compare, int id, String unit, int precision) {
        this.text = text;
        this.compare = compare;
        this.id = id;
        this.unit = unit;
        this.precision = precision;
    }

    public int id() { return id; }

    public boolean biggerWins() { return compare; }
}
