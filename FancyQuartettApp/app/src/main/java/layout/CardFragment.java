package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.CardAttrViewAdapter;
import de.uulm.mal.fancyquartett.adapters.CardImagesPagerAdapter;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.utils.GameEngine;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;


/**
 * Created by Lukas on 09.01.2016.
 */
public class CardFragment extends Fragment implements LocalDeckLoader.OnLocalDeckLoadedListener, CardAttrViewAdapter.OnCardAttrClickListener {

    private static final String ARG_CARDID = "card_id";
    private static final String ARG_CARD = "card";
    private static final String ARG_DECKID = "deck_name";
    private static final String ARG_ISCLICKABLE = "is_clickable";
    private static final String ARG_ISUSEDINPOINTMODE = "is_used_in_point_mode";

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
    private CardAttrViewAdapter cardAttrViewAdapter;
    private OnFragmentInteractionListener mListener;
    private boolean isClickable;
    private boolean isUsedInPointMode;

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
    public static CardFragment newInstance(Card card, boolean isClickable) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD, card);
        args.putBoolean(ARG_ISCLICKABLE, isClickable);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new instance of this fragment using the provided prameters.
     * @param card
     * @param isClickable
     * @param isUsedInPointMode
     * @return
     */
    public static CardFragment newInstance(Card card, boolean isClickable, boolean isUsedInPointMode) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD, card);
        args.putBoolean(ARG_ISCLICKABLE, isClickable);
        args.putBoolean(ARG_ISUSEDINPOINTMODE, isUsedInPointMode);
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
    public static CardFragment newInstance(int cardId, int deckid, boolean isClickable) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CARDID, cardId);
        args.putInt(ARG_DECKID, deckid);
        args.putBoolean(ARG_ISCLICKABLE, isClickable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            cardID = args.getInt(ARG_CARDID);
            card = (Card) args.getSerializable(ARG_CARD);
            deckID = args.getInt(ARG_DECKID);
            isClickable = args.getBoolean(ARG_ISCLICKABLE);
            isUsedInPointMode = args.getBoolean(ARG_ISUSEDINPOINTMODE);
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
        if(isUsedInPointMode) {
            cardAttrViewAdapter = new CardAttrViewAdapter(getContext(), card.getAttributes(), this, isUsedInPointMode);
        }
        else {
            cardAttrViewAdapter = new CardAttrViewAdapter(getContext(), card.getAttributes(), this);
        }
        recList.setAdapter(cardAttrViewAdapter);
        // check if clickable
        if(!isClickable) disableCardAttrClick();
        else enableCardAttrClick();
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
     * Enables click event on all card attributes.
     * Returns true if events could be activated.
     * Returns false if events could not be activated.
     * @return
     */
    public boolean enableCardAttrClick() {
        if(cardAttrViewAdapter != null) {
            cardAttrViewAdapter.enableClicks();
            return true;
        }
        return false;
    }

    /**
     * Disables click event on all card attributes.
     * Returns true if events could be disabled.
     * Returns false if events could not be disabled.
     * @return
     */
    public boolean disableCardAttrClick() {
        if(cardAttrViewAdapter != null) {
            cardAttrViewAdapter.disableClicks();
            return true;
        }
        return false;
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
        CardAttrViewAdapter cardAttrViewAdapter = new CardAttrViewAdapter(getContext(), card.getAttributes(), this);
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
