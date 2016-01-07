package layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.GalleryViewAdapter;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.LocalDecksLoader;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Menu menu;

    public GalleryFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        //Initialize Data
        galleryViewAdapter = new GalleryViewAdapter(getContext());
        LocalDecksLoader loader = new LocalDecksLoader(Settings.localAssets,getContext(),galleryViewAdapter);
        loader.execute();

        glm= new GridLayoutManager(getContext(),2);
        llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_gallery, container, false);
        //Create a GallerViewAdapter for the RecyclerList

        recList = (RecyclerView) rootView.findViewById(R.id.recycler_gallery_list);
        recList.setHasFixedSize(true);

        recList.setLayoutManager(llm);
        recList.setAdapter(galleryViewAdapter);


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
        this.menu=menu;
        if (recList.getLayoutManager().equals(llm)){
            menu.findItem(R.id.gridLayoutButton).setVisible(true);
        }else{
            menu.findItem(R.id.listLayoutButton).setVisible(true);
        }
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
            galleryViewAdapter = new GalleryViewAdapter(getContext(), galleryViewAdapter.getGalleryModel(), GalleryViewAdapter.LISTLAYOUT);
            recList.setAdapter(galleryViewAdapter);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.gridLayoutButton);
            item2.setVisible(true);
        }else if(item.getItemId()==R.id.gridLayoutButton){
            recList.setLayoutManager(glm);
            galleryViewAdapter = new GalleryViewAdapter(getContext(),galleryViewAdapter.getGalleryModel(),GalleryViewAdapter.GRIDLAYOUT);
            recList.setAdapter(galleryViewAdapter);
            item.setVisible(false);
            MenuItem item2=menu.findItem(R.id.listLayoutButton);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
