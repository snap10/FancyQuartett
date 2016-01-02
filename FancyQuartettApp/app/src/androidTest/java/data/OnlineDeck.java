package data;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mk on 02.01.2016.
 */
public class OnlineDeck extends Deck {

    private JSONObject deckJson;
    private String host;

    public OnlineDeck(JSONObject json, String host) throws JSONException {
        deckJson = json;
        super.name = deckJson.getString("name");
        super.description = deckJson.getString("description");
        this.host = host;
    }

    // creates a full offline deck. includes download of images.
    public OfflineDeck download() throws JSONException {
        JSONArray cardsJson = deckJson.getJSONArray("cards");
        JSONArray propsJson = deckJson.getJSONArray("properties");
        Property[] props = new Property[propsJson.length()];
        for(int i = 0; i < props.length; i++) {
            props[i] = new Property(propsJson.getJSONObject(i));
        }
        Card[] cards = new Card[cardsJson.length()];
        for(int i = 0; i < cards.length; i++) {
            // creating card objects initiates image downloads
            cards[i] = new Card(cardsJson.getJSONObject(i), props, host);
        }
        //TODO save json locally
        return new OfflineDeck(super.name, super.description, cards, props);
    }
}
