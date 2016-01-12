package de.uulm.mal.fancyquartett.data;

import android.graphics.Bitmap;

/**
 * Created by Snap10 on 04/01/16.
 * Central class for implementing static configuration like URLs, Folders and Preferences
 */
public class Settings {

    public Settings() {
    }

    //Testadress for Localhost on FerdiBirksMacbook, please adapt for your localhost
    public static String defaultServerAdress = "192.168.0.40/fancyquartetttest";
    public static String defaultLocalAssets = "decks";
    public static String defaultLocalFolder = "/decks/";
    public static String defaultServerDecklistJsonFilename = "decks.json";

    public static String serverAdress = "192.168.0.40/fancyquartetttest";
    public static String localAssets = "decks";
    public static String localFolder = "/decks/";
    public static String serverDecklistJsonFilename = "decks.json";

    public String getServerAdress() {
        return serverAdress;
    }

    public void setServerAdress(String serverAdress) {
        this.serverAdress = serverAdress;
    }

    public String getLocalAssets() {
        return localAssets;
    }

    public void setLocalAssets(String localAssets) {
        this.localAssets = localAssets;
    }

    public String getLocalFolder() {
        return localFolder;
    }

    public void setLocalFolder(String localFolder) {
        this.localFolder = localFolder;
    }

    public String getServerDecklistJsonFilename() {
        return serverDecklistJsonFilename;
    }

    public void setServerDecklistJsonFilename(String serverDecklistJsonFilename) {
        this.serverDecklistJsonFilename = serverDecklistJsonFilename;
    }

    /**
     * Calculates a given MaxSize of a Picture to resize it with maintained AspectRatio
     *
     * @param image
     * @param maxSize
     * @return
     */
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
