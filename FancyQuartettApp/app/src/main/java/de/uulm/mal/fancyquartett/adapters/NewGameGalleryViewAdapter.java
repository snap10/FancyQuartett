package de.uulm.mal.fancyquartett.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.uulm.mal.fancyquartett.data.GalleryModel;
import de.uulm.mal.fancyquartett.data.OfflineDeck;

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
}
