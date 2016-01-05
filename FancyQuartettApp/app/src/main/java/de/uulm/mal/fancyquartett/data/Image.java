package de.uulm.mal.fancyquartett.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mk on 01.01.2016.
 */
public class Image {

    private int id = 0;

    private String filename = null;

    // path that can point to web or local resource
    private String path = null;

    // indicator if image needs to be downloaded
    private boolean isLocal = false;



    public Image(JSONObject json, String path, boolean isLocal) throws JSONException {
        this.id = json.getInt("id");
        this.filename = json.getString("filename");
        this.path = path;
        this.isLocal = isLocal;
    }

    public int id() { return id; }

    public boolean isLocal() { return isLocal; }

    public void download() {
        if(!isLocal) {
            new ImgDownloader("http://"+path+"/"+filename).execute();
        }
    }

    public boolean deleteLocalCopy() {
        //TODO write code to delete image
        return false;
    }

    protected void downloadCallback(Bitmap img) {
        //TODO save file, change path and set isLocal to true
    }

    private class ImgDownloader extends AsyncTask<Void, Void, Void> {

        private String url;
        private Bitmap img;

        public ImgDownloader(String url) {
            super();
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... v) {
            try {
                URL u = new URL(url);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                img = BitmapFactory.decodeStream(c.getInputStream());
            } catch (IOException e) {System.out.println(e);}
            return null;
        }

        protected void onPostExecute(Void v) {
            downloadCallback(img);
        }
    }
}
