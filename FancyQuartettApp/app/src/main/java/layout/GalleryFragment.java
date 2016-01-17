package layout;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.GalleryViewAdapter;
import de.uulm.mal.fancyquartett.data.GalleryModel;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.OnlineDeck;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.LocalDecksLoader;
import de.uulm.mal.fancyquartett.utils.OnlineDecksLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {

    GalleryViewAdapter galleryViewAdapter;
    RecyclerView recList;
    LinearLayoutManager llm;
    GridLayoutManager glm;

    private OnFragmentInteractionListener mListener;
    private Menu menu;
    private LocalDecksLoader loader;
    private OnlineDecksLoader onlineLoader;

    public GalleryFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GalleryFragment.
     */
    public static GalleryFragment newInstance(Bundle bundle) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //Initialize Data
        galleryViewAdapter = new GalleryViewAdapter(getContext());
        loader = new LocalDecksLoader(getContext().getFilesDir() + Settings.localFolder, galleryViewAdapter);
        loader.execute();

       onlineLoader =new OnlineDecksLoader(Settings.serverAdress, Settings.serverRootPath,getContext().getCacheDir().getAbsolutePath(), galleryViewAdapter);
        onlineLoader.execute();
        glm = new GridLayoutManager(getContext(), 2);
        llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);


    }

    /**
     * This hook is called whenever an item in a context menu is selected. The
     * default implementation simply returns false to have the normal processing
     * happen (calling the item's Runnable or sending a message to its Handler
     * as appropriate). You can use this method for any items for which you
     * would like to do processing without those other facilities.
     * <p>
     * Use {@link MenuItem#getMenuInfo()} to get extra information set by the
     * View that added this menu item.
     * <p>
     * Derived classes should call through to the base class for it to perform
     * the default menu handling.
     *
     * @param item The context menu item that was selected.
     * @return boolean Return false to allow normal context menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (R.id.deleteDeckMenuItem==item.getItemId()){
            OfflineDeck offlineDeck = (OfflineDeck)item.getIntent().getExtras().get("offlinedeck");
            offlineDeck.removeFromFileSystem(this.getContext());
            galleryViewAdapter.getGalleryModel().remove(offlineDeck);
            new OnlineDecksLoader(Settings.serverAdress, Settings.serverRootPath,getContext().getCacheDir().getAbsolutePath(), galleryViewAdapter).execute();

        }else if(R.id.downloadDeckMenuItem==item.getItemId()){
                OnlineDeck onlineDeck = (OnlineDeck)item.getIntent().getExtras().get("onlinedeck");
                galleryViewAdapter.showDownloadAlertDialog(onlineDeck,galleryViewAdapter,this.getView());
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_gallery, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setEnabled(true);
        if (loader!=null&&loader.getStatus()== AsyncTask.Status.RUNNING){
            swipeRefreshLayout.setRefreshing(true);
        }else if(onlineLoader!=null &&onlineLoader.getStatus()== AsyncTask.Status.RUNNING){
            swipeRefreshLayout.setRefreshing(true);
        }
        galleryViewAdapter.setRefreshLayout(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                galleryViewAdapter.setRefreshLayout(swipeRefreshLayout);
                galleryViewAdapter.setGalleryModel(new GalleryModel());
                new OnlineDecksLoader(Settings.serverAdress, Settings.serverRootPath,getContext().getCacheDir().getAbsolutePath(), galleryViewAdapter).execute();
                new LocalDecksLoader(getContext().getFilesDir() + Settings.localFolder, galleryViewAdapter).execute();
            }
        });
        recList = (RecyclerView) rootView.findViewById(R.id.recycler_gallery_list);
        recList.setHasFixedSize(true);

        recList.setLayoutManager(llm);
        recList.setAdapter(galleryViewAdapter);
        registerForContextMenu(recList);


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link Activity#onCreateOptionsMenu(Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_menu, menu);
        if (recList.getLayoutManager().equals(llm)) {
            menu.findItem(R.id.gridLayoutButton).setVisible(true);
        } else {
            menu.findItem(R.id.listLayoutButton).setVisible(true);
        }
        this.menu = menu;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.listLayoutButton) {
            recList.setLayoutManager(llm);
            galleryViewAdapter = new GalleryViewAdapter(getContext(), galleryViewAdapter.getGalleryModel(), GalleryViewAdapter.LISTLAYOUT);
            recList.setAdapter(galleryViewAdapter);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.gridLayoutButton);
            item2.setVisible(true);
        } else if (item.getItemId() == R.id.gridLayoutButton) {
            recList.setLayoutManager(glm);
            galleryViewAdapter = new GalleryViewAdapter(getContext(), galleryViewAdapter.getGalleryModel(), GalleryViewAdapter.GRIDLAYOUT);
            recList.setAdapter(galleryViewAdapter);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.listLayoutButton);
            item2.setVisible(true);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
