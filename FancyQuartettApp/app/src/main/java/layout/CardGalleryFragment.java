package layout;

import android.content.Context;
import android.net.Uri;
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
import android.widget.Toast;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.CardGalleryViewAdapter;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;
import de.uulm.mal.fancyquartett.utils.LocalDecksLoader;
import de.uulm.mal.fancyquartett.utils.OnlineDecksLoader;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardGalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CardGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardGalleryFragment extends Fragment implements LocalDeckLoader.OnLocalDeckLoadedListener {

    RecyclerView recList;
    LinearLayoutManager llm;
    GridLayoutManager glm;

    private static final String ARG_PARAM1 = "deckname";
    private static final String ARG_PARAM2 = "offlinedeck";

    private OnFragmentInteractionListener mListener;
    private OfflineDeck offlineDeck;
    private Menu menu;
    private CardGalleryViewAdapter cardGalleryViewAdapter;

    public CardGalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param deckname
     * @return A new instance of fragment CardGalleryFragment.
     */
    public static CardGalleryFragment newInstance(String deckname) {
        CardGalleryFragment fragment = new CardGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, deckname);
        fragment.setArguments(args);
        return fragment;
    }

    public static CardGalleryFragment newInstance(Bundle bundle){
        CardGalleryFragment fragment = new CardGalleryFragment();
        Bundle args = bundle;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offlineDeck = (OfflineDeck)getArguments().getSerializable(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        glm= new GridLayoutManager(getContext(),2);
        llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_gallery, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefreshlayout);
        swipeRefreshLayout.setEnabled(false);
        recList = (RecyclerView) rootView.findViewById(R.id.recycler_gallery_list);
        recList.setHasFixedSize(true);
        recList.setLayoutManager(llm);
        if (offlineDeck!=null){
            cardGalleryViewAdapter = new CardGalleryViewAdapter(getContext().getApplicationContext(), CardGalleryViewAdapter.LISTLAYOUT);
            cardGalleryViewAdapter.setOfflineDeck(offlineDeck);
            recList.setAdapter(cardGalleryViewAdapter);

        }


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * Callback Method for LocalDeckLoader
     *
     * @param offlineDeck
     */
    @Override
    public void onDeckLoaded(OfflineDeck offlineDeck) {
        if (offlineDeck==null){
            Toast.makeText(this.getContext(),"Deck could not be loaded",Toast.LENGTH_LONG).show();
        }else{
            this.offlineDeck = offlineDeck;
            cardGalleryViewAdapter = new CardGalleryViewAdapter(getContext().getApplicationContext(), CardGalleryViewAdapter.LISTLAYOUT);
            cardGalleryViewAdapter.setOfflineDeck(offlineDeck);
            recList.setAdapter(cardGalleryViewAdapter);

        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

        if (item.getItemId()==R.id.listLayoutButton) {
            recList.setLayoutManager(llm);
            cardGalleryViewAdapter = new CardGalleryViewAdapter(getContext().getApplicationContext(),offlineDeck, CardGalleryViewAdapter.LISTLAYOUT);
            recList.setAdapter(cardGalleryViewAdapter);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.gridLayoutButton);
            item2.setVisible(true);
        }else if(item.getItemId()==R.id.gridLayoutButton){
            recList.setLayoutManager(glm);
            cardGalleryViewAdapter = new CardGalleryViewAdapter(getContext().getApplicationContext(),offlineDeck, CardGalleryViewAdapter.GRIDLAYOUT);
            recList.setAdapter(cardGalleryViewAdapter);
            item.setVisible(false);
            MenuItem item2=menu.findItem(R.id.listLayoutButton);
            item2.setVisible(true);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
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
        menu.clear();
        this.menu=menu;
        inflater.inflate(R.menu.gallery_menu,menu);
    }
}
