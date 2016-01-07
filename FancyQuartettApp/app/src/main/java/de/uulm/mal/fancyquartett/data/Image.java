package de.uulm.mal.fancyquartett.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mk on 01.01.2016.
 */
public class Image {

    private int id = 0;
    private String filename;
    private Context context;
    // path that can point to web or local resource
    private String localFolder;
    private String hostadress;
    private String deckname;

    // indicator if image needs to be downloaded
    private boolean isLocal = false;


    public Image(JSONObject json, String hostaddress,String localFolder, String deckname, boolean isLocal) throws JSONException {
        ;
        this.id = json.getInt("id");
        this.filename = json.getString("filename");
        this.localFolder = localFolder;
        this.hostadress = hostaddress;
        this.deckname = deckname;
        this.isLocal = isLocal;
    }

    //Overload Constructor
    public Image(JSONObject json,String localFolder, String deckname, boolean isLocal) throws JSONException {
        ;
        this.id = json.getInt("id");
        this.filename = json.getString("filename");
        this.localFolder = localFolder;

        this.deckname = deckname;
        this.isLocal = isLocal;
    }

    public int id() {
        return id;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void download() {
        if (!isLocal) {
            new ImgDownloader("http://" + hostadress + "/" + deckname + "/" + filename).execute();
        }
    }

    public void saveBitmap(Bitmap img) {
        if (!isLocal&&img!=null) {
            downloadCallback(img);
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = BitmapFactory.decodeFile(localFolder+"/"+filename);
        return bitmap;
    }


    public boolean deleteLocalCopy() {
        //TODO write code to delete image
        return false;
    }

    protected void downloadCallback(Bitmap img) {
        if (img!=null){
            try {
                File file = new File(localFolder + "/" + deckname, filename);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                img.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                //TODO provide User Feedback
                e.printStackTrace();
            }
        }


        //TODO save file, change path and set isLocal to true
    }

    public String getFileName() {
        return filename;
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
            } catch (IOException e) {
                System.out.println(e);
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            downloadCallback(img);
        }
    }
}
