package de.uulm.mal.fancyquartett.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.data.Settings;

/**
 * Created by Snap10 on 07/01/16.
 */
public class AssetsInstaller extends AsyncTask<Void, Void, Exception> {

    Context context;
    String path;
    OnAssetsInstallerCompletedListener listener;

    public AssetsInstaller(String path, Context context, OnAssetsInstallerCompletedListener listener) throws IOException {
        this.path = path;
        this.context = context.getApplicationContext();
        this.listener = listener;


    }

    public interface OnAssetsInstallerCompletedListener {
        /**
         * Callback method for onPostExecute of AsyncTask
         *
         * @param possibleException
         */
        public void onAssetsInstallerCompleted(Exception possibleException);
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Exception doInBackground(Void... params) {
        boolean error = false;
        try {
            error = copyAssets();
        } catch (IOException e) {
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
     * @param exception The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Exception exception) {
        super.onPostExecute(exception);
        listener.onAssetsInstallerCompleted(exception);
    }


    private boolean copyAssets() throws IOException {
        AssetManager assetManager = context.getAssets();
        String[] decks = null;
        String[] files = null;
        decks = assetManager.list(path);
        for (String deck : decks) {

            files = assetManager.list(path + "/" + deck);


            if (files != null) for (String filename : files) {

                InputStream in = null;
                OutputStream out = null;

                in = assetManager.open(path + "/" + deck + "/" + filename);
                File outDir = new File(context.getFilesDir() + Settings.localFolder + deck);
                outDir.mkdirs();
                File outFile = new File(outDir, filename);
                outFile.createNewFile();
                out = new FileOutputStream(outFile);
                if (filename.endsWith(".json")) {
                    BufferedReader inbuff = new BufferedReader(new InputStreamReader(in));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = inbuff.readLine()) != null) {
                        response.append(inputLine);
                        response.append("\n");
                    }
                    inbuff.close();
                    String json = response.toString();
                    try {
                        JSONObject deckjson = new JSONObject(json);
                        deckjson=calculateMedianOfJson(deckjson);
                        out = new FileOutputStream(outFile);
                        out.write(deckjson.toString().getBytes(Charset.forName("UTF-8")));
                        out.flush();
                        out.close();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return false;
                    }
                }else{
                    copyFile(in, out);
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }
        return true;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private JSONObject calculateMedianOfJson(JSONObject deckjson) throws JSONException {
        JSONArray cardsjson = deckjson.getJSONArray("cards");
        JSONArray propertyjson = deckjson.getJSONArray("properties");
        ArrayList<Property> properties = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < propertyjson.length(); i++) {
            properties.add(new Property(propertyjson.getJSONObject(i)));
        }
        for (int i = 0; i < cardsjson.length(); i++) {
            Card card = new Card(cardsjson.getJSONObject(i), properties, context.getFilesDir() + Settings.localFolder, deckjson.get("name").toString().toLowerCase(), true);
            cards.add(card);
        }
        for (int i = 0; i < properties.size(); i++) {
            Property prop = properties.get(i);
            double[] medArray = new double[cards.size()];
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
        return deckjson;
    }

    private double calculateMedian(double[] medArray) {
        double median;
        Arrays.sort(medArray);
        if (medArray.length % 2 == 0) {
            median = (medArray[medArray.length / 2] + medArray[medArray.length / 2 - 1]) / 2;
        } else {
            median = medArray[medArray.length / 2];
        }
        return median;
    }
}
