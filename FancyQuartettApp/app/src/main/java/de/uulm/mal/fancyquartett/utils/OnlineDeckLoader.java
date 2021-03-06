package de.uulm.mal.fancyquartett.utils;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.uulm.mal.fancyquartett.data.Image;
import de.uulm.mal.fancyquartett.data.OnlineDeck;
import de.uulm.mal.fancyquartett.data.Settings;

/**
 * Created by mk in GalleryModel Class.
 * Moved by Snap10 to utils Package.
 */
public class OnlineDeckLoader extends AsyncTask<Void, Void, Exception> {

    private int deckid;
    private String rootpath;
    private String host;
    private String json;
    private OnOnlineDeckLoaded listener;
    private String cachePath;
    private OnlineDeck onlineDeck;

    /**
     *
     * @param host
     * @param rootpath
     * @param deckid
     * @param cachePath
     * @param listener
     */
    public OnlineDeckLoader(String host, String rootpath, int deckid,String cachePath, OnOnlineDeckLoaded listener) {
        super();
        this.host = host;
        this.listener = listener;
        this.cachePath = cachePath;
        this.rootpath = rootpath;
        this.deckid = deckid;
    }


    public interface OnOnlineDeckLoaded {
        /**
         * Callback Method for DeckDownloader, called when finished or exception is thrown.
         * Possible Exception is deliverd as parameter. Equals null if no exception was thrown
         *
         * @param possibleException
         * @param onlineDeck
         */
        public void onDownloadFinished(Exception possibleException, OnlineDeck onlineDeck);
    }

    /**
     * @param v
     * @return
     */
    @Override
    protected Exception doInBackground(Void... v) {
        try {

            JSONObject deckjson = new JSONObject(retrieveDataFromServer(host, rootpath + "/" + deckid, "application/json"));
            //TODO add caching functionallity
            File outDir = new File(cachePath +"/"+ deckid+"/images");
            outDir.mkdirs();
            String localPath = Image.downloadFromTo(deckjson.getString("image"), outDir);
            if(localPath!=null){
                deckjson.put("image",localPath);
            }
            onlineDeck =new  OnlineDeck(deckid, deckjson.getString("name"), deckjson.getString("description"), new Image(deckjson.getString("image"),true), deckjson.getString("misc"), deckjson.getString("misc_version"));

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

    /**
     * @param host
     * @param path
     * @param contentType
     * @return
     * @throws IOException
     */
    private String retrieveDataFromServer(String host, String path, String contentType) throws IOException {
        URL u = new URL("http://" + host + "/" + path);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestProperty("Authorization", Settings.serverAuthorization);
        c.setRequestProperty("Content-Type", contentType);
        c.setConnectTimeout(1000);
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }
        in.close();
        String json = response.toString();
        return json;
    }
}