package de.uulm.mal.fancyquartett.utils;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.uulm.mal.fancyquartett.data.OnlineDeck;

/**
 * Created by Snap10 on 12.01.16.
 */
public class OnlineDecksLoader extends AsyncTask<Void, Void, Exception> {

    private String host;
    private String json;
    private String deckjson;
    private OnOnlineDecksLoaded listener;
    private String decklistFilename;
    private ArrayList<OnlineDeck> onlineDecks;
    public OnlineDecksLoader(String host,String decklistFilename, OnOnlineDecksLoaded listener) {
        super();
        this.host =host;
        this.listener=listener;
        this.decklistFilename=decklistFilename;
    }

    public interface OnOnlineDecksLoaded{
        /**
         * Callback Method for OnlineDecksLoader, called when finished or exception is thrown.
         * Possible Exception is deliverd as parameter. Equals null if no exception was thrown
         * @param possibleException
         * @param onlineDecks
         */
        public void onDownloadFinished(Exception possibleException, ArrayList<OnlineDeck> onlineDecks);
    }

    @Override
    protected Exception doInBackground(Void... v) {
        try {
            URL u = new URL("http://"+host +"/"+decklistFilename);
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
            JSONArray decklist = new JSONObject(json).getJSONArray("decks");
            onlineDecks = new ArrayList<OnlineDeck>();
            for (int i = 0; i < decklist.length() ; i++) {
                String deckname= decklist.get(i).toString().toLowerCase();
                URL deckurl = new URL("http://"+host+"/"+deckname+"/"+deckname+".json");
                HttpURLConnection deckconnection = (HttpURLConnection) deckurl.openConnection();
                deckconnection.setConnectTimeout(2000);
                BufferedReader deckin = new BufferedReader(new InputStreamReader(deckconnection.getInputStream()));
                String deckInputLine;
                StringBuffer deckResponse = new StringBuffer();
                while ((deckInputLine = deckin.readLine()) != null) {
                    deckResponse.append(deckInputLine);
                    deckResponse.append("\n");
                }
                in.close();
                deckjson = deckResponse.toString();
                OnlineDeck onlineDeck = new OnlineDeck(new JSONObject(deckjson), host,null);
                onlineDecks.add(onlineDeck);
            }
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
        listener.onDownloadFinished(e, onlineDecks);
    }
}
