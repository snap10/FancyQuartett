package de.uulm.mal.fancyquartett.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mk on 02.01.2016.
 */
public class OnlineDeck extends Deck {

    private JSONObject deckJson;
    private String host;
    private GalleryModel gallery;

    public OnlineDeck(JSONObject json, String host, GalleryModel gallery) throws JSONException {

        super(json.getString("name"),json.getString("description"));
        deckJson = json;

        this.host = host;
        this.gallery = gallery;
    }

    /**
     * DummyConstructor for Testing with Custom Decks
     * //TODO Delete when appropriate Deck Ressource is availabe
     * @param name
     * @param description
     */
    public OnlineDeck(String name,String description){
        super(name,description);
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
            cards[i] = new Card(cardsJson.getJSONObject(i), props, host, false);
        }
        OfflineDeck offlineDeck = new OfflineDeck(super.name, super.description, cards, props);
        gallery.move(this, offlineDeck);
        //TODO save json locally
        return offlineDeck;
    }
}
