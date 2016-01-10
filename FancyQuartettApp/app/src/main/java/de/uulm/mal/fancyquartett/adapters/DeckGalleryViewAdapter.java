package de.uulm.mal.fancyquartett.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.CardViewerActivity;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.OfflineDeck;

/**
 * Created by Snap10 on 04/01/16.
 */
public class DeckGalleryViewAdapter extends RecyclerView.Adapter<DeckGalleryViewAdapter.DeckGalleryViewHolder>  {

    public static final int LISTLAYOUT = 0;
    public static final int GRIDLAYOUT = 1;

    OfflineDeck offlineDeck;
    Context context;
    int layout;

    /**
     *
     * @param context
     */
    public DeckGalleryViewAdapter(Context context) {
        this.context = context.getApplicationContext();
        layout = 0;
    }


    /**
     *
     * @param context
     * @param layout
     */
    public DeckGalleryViewAdapter(Context context, int layout) {
        this.context = context.getApplicationContext();
        this.layout = layout;
    }

    public DeckGalleryViewAdapter(Context context, OfflineDeck offlineDeck, int layout) {
        this.context=context.getApplicationContext();
        this.layout=layout;
        this.offlineDeck=offlineDeck;

    }

    /**
     *
     * @return
     */
    public int getLayout() {
        return layout;
    }

    /**
     *
     * @param layout
     */
    public void setLayout(int layout) {
        this.layout = layout;
    }


    /**
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (offlineDeck==null) {
            return 0;
        }
        return offlineDeck.getCards().size();
    }

    /**
     * @param deckGalleryViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final DeckGalleryViewHolder deckGalleryViewHolder, int i) {
        final Card card = offlineDeck.getCards().get(i);
        if (card == null) {
            Toast.makeText(this.getContext(), "Error:Some Cards failed to load", Toast.LENGTH_SHORT).show();
        } else {
            deckGalleryViewHolder.deckName.setText(card.getName());
            deckGalleryViewHolder.deckDescription.setText(card.getDescription());
            deckGalleryViewHolder.deckIcon.setImageBitmap(card.getImages().get(0).getBitmap());
            deckGalleryViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, CardViewerActivity.class);
                    intent.putExtra("deckname",offlineDeck.getName());
                    intent.putExtra("cardnumber",card.getID());
                    intent.putExtra("decksize",offlineDeck.getCards().size());

                    v.getContext().startActivity(intent);
                }
            });
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
    public DeckGalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
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


        return new DeckGalleryViewHolder(itemView, i, context);
    }

    public void setOfflineDeck(OfflineDeck offlineDeck) {
        this.offlineDeck = offlineDeck;
    }


    /**
     * InnerClass TimerViewHolder extends RecyclerView.ViewHolder
     */
    public static class DeckGalleryViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView deckName;
        private TextView deckDescription;
        private ImageView deckIcon;
        private ImageButton deckContextMenuButton;
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
        public DeckGalleryViewHolder(View v, int index, final Context context) {
            super(v);
            view=v;
            deckName = (TextView) v.findViewById(R.id.deckname);
            deckDescription = (TextView) v.findViewById(R.id.deckdescription);
            deckIcon = (ImageView) v.findViewById(R.id.deckicon);
            deckContextMenuButton= (ImageButton)v.findViewById(R.id.deck_options_button);
            deckContextMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.showContextMenu();
                }
            });
            v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    //TODO specify right Menu
                    new MenuInflater(context).inflate(R.menu.gallerylist_menu,menu);
                }
            });
        }

    }


}


