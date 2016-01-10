package layout;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.CardImagesPagerAdapter;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.Image;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardViewerDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CardViewerDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardViewerDetailFragment extends Fragment implements LocalDeckLoader.OnLocalDeckLoadedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private int cardnumber;
    private String deckname;
    private Card card;
    private View cardViewerDetailFragmentView;

    public CardViewerDetailFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cardnumber Parameter 1.
     * @param deckname   Parameter 2.
     * @return A new instance of fragment CardViewerDetailFragment.
     */
    public static CardViewerDetailFragment newInstance(int cardnumber, String deckname) {
        CardViewerDetailFragment fragment = new CardViewerDetailFragment();
        Bundle args = new Bundle();
        args.putInt("cardnumber", cardnumber);
        args.putString("deckname", deckname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardnumber = getArguments().getInt("cardnumber");
            deckname = getArguments().getString("deckname");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cardViewerDetailFragmentView = inflater.inflate(R.layout.fragment_card_viewer_detail, container, false);
        return cardViewerDetailFragmentView;
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
     * Doas collect the Images of the Card and setup the View of the Card
     *
     * @param offlineDeck
     */
    @Override
    public void onDeckLoaded(OfflineDeck offlineDeck) {
        if (offlineDeck==null){
            Toast.makeText(getContext(),"Error while loading Card",Toast.LENGTH_SHORT);
        }
        card = offlineDeck.getCards().get(cardnumber);
        ViewPager viewPager = (ViewPager) cardViewerDetailFragmentView.findViewById(R.id.viewPager_SlideShow);
        //Providing the ViewPager with the ImageViews
        CardImagesPagerAdapter pagerAdapter= new CardImagesPagerAdapter(cardViewerDetailFragmentView.getContext(),card);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);


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
