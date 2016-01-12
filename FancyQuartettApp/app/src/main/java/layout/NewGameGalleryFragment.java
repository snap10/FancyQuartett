package layout;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import de.uulm.mal.fancyquartett.adapters.NewGameGalleryViewAdapter;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.LocalDecksLoader;
import de.uulm.mal.fancyquartett.utils.OnlineDecksLoader;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewGameGalleryFragment extends Fragment {

    private NewGameGalleryViewAdapter newGameGalleryViewAdapter;
    RecyclerView recList;
    LinearLayoutManager llm;
    GridLayoutManager glm;
    private Menu menu;

    public NewGameGalleryFragment() {
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //Initialize Data
        newGameGalleryViewAdapter = new NewGameGalleryViewAdapter(getActivity());
        LocalDecksLoader loader = new LocalDecksLoader(getContext().getFilesDir() + Settings.localFolder, newGameGalleryViewAdapter);
        loader.execute();

        new OnlineDecksLoader(Settings.serverAdress,Settings.serverDecklistJsonFilename,newGameGalleryViewAdapter).execute();

        glm = new GridLayoutManager(getContext(), 2);
        llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_gallery, container, false);
        recList = (RecyclerView) rootView.findViewById(R.id.recycler_gallery_list);
        recList.setHasFixedSize(true);

        recList.setLayoutManager(llm);
        recList.setAdapter(newGameGalleryViewAdapter);


        return rootView;
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
        menu.clear();
        this.menu = menu;
        inflater.inflate(R.menu.menu_deck_gallery, menu);
        if (menu != null) {
            if (recList.getLayoutManager().equals(llm)) {
                menu.findItem(R.id.gridLayoutButton).setVisible(true);
            } else {
                menu.findItem(R.id.listLayoutButton).setVisible(true);
            }
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

        if (item.getItemId() == R.id.listLayoutButton) {
            recList.setLayoutManager(llm);
            newGameGalleryViewAdapter = new NewGameGalleryViewAdapter(getActivity(), newGameGalleryViewAdapter.getGalleryModel(), GalleryViewAdapter.LISTLAYOUT);
            recList.setAdapter(newGameGalleryViewAdapter);
            item.setVisible(false);
            MenuItem item2 = menu.findItem(R.id.gridLayoutButton);
            item2.setVisible(true);
        } else if (item.getItemId() == R.id.gridLayoutButton) {
            recList.setLayoutManager(glm);
            newGameGalleryViewAdapter = new NewGameGalleryViewAdapter(getActivity(), newGameGalleryViewAdapter.getGalleryModel(), GalleryViewAdapter.GRIDLAYOUT);
            recList.setAdapter(newGameGalleryViewAdapter);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
