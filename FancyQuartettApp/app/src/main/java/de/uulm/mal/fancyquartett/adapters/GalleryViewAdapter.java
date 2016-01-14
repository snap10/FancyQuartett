package de.uulm.mal.fancyquartett.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.ArrayList;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.CardGalleryActivity;
import de.uulm.mal.fancyquartett.data.GalleryModel;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.OnlineDeck;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.DeckDownloader;
import de.uulm.mal.fancyquartett.utils.LocalDecksLoader;
import de.uulm.mal.fancyquartett.utils.OnlineDecksLoader;


/**
 * Created by Snap10 on 04/01/16.
 */
public class GalleryViewAdapter extends RecyclerView.Adapter<GalleryViewAdapter.GalleryViewHolder> implements OnlineDecksLoader.OnOnlineDecksLoaded, LocalDecksLoader.OnLocalDecksLoadedListener, DeckDownloader.OnDeckDownloadedListener {

    public static final int LISTLAYOUT = 0;
    public static final int GRIDLAYOUT = 1;

    GalleryModel galleryModel;
    Context context;
    int layout;
    private View.OnClickListener itemClickListener;
    private ProgressDialog mProgressDialog;
    protected SwipeRefreshLayout swipeRefreshLayout;

    /**
     * @param context
     */
    public GalleryViewAdapter(Context context) {
        this.context = context;
        galleryModel = new GalleryModel();
        layout = 0;
    }

    /**
     * @param context
     * @param galleryModel
     */
    public GalleryViewAdapter(Context context, GalleryModel galleryModel) {
        this.context = context.getApplicationContext();
        this.galleryModel = galleryModel;
        layout = 0;

    }

    /**
     * @param context
     * @param galleryModel
     * @param layout
     */
    public GalleryViewAdapter(Context context, GalleryModel galleryModel, int layout) {
        this.context = context.getApplicationContext();
        this.galleryModel = galleryModel;
        this.layout = layout;
    }

    /**
     * @return
     */
    public int getLayout() {
        return layout;
    }

    /**
     * @param layout
     */
    public void setLayout(int layout) {
        this.layout = layout;
    }

    /**
     * @return
     */
    public GalleryModel getGalleryModel() {
        return galleryModel;
    }

    /**
     * @param galleryModel
     */
    public void setGalleryModel(GalleryModel galleryModel) {
        this.galleryModel = galleryModel;
    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        if (galleryModel == null) {
            return 0;
        } else {
            return galleryModel.getSize();
        }
    }

    /**
     * @param galleryViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final GalleryViewHolder galleryViewHolder, int i) {
        final OfflineDeck offlineDeck = galleryModel.getOfflineDeck(i);
        if (offlineDeck == null) {
            //No OfflineDeck Information available thus just set Name and Description
            final OnlineDeck onlineDeck = galleryModel.getOnlineDeck(i);
            galleryViewHolder.deckName.setText(onlineDeck.getName());
            galleryViewHolder.deckDescription.setText(onlineDeck.getDescription());
            galleryViewHolder.deckIcon.setImageBitmap(onlineDeck.getDeckimage().getBitmap());
            galleryViewHolder.view.setOnClickListener(getItemClickListener(onlineDeck, this));
        } else {
            galleryViewHolder.deckName.setText(offlineDeck.getName());
            galleryViewHolder.deckDescription.setText(offlineDeck.getDescription());
            galleryViewHolder.deckIcon.setImageBitmap(offlineDeck.getCards().get(0).getImages().get(0).getBitmap());
            galleryViewHolder.view.setOnClickListener(getItemClickListener(offlineDeck));
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
     * Method for Callback of LocalDecksLoader, receives an ArrayList<OfflineDecks> when Task is completed.
     *
     * @param offlineDecks
     */
    @Override
    public void onLocalDecksLoaded(ArrayList<OfflineDeck> offlineDecks) {
        if (swipeRefreshLayout!=null){
            swipeRefreshLayout.setRefreshing(false);
        }
        if (offlineDecks != null) {
            galleryModel.addOfflineDecks(offlineDecks);
            galleryModel.setAdapter(this);
            notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), R.string.noLocalDecksFound, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback Method for OnlineDecksLoader, called when finished or exception is thrown.
     * Possible Exception is deliverd as parameter. Equals null if no exception was thrown
     *
     * @param possibleException
     * @param onlineDecks
     */
    @Override
    public void onDownloadFinished(Exception possibleException, ArrayList<OnlineDeck> onlineDecks) {
        if (swipeRefreshLayout!=null){
            swipeRefreshLayout.setRefreshing(false);
        }
        if (possibleException == null) {
            if (onlineDecks != null) {
                galleryModel.addOnlineDecks(onlineDecks);
                notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), R.string.noOnlineDecksFound, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), possibleException.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * @param offlineDeck
     * @return itemClickListener
     */
    protected View.OnClickListener getItemClickListener(final OfflineDeck offlineDeck) {
        View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CardGalleryActivity.class);
                intent.putExtra("deckname", offlineDeck.getName());
                intent.putExtra("offlinedeck", offlineDeck);
                v.getContext().startActivity(intent);
            }
        };
        return itemClickListener;
    }


    /**
     * @param onlineDeck
     * @return itemClickListener
     */
    protected View.OnClickListener getItemClickListener(final OnlineDeck onlineDeck, final DeckDownloader.OnDeckDownloadedListener listener) {
        View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownloadAlertDialog(onlineDeck, listener, v);
            }
        };
        return itemClickListener;
    }

    /**
     * @param onlinedeck
     */
    protected void showDownloadAlertDialog(final OnlineDeck onlinedeck, final DeckDownloader.OnDeckDownloadedListener listener, final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage(R.string.downloadandsavedialogtext).setTitle(R.string.downloaddialogtitle);

        // Add the buttons
        builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    final DeckDownloader downloader = new DeckDownloader(Settings.serverAdress, getContext().getFilesDir() + Settings.localFolder, Settings.serverRootPath,onlinedeck.getId(), listener);
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
        if (possibleException == null) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (offlineDeck != null) {
                galleryModel.addOfflineDeck(offlineDeck);
                notifyDataSetChanged();
            }
        } else {
            Toast.makeText(getContext(), possibleException.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout=swipeRefreshLayout;
    }

    /**
     * InnerClass TimerViewHolder extends RecyclerView.ViewHolder
     */
    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

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
        public GalleryViewHolder(View v, int index, final Context context) {
            super(v);
            view = v;
            deckName = (TextView) v.findViewById(R.id.deckname);
            deckDescription = (TextView) v.findViewById(R.id.deckdescription);
            deckIcon = (ImageView) v.findViewById(R.id.deckicon);
            deckContextMenuButton = (ImageButton) v.findViewById(R.id.deck_options_button);
            deckContextMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.showContextMenu();
                }
            });
            v.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    new MenuInflater(context).inflate(R.menu.gallerylist_menu, menu);
                }
            });
        }

    }


}

