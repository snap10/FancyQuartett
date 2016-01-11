package layout;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardViewerFragment extends Fragment{

    private int cardnumber = 0;
    private int decksize;
    private String deckname;
    private OfflineDeck offlineDeck;

    public CardViewerFragment() {
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
        if (getArguments() != null) {
            cardnumber = getArguments().getInt("cardnumber");
            offlineDeck = (OfflineDeck)getArguments().getSerializable("offlineDeck");
            if (offlineDeck!=null) {
                decksize = offlineDeck.getCards().size();
                deckname = offlineDeck.getName();
            }else{
                decksize = getArguments().getInt("decksize");
                deckname = getArguments().getString("deckname");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_card_viewer, container, false);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.card_pager);
        CardPagerAdapter pagerAdapter = new CardPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        return v;
    }


    public static CardViewerFragment newInstance() {
        CardViewerFragment fragment = new CardViewerFragment();
        return fragment;
    }

    public static CardViewerFragment newInstance(Bundle bundle) {
        CardViewerFragment fragment = new CardViewerFragment();
        Bundle args = bundle;
        fragment.setArguments(args);
        return fragment;
    }



    private class CardPagerAdapter extends FragmentPagerAdapter {
        LocalDeckLoader loader;
    //TODO solve Problem when scrolling backwards
        public CardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the Fragment associated with a specified position.
         *
         * @param position
         */
        @Override
        public CardFragment getItem(int position) {
            CardFragment fragment;
            if (offlineDeck==null){
                fragment= CardFragment.newInstance(cardnumber,deckname);
                loader = new LocalDeckLoader(getContext().getFilesDir() + Settings.localFolder, deckname.toLowerCase(), fragment);
                loader.execute();
                //get CardFragment showing the position-th Card of the Deck
            }else{
                fragment = CardFragment.newInstance(offlineDeck.getCards().get(position));

            }
            return fragment;
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return decksize;
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


}
