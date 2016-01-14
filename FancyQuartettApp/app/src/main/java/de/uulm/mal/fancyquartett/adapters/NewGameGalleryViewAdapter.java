package de.uulm.mal.fancyquartett.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.GalleryModel;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.OnlineDeck;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.DeckDownloader;

/**
 * Created by Snap10 on 08/01/16.
 */
public class NewGameGalleryViewAdapter extends GalleryViewAdapter {
    Activity activity;
    ProgressDialog mProgressDialog;

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
                conData.putSerializable("offlinedeck", offlineDeck);
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
     * @param listener
     */
    @Override
    protected void showDownloadAlertDialog(final OnlineDeck onlinedeck, final DeckDownloader.OnDeckDownloadedListener listener, final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage(R.string.downloadbeforeuseonlinedeck).setTitle(R.string.downloadalertdialogtitle);

        // Add the buttons
        builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    final DeckDownloader downloader = new DeckDownloader(Settings.serverAdress, getContext().getFilesDir() + Settings.localFolder, Settings.serverRootPath, onlinedeck.getId(), listener);
                    downloader.execute();

// instantiate it within the onCreate method
                    mProgressDialog = new ProgressDialog(v.getContext());
                    mProgressDialog.setMessage(context.getString(R.string.downloadProgressText));
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloader.cancel(true);
                            dialog.dismiss();
                        }
                    });
                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            downloader.cancel(true);
                        }
                    });
                    mProgressDialog.show();

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

    /**
     * Callback Method for DeckDownloader, called when finished or exception is thrown.
     * Possible Exception is deliverd as parameter. Equals null if no exception was thrown
     *
     * @param possibleException
     * @param offlineDeck
     */
    @Override
    public void onDeckDownloadFinished(Exception possibleException, OfflineDeck offlineDeck) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (possibleException == null) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (offlineDeck != null) {
                Bundle conData = new Bundle();
                conData.putSerializable("offlinedeck", offlineDeck);
                Intent intent = new Intent();
                intent.putExtras(conData);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        } else {
            if (mProgressDialog != null) {
                mProgressDialog.setMessage("Error while Downloading");
                possibleException.printStackTrace();
            }
        }

    }
}
