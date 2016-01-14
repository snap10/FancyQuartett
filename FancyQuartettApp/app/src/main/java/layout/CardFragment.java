package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.CardAttrViewAdapter;
import de.uulm.mal.fancyquartett.adapters.CardImagesPagerAdapter;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;


/**
 * Created by Lukas on 09.01.2016.
 */
public class CardFragment extends Fragment implements LocalDeckLoader.OnLocalDeckLoadedListener, CardAttrViewAdapter.OnCardAttrClickListener {

    private static final String ARG_CARDID = "card_id";
    private static final String ARG_CARD = "card";
    private static final String ARG_DECKID = "deck_name";

    // view attributes
    private RecyclerView recList;
    private GridLayoutManager glm;
    private TextView cardName;

    // other attributes
    private OfflineDeck offlineDeck;
    private Card card;
    private int cardID;
    private int deckID;
    private View cardFragmentView;
    private OnFragmentInteractionListener mListener;

    public CardFragment() {
        // required empty public constructor
    }

    /**
     * Creates a new instance of this fragment.
     *
     * @return
     */
    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new instance of this fragment using the provided prameters.
     *
     * @param card
     * @return
     */
    public static CardFragment newInstance(Card card) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD, card);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new instance of this fragment using the provided prameters.
     *
     * @param cardId
     * @param deckid
     *
     * @return
     */
    public static CardFragment newInstance(int cardId, int deckid) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CARDID, cardId);
        args.putInt(ARG_DECKID, deckid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardID = getArguments().getInt(ARG_CARDID);
            card = (Card) getArguments().getSerializable(ARG_CARD);
            deckID = getArguments().getInt(ARG_DECKID);
        }
        if (card != null) {
            cardID = card.getID();
            deckID = card.getDeckID();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        cardFragmentView = inflater.inflate(R.layout.fragment_card, container, false);
        // set cardName
        cardName = (TextView) cardFragmentView.findViewById(R.id.textView_CardName);
        cardName.setText(card.getName());
        // initialise RecyclerView
        recList = (RecyclerView) cardFragmentView.findViewById(R.id.recycler_card_attributes);
        recList.setHasFixedSize(true);
        // create GridLayoutManager for RecyclerView
        glm = new GridLayoutManager(getContext(), 2);
        if (recList.getLayoutManager() == null) {
            recList.setLayoutManager(glm);

        }
        // create cardAttrViewAdapter
        CardAttrViewAdapter cardAttrViewAdapter = new CardAttrViewAdapter(getContext(), card.getAttributeList(), this);
        recList.setAdapter(cardAttrViewAdapter);
        // create ViewPager
        ViewPager viewPager = (ViewPager) cardFragmentView.findViewById(R.id.viewPager_SlideShow);
        // providing the ViewPager with the ImageViews
        CardImagesPagerAdapter pagerAdapter = new CardImagesPagerAdapter(cardFragmentView.getContext(), card);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        cardAttrViewAdapter.setCard(card);

        return cardFragmentView;
    }

    /**
     * Callback Method for LocalDeckLoader
     * Does collect the Images of the Card and setup the View of the Card
     *
     * @param offlineDeck
     */
    @Override
    public void onDeckLoaded(OfflineDeck offlineDeck) {
        if (offlineDeck == null) {
            Toast.makeText(getContext(), "Error while loading Card", Toast.LENGTH_SHORT);
        }
        this.offlineDeck = offlineDeck;
        this.card = offlineDeck.getCards().get(cardID);
        // create cardAttrViewAdapter
        CardAttrViewAdapter cardAttrViewAdapter = new CardAttrViewAdapter(getContext(), card.getAttributeList(), this);
        recList.setAdapter(cardAttrViewAdapter);
        // create ViewPager
        ViewPager viewPager = (ViewPager) cardFragmentView.findViewById(R.id.viewPager_SlideShow);
        // providing the ViewPager with the ImageViews
        CardImagesPagerAdapter pagerAdapter = new CardImagesPagerAdapter(cardFragmentView.getContext(), card);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        cardAttrViewAdapter.setCard(card);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement layout.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * If a card attribute is clicked, the Listeners are informed with parameters
     *
     * @param property
     * @param value
     * @param attribute
     */
    @Override
    public void onCardAttrClicked(Property property, double value, CardAttribute attribute) {
        mListener.onCardFragmentAttributeInteraction(property, value, attribute);
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
        /**
         * If a card attribute is clicked, the event is routed to the Listeners of this Fragment
         *
         * @param property
         * @param value
         * @param cardAttribute
         */
        void onCardFragmentAttributeInteraction(Property property, double value, CardAttribute cardAttribute);
    }
}
