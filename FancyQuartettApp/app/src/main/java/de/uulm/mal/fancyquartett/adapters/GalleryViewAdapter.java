package de.uulm.mal.fancyquartett.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.GalleryModel;

/**
 * Created by Snap10 on 04/01/16.
 */
public class GalleryViewAdapter extends RecyclerView.Adapter<GalleryViewAdapter.GalleryViewHolder> {

    GalleryModel galleryModel;

    Context context;

    public GalleryViewAdapter(Context context, GalleryModel galleryModel) {
        this.context = context;
        this.galleryModel=galleryModel;
    }

    @Override
    public int getItemCount() {
        //TODO remove in Production, just for testing
        if (galleryModel.getSize()==0){
            galleryModel.addTestDecks();
        }
        //TODO
        return galleryModel.getSize();
    }

    /**
     *
     * @param galleryViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final GalleryViewHolder galleryViewHolder, int i) {

    //TODO implement the and ClickListeners
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.deckgallery_list_item, viewGroup, false);


        return new GalleryViewHolder(itemView, i, context);
    }



    /**
     * Calculates a given MaxSize of a Picture to resize it with maintained AspectRatio
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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

    /**
     * InnerClass TimerViewHolder extends RecyclerView.ViewHolder
     */
    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        private TextView deckName;
        private TextView deckDescription;
        private ImageView deckIcon;
        protected int index;

        protected Context context;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        /**
         *
         * @param v
         * @param index
         * @param context
         */
        public GalleryViewHolder(View v, int index, Context context) {
            super(v);
            deckName = (TextView) v.findViewById(R.id.deckname);
            deckDescription = (TextView) v.findViewById(R.id.deckdescription);
            deckIcon = (ImageView) v.findViewById(R.id.deckicon);


        }

    }


}

