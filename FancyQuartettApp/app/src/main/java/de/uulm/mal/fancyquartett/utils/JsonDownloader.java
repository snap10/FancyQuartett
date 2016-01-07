package de.uulm.mal.fancyquartett.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mk in GalleryModel Class.
 * Moved by Snap10 to utils Package.
 */
public class JsonDownloader extends AsyncTask<Void, Void, Exception> {

    private String url;
    private String json;
    private OnJasonDownloaderFinished listener;

    public JsonDownloader(String url, OnJasonDownloaderFinished listener) {
        super();
        this.url = url;
        this.listener=listener;
    }

    public interface OnJasonDownloaderFinished{
        /**
         * Callback Method for JsonDownloader, called when finished or exception is thrown.
         * Possible Exception is deliverd as parameter. Equals null if no exception was thrown
         * @param possibleException
         * @param json the JsonString
         */
        public void onDownloadFinished(Exception possibleException, String json);
    }

    @Override
    protected Exception doInBackground(Void... v) {
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append("\n");
            }
            in.close();
            json = response.toString();
        } catch (IOException e) {
            System.out.println(e);
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
        listener.onDownloadFinished(e, json);
    }
}