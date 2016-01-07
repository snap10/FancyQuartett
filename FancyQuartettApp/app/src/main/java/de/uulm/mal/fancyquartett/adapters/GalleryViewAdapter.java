package de.uulm.mal.fancyquartett.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.Deck;
import de.uulm.mal.fancyquartett.data.GalleryModel;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.LocalDecksLoader;


/**
 * Created by Snap10 on 04/01/16.
 */
public class GalleryViewAdapter extends RecyclerView.Adapter<GalleryViewAdapter.GalleryViewHolder> implements LocalDecksLoader.OnTaskCompleted {

    public static final int LISTLAYOUT = 0;
    public static final int GRIDLAYOUT = 1;

    GalleryModel galleryModel;
    Context context;
    int layout;

    public GalleryViewAdapter(Context context) {
        this.context = context.getApplicationContext();
        galleryModel = new GalleryModel();
        layout = 0;
    }


    public GalleryViewAdapter(Context context, GalleryModel galleryModel) {
        this.context = context;
        this.galleryModel = galleryModel;
        layout = 0;

    }

    public GalleryViewAdapter(Context context, GalleryModel galleryModel, int layout) {
        this.context = context;
        this.galleryModel = galleryModel;
        this.layout = layout;
    }


    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public GalleryModel getGalleryModel() {
        return galleryModel;
    }

    public void setGalleryModel(GalleryModel galleryModel) {
        this.galleryModel = galleryModel;
    }

    @Override
    public int getItemCount() {
        //TODO remove in Production, just for testing
        if (galleryModel.getSize() == 0) {
            galleryModel.addTestDecks();
        }
        //TODO
        return galleryModel.getSize();
    }

    /**
     * @param galleryViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final GalleryViewHolder galleryViewHolder, int i) {
        OfflineDeck offlineDeck = galleryModel.getOfflineDeck(i);
        if (offlineDeck == null) {
            //No OfflineDeck Information available thus just set Name and Description
            galleryViewHolder.deckName.setText(galleryModel.getDeck(i).getName());
            galleryViewHolder.deckDescription.setText(galleryModel.getDeck(i).getDescription());
        } else {
            galleryViewHolder.deckName.setText(offlineDeck.getName());
            galleryViewHolder.deckDescription.setText(offlineDeck.getDescription());
            galleryViewHolder.deckIcon.setImageBitmap(offlineDeck.getCards().get(0).getImages().get(0).getBitmap());
        }

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
        View itemView;
        if (GRIDLAYOUT == layout) {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.deckgallery_grid_item, viewGroup, false);
        } else {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.deckgallery_list_item, viewGroup, false);
        }


        return new GalleryViewHolder(itemView, i, context);
    }


    /**
     * Calculates a given MaxSize of a Picture to resize it with maintained AspectRatio
     *
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
     * Method for Callback of LocalDecksLoader, receives an ArrayList<OfflineDecks> when Task is completed.
     *
     * @param object
     */
    @Override
    public void onTaskCompleted(Object object) {
        if (object != null) {
            galleryModel = new GalleryModel((ArrayList<OfflineDeck>) object);
            galleryModel.setAdapter(this);
            notifyDataSetChanged();
            galleryModel.fetchOnlineDeck(Settings.serverAdress, "bikes");
        } else {
            galleryModel=new GalleryModel(this, Settings.serverAdress, context.getFilesDir() + Settings.localFolder);
            galleryModel.setAdapter(this);
            notifyDataSetChanged();
            galleryModel.fetchOnlineDeck(Settings.serverAdress, "bikes");
        }


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
         * @param v
         * @param index
         * @param context
         */
        public GalleryViewHolder(View v, int index, final Context context) {
            super(v);
            deckName = (TextView) v.findViewById(R.id.deckname);
            deckDescription = (TextView) v.findViewById(R.id.deckdescription);
            deckIcon = (ImageView) v.findViewById(R.id.deckicon);

            v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    new MenuInflater(context).inflate(R.menu.gallerylist_menu,menu);
                }
            });
        }

    }


}

