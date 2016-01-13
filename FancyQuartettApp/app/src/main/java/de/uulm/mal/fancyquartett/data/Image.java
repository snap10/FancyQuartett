package de.uulm.mal.fancyquartett.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mk on 01.01.2016.
 */
public class Image implements Serializable {

    private int id = 0;
    private String filename;
    // path that can point to web or local resource
    private String localDeckfolder;
    private String hostadress;
    private String deckname;

    // indicator if image needs to be downloaded
    private boolean isLocal = false;


    public Image(JSONObject json, String hostaddress, String localDeckFolder, String deckname, boolean isLocal) throws JSONException {
        ;
        this.id = json.getInt("id");
        this.filename = json.getString("filename");
        this.localDeckfolder = localDeckFolder;
        this.hostadress = hostaddress;
        this.deckname = deckname;
        this.isLocal = isLocal;
    }

    //Overload Constructor
    public Image(JSONObject json, String localDeckfolder, String deckname, boolean isLocal) throws JSONException {
        ;
        this.id = json.getInt("id");
        this.filename = json.getString("filename");
        this.localDeckfolder = localDeckfolder;

        this.deckname = deckname;
        this.isLocal = isLocal;
    }

    public int id() {
        return id;
    }

    public boolean isLocal() {
        return isLocal;
    }

    /**
     * download the Image from the stored Source
     * Decide if you want to load asyncrounous or not
     *
     * @param asyncronous
     */
    public void download(boolean asyncronous) {
        if (!isLocal) {
            if (asyncronous) {
                new ImgDownloader("http://" + hostadress + "/" + deckname + "/" + filename).execute();
            } else {
                try {
                    Bitmap img;
                    URL u = new URL("http://" + hostadress + "/" + deckname + "/" + filename);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    img = BitmapFactory.decodeStream(c.getInputStream());
                    File file = new File(localDeckfolder, filename);
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    img.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    isLocal = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = BitmapFactory.decodeFile(localDeckfolder + "/" + filename);
        return bitmap;
    }


    public boolean deleteLocalCopy() {
        //TODO write code to delete image
        return false;
    }

    protected void downloadCallback() {
        isLocal = true;
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
                File file = new File(localDeckfolder, filename);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                img.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (IOException e) {
                System.out.println(e);
            }
            return null;
        }


        protected void onPostExecute(Void v) {
            downloadCallback();
        }
    }
}
