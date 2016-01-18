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

import de.uulm.mal.fancyquartett.data.Image;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Settings;


/**
 * Created by Snap10 on 12.01.16.
 */
public class DeckDownloader extends AsyncTask<Void, Void, Exception> {


    private String localpath;
    private String rootpath;
    private String host;
    private String json;
    private OnDeckDownloadedListener listener;
    private int deckID;
    private OfflineDeck offlineDeck;

    /**
     * @param host
     * @param deckID
     * @param listener
     */
    public DeckDownloader(String host, String localpath, String rootpath, int deckID, OnDeckDownloadedListener listener) {
        super();
        this.host = host;
        this.listener = listener;
        this.deckID = deckID;
        if (rootpath == null) {
            this.rootpath = "/decks";
        } else {
            this.rootpath = rootpath;
        }
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
            String json = retrieveDataFromServer(host, rootpath + "/" + deckID,"application/json");

            JSONObject deckjsonTmp = new JSONObject(json);
            int deckid = deckjsonTmp.getInt("id");

            JSONObject deckjson = new JSONObject(retrieveDataFromServer(host, rootpath + "/" + deckid,"application/json"));
            File outDir = new File(localpath + deckid+"/images");
            outDir.mkdirs();
            String localPath = Image.downloadFromTo(deckjson.getString("image"), outDir);
            if(localPath!=null){
                deckjson.put("image",localPath);
            }
            JSONArray cardsTmp = new JSONArray(retrieveDataFromServer(host, rootpath + "/" + deckid + "/cards","application/json"));
            //Download Attribute Images
            JSONArray tmpAttributes = new JSONArray(retrieveDataFromServer(host, rootpath + "/" + deckid + "/cards/" + cardsTmp.getJSONObject(0).getInt("id") + "/attributes","application/json"));
            String[] localAttributeImagePath = new String[tmpAttributes.length()];
            for (int i = 0; i <tmpAttributes.length() ; i++) {
                JSONObject attribute =tmpAttributes.getJSONObject(i);
                outDir = new File(localpath + deckid+"/images/attributes");
                outDir.mkdirs();
                System.out.println(attribute.getString("image"));
                localAttributeImagePath[i] = Image.downloadFromTo(attribute.getString("image"),outDir);
            }
            JSONArray cardsJsonArray = new JSONArray();

            ArrayList<double[]> valuesList = new ArrayList<>();
            for (int j = 0; j < cardsTmp.length(); j++) {
                int cardID = cardsTmp.getJSONObject(j).getInt("id");
                JSONObject cardTmp = new JSONObject(retrieveDataFromServer(host, rootpath + "/" + deckid + "/cards/" + cardID,"application/json"));
                //Attributes
                JSONArray attributes = new JSONArray(retrieveDataFromServer(host, rootpath + "/" + deckid + "/cards/" + cardID + "/attributes","application/json"));
                double[] attributesValues = new double[attributes.length()];
                for (int k = 0; k < attributes.length(); k++) {
                    attributesValues[k] = attributes.getJSONObject(k).getDouble("value");
                    //If localImagePath is not null rewrite JSON Path to localpath
                    JSONObject attributeTmp = attributes.getJSONObject(k);
                    attributeTmp.put("id",k);
                    if (localAttributeImagePath[k]!=null){

                        attributeTmp.put("image",localAttributeImagePath[k]);
                    }
                    attributes.put(k,attributeTmp);
                }
                valuesList.add(attributesValues);
                //Images
                JSONArray images = new JSONArray(retrieveDataFromServer(host, rootpath + "/" + deckid + "/cards/" + cardID + "/images","application/json"));
                images = downloadCardImages(deckjsonTmp, images,deckid);
                cardTmp.put("attributes", attributes);
                cardTmp.put("images", images);
                cardsJsonArray.put(cardTmp);
            }
            cardsJsonArray=writeMedianToAttributeJson(cardsTmp, cardsJsonArray, valuesList);

            deckjson.put("cards", cardsJsonArray);
            String deckname = deckjson.getString("name").toLowerCase();
            outDir = new File(localpath + deckid);
            outDir.mkdirs();
            File outFile = new File(outDir, deckid + ".json");
            outFile.createNewFile();
            FileOutputStream out = new FileOutputStream(outFile);
            out.write(deckjson.toString().getBytes(Charset.forName("UTF-8")));
            out.flush();
            out.close();
            offlineDeck = new OfflineDeck(deckjson,true);
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
     *
     * @param deckjsonTmp
     * @param images
     * @return
     * @throws JSONException
     */
    private JSONArray downloadCardImages(JSONObject deckjsonTmp, JSONArray images, int deckid) throws JSONException {
        for (int i = 0; i <images.length() ; i++) {
            JSONObject imageTmp =images.getJSONObject(i);
            File outDir = new File(localpath + deckid+"/images/cards");
            outDir.mkdirs();
            String localPath = Image.downloadFromTo(imageTmp.getString("image"), outDir);
            if(localPath!=null){
                imageTmp.put("image",localPath);
                images.put(i,imageTmp);
            }
        }
        return images;
    }

    /**
     *
     * @param cardsTmp
     * @param cardsJsonArray
     * @param valuesList
     * @return
     * @throws JSONException
     */
    private JSONArray writeMedianToAttributeJson(JSONArray cardsTmp, JSONArray cardsJsonArray, ArrayList<double[]> valuesList) throws JSONException {
        //Write median to JSON
        double[] medianArray = calculateMedian(valuesList);
        //For every Card get the Attributes and write the median to the attribute Object. Put That Object back in the Array and Put the Array to the Card
        //Then put the Card back in the CardsArray
        for (int j = 0; j < cardsTmp.length(); j++) {
            JSONObject cardTmp = cardsJsonArray.getJSONObject(j);
            JSONArray attributes = cardTmp.getJSONArray("attributes");
            for (int k = 0; k < attributes.length(); k++) {
                JSONObject tmpAttribute = attributes.getJSONObject(k);
                tmpAttribute.put("median", medianArray[k]);
                attributes.put(k, tmpAttribute);
            }
            cardTmp.put("attributes", attributes);
            cardsJsonArray.put(j, cardTmp);
        }
        return cardsJsonArray;
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


    private String retrieveDataFromServer(String host, String path,String contentType) throws IOException {
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

    /**
     * Calculates the Median of the Given float Array and returns the median
     *
     * @param valuesList
     * @return
     */
    private double[] calculateMedian(ArrayList<double[]> valuesList) {
        double[] median = new double[valuesList.get(0).length];
        double[] tmp = new double[valuesList.size()];
        for (int i = 0; i < median.length; i++) {
            for (int k = 0; k < valuesList.size(); k++) {
                tmp[k] = valuesList.get(k)[i];
                median[i] = returnMedian(tmp);
            }
        }
        return median;
    }

    private double returnMedian(double[] medArray) {
        Arrays.sort(medArray);
        double median;
        if (medArray.length % 2 == 0) {
            median = (medArray[medArray.length / 2] + medArray[medArray.length / 2 - 1]) / 2;
        } else {
            median = medArray[medArray.length / 2];
        }
        return median;
    }

}
