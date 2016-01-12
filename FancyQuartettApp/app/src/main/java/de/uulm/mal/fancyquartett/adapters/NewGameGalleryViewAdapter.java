package de.uulm.mal.fancyquartett.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.GalleryModel;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.OnlineDeck;

/**
 * Created by Snap10 on 08/01/16.
 */
public class NewGameGalleryViewAdapter extends GalleryViewAdapter {
    Activity activity;

    /**
     * @param activity
     * @param galleryModel
     * @param layout
     */
    public NewGameGalleryViewAdapter(Activity activity, GalleryModel galleryModel, int layout) {
        super(activity, galleryModel, layout);
        this.activity = activity;
    }

    /**
     * @param activity
     */
    public NewGameGalleryViewAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public View.OnClickListener getItemClickListener(final OfflineDeck offlineDeck) {
        View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle conData = new Bundle();
                conData.putSerializable("offlinedeck",offlineDeck);
                Intent intent = new Intent();
                intent.putExtras(conData);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        };
        return itemClickListener;
    }

    /**
     * @param onlinedeck
     */
    @Override
    protected void showDownloadAlertDialog(final OnlineDeck onlinedeck) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.downloadbeforeuseonlinedeck).setTitle(R.string.downloadalertdialogtitle);

        // Add the buttons
        builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    //TODO choose right method to download
                    onlinedeck.download();
                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO REMOVE TOAST
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //DO nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
