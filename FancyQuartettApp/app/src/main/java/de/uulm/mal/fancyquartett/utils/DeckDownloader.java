package de.uulm.mal.fancyquartett.utils;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Property;

/**
 * Created by Snap10 on 12.01.16.
 */
public class DeckDownloader extends AsyncTask<Void, Void, Exception> {


    private final String localpath;
    private String host;
    private String json;
    private OnDeckDownloadedListener listener;
    private String deckname;
    private OfflineDeck offlineDeck;

    /**
     * @param host
     * @param deckname
     * @param listener
     */
    public DeckDownloader(String host, String localpath, String deckname, OnDeckDownloadedListener listener) {
        super();
        this.host = host;
        this.listener = listener;
        this.deckname = deckname.toLowerCase();
        this.localpath = localpath;
    }


    public interface OnDeckDownloadedListener {
        /**
         * Callback Method for DeckDownloader, called when finished or exception is thrown.
         * Possible Exception is deliverd as parameter. Equals null if no exception was thrown
         *
         * @param possibleException
         * @param offlineDeck
         */
        public void onDeckDownloadFinished(Exception possibleException, OfflineDeck offlineDeck);
    }

    /**
     * @param v
     * @return
     */
    @Override
    protected Exception doInBackground(Void... v) {
        try {

            URL u = new URL("http://" + host + "/" + deckname + "/" + deckname + ".json");
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setConnectTimeout(2000);
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append("\n");
            }
            in.close();
            json = response.toString();
            JSONObject deckjson = new JSONObject(json);
            JSONArray cardsjson = deckjson.getJSONArray("cards");
            JSONArray propertyjson = deckjson.getJSONArray("properties");
            ArrayList<Property> properties = new ArrayList<>();
            ArrayList<Card> cards = new ArrayList<>();
            for (int i = 0; i < propertyjson.length(); i++) {
                properties.add(new Property(propertyjson.getJSONObject(i)));
            }
            for (int i = 0; i < cardsjson.length(); i++) {
                Card card = new Card(cardsjson.getJSONObject(i), properties,host, localpath+"/"+deckname, deckname, false);
                cards.add(card);
            }
            for (int i = 0; i < properties.size(); i++) {
                Property prop = properties.get(i);
                float[] medArray = new float[cards.size()];
                for (int j = 0; j < cards.size(); j++) {
                    Card card = cards.get(j);
                    medArray[j] = card.getValue(prop);
                }
                prop.setMedian(calculateMedian(medArray));
                JSONObject propjson = propertyjson.getJSONObject(i);
                propjson.put("median", prop.getMedian());
                propertyjson.put(i, propjson);
            }

            deckjson.put("properties", propertyjson);

            File outDir = new File(localpath + deckname);
            outDir.mkdirs();
            File outFile = new File(outDir, deckname.toLowerCase() + ".json");
            outFile.createNewFile();
            FileOutputStream out = new FileOutputStream(outFile);
            out.write(deckjson.toString().getBytes(Charset.forName("UTF-8")));
            out.flush();
            out.close();

            offlineDeck = new OfflineDeck(deckname,deckjson.getString("description"),cards,properties);
        } catch (IOException e) {
            System.out.println(e);
            return e;
        } catch (JSONException e) {
            e.printStackTrace();
            return e;
        }

        return null;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param e The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        listener.onDeckDownloadFinished(e, offlineDeck);
    }

    /**
     * Calculates the Median of the Given float Array and returns the median
     *
     * @param medArray
     * @return
     */
    private float calculateMedian(float[] medArray) {
        float median;
        Arrays.sort(medArray);
        if (medArray.length % 2 == 0) {
            median = (medArray[medArray.length / 2] + medArray[medArray.length / 2 - 1]) / 2;
        } else {
            median = medArray[medArray.length / 2];
        }
        return median;
    }

}
