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

    private String fullLocalPath;
    private String fullPath;
    private int id;
    private int cardID;
    private int order;

    private String description;
    private String filename;
    // path that can point to web or local resource
    private String localDeckfolder;
    private String hostadress;
    private String deckname;

    // indicator if image needs to be downloaded
    private boolean isLocal = false;


    public Image(JSONObject json, String hostaddress, String localDeckFolder, String deckname, boolean isLocal) throws JSONException {
        this.id = json.getInt("id");
        this.filename = json.getString("filename");
        this.localDeckfolder = localDeckFolder;
        this.hostadress = hostaddress;
        this.deckname = deckname;
        this.isLocal = isLocal;
        this.fullPath = "http://" + hostadress + "/" + deckname + "/" + filename;
        this.fullLocalPath = localDeckfolder + "/" + filename;
    }

    //Overload Constructor
    public Image(JSONObject json, String localDeckfolder, String deckname, boolean isLocal) throws JSONException {
        this.id = json.getInt("id");
        this.filename = json.getString("filename");
        this.localDeckfolder = localDeckfolder;
        this.deckname = deckname;
        this.isLocal = isLocal;
        this.fullPath = "http://" + hostadress + "/" + deckname + "/" + filename;
        this.fullLocalPath = localDeckfolder + "/" + filename;
    }

    public Image(String fullPath) {
        this.fullPath = fullPath;
        isLocal=false;
    }

    /**
     *
     * @param fullPath
     * @param isLocal
     */
    public Image(String fullPath,boolean isLocal) {
        if (isLocal){
            this.isLocal=isLocal;
            fullLocalPath=fullPath;
        }else{
            this.fullPath = fullPath;
            isLocal=false;
        }
    }

    /**
     * Downloads the Image if isLocal==false.
     * @param imgJson
     * @param isLocal if ==false the Image will be downloaded using the Path from the JSON (in the current Thread)
     * @throws JSONException
     */
    public Image(JSONObject imgJson, boolean isLocal) throws JSONException {
        this.id = imgJson.getInt("id");
        this.cardID=imgJson.getInt("card");
        this.order=imgJson.getInt("order");
        this.description=imgJson.getString("description");
        this.fullPath=imgJson.getString("image");
        this.isLocal=isLocal;
        if (!isLocal){
            fullLocalPath=download(false);
        }else{
            fullLocalPath = fullPath;
        }
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
     *returns null if exeption occurs or if download is conducted async
     * @param asyncronous
     */
    public String download(boolean asyncronous) {
        if (!isLocal) {
            if (asyncronous) {
                new ImgDownloader(fullPath,fullLocalPath).execute();
                return null;
            } else {
                try {
                    String extension = fullPath.substring(fullPath.lastIndexOf('.') + 1);
                    Bitmap img;
                    URL u = new URL(fullPath);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestProperty("Authorization", Settings.serverAuthorization);
                    c.setRequestProperty("Content-Type", "image/" + extension);
                    img = BitmapFactory.decodeStream(c.getInputStream());
                    File file = new File(fullLocalPath);
                    file.mkdirs();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    img.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    isLocal = true;
                    return file.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = BitmapFactory.decodeFile(fullLocalPath);
        return bitmap;
    }


    public boolean deleteLocalCopy() {
        //TODO write code to delete image
        return false;
    }

    protected void downloadCallback(String localPath) {
        if (localPath!=null){
            this.fullLocalPath=localPath;
            isLocal = true;
        }
    }

    /**
     * Used to Download a Image from an OnlineSource to the Specified Folder.
     * Filename is a jpg afterwards
     * @param sourcePath
     * @param outDir
     * @return
     */
    public static String downloadFromTo(String sourcePath, File outDir) {
        try {

            String extension = sourcePath.substring(sourcePath.lastIndexOf('.') + 1);
            Bitmap img;
            URL u = new URL(sourcePath);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("Authorization", Settings.serverAuthorization);
            c.setRequestProperty("Content-Type", "image/"+extension);
            img = BitmapFactory.decodeStream(c.getInputStream());
            File file = new File(outDir, sourcePath.substring(sourcePath.lastIndexOf("/") + 1, sourcePath.lastIndexOf("."))+".jpg");
            outDir.mkdirs();
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    private class ImgDownloader extends AsyncTask<Void, Void, String> {

        private final String newFullLocalPath
                ;
        private String url;
        private Bitmap img;

        public ImgDownloader(String url,String newFullLocalPath) {
            super();
            this.url = url;
            this.newFullLocalPath=newFullLocalPath;
        }

        @Override
        protected String doInBackground(Void... v) {
            try {
                String extension = url.substring(url.lastIndexOf('.') + 1);
                URL u = new URL(url);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestProperty("Authorization", Settings.serverAuthorization);
                c.setRequestProperty("Content-Type", "image/"+extension);
                img = BitmapFactory.decodeStream(c.getInputStream());
                File file = new File(newFullLocalPath);
                file.mkdirs();
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                img.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                return file.getAbsolutePath();
            } catch (IOException e) {
                System.out.println(e);
                return null;
            }

        }


        protected void onPostExecute(String localPath) {
            downloadCallback(localPath);
        }
    }
}
