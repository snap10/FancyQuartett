package de.uulm.mal.fancyquartett.utils;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.mal.fancyquartett.data.OnlineDeck;

/**
 * Created by mk in GalleryModel Class.
 * Moved by Snap10 to utils Package.
 */
public class OnlineDeckLoader extends AsyncTask<Void, Void, Exception> {

    private String host;
    private String json;
    private OnOnlineDeckLoaded listener;
    private String deckname;
    private OnlineDeck onlineDeck;

    /**
     *
     * @param host
     * @param deckname
     * @param listener
     */
    public OnlineDeckLoader(String host,String deckname, OnOnlineDeckLoaded listener) {
        super();
        this.host = host;
        this.listener=listener;
        this.deckname = deckname;
    }


    public interface OnOnlineDeckLoaded {
        /**
         * Callback Method for OnlineDeckLoader, called when finished or exception is thrown.
         * Possible Exception is deliverd as parameter. Equals null if no exception was thrown
         * @param possibleException
         * @param onlineDeck
         */
        public void onDownloadFinished(Exception possibleException, OnlineDeck onlineDeck);
    }

    /**
     *
     * @param v
     * @return
     */
    @Override
    protected Exception doInBackground(Void... v) {
        try {

            URL u = new URL("http://"+host+"/"+deckname+"/"+deckname+".json");
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
            onlineDeck = new OnlineDeck(new JSONObject(json),host,null);
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
        listener.onDownloadFinished(e, onlineDeck);
    }
}