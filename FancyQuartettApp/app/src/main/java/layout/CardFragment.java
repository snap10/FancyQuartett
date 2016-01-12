package layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.CardAttrViewAdapter;
import de.uulm.mal.fancyquartett.adapters.CardImagesPagerAdapter;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;

/**
 * Created by Lukas on 09.01.2016.
 */
public class CardFragment extends Fragment implements LocalDeckLoader.OnLocalDeckLoadedListener {

    private static final String ARG_CARDID = "card_id";
    private static final String ARG_CARD = "card";
    private static final String ARG_DECKNAME = "deck_name";

    // view attributes
    private RecyclerView recList;
    private GridLayoutManager glm;

    // other attributes
    private OfflineDeck offlineDeck;
    private Card card;
    private int cardID;
    private String deckname;
    private View cardFragmentView;

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
     * @param deckname
     * @return
     */
    public static CardFragment newInstance(int cardId, String deckname) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CARDID, cardId);
        args.putString(ARG_DECKNAME, deckname);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardID = getArguments().getInt(ARG_CARDID);
            card = (Card) getArguments().getSerializable(ARG_CARD);
            deckname = getArguments().getString(ARG_DECKNAME);
        }
        if (card != null) {
            cardID = card.getID();
            deckname = card.getDeckname();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // inflate the layout for this fragment
        cardFragmentView = inflater.inflate(R.layout.fragment_card, container, false);
        // initialise RecyclerView
        recList = (RecyclerView) cardFragmentView.findViewById(R.id.recycler_card_attributes);
        recList.setHasFixedSize(true);
        // create GridLayoutManager for RecyclerView
        glm = new GridLayoutManager(getContext(), 2);
        if (recList.getLayoutManager() == null) {
            recList.setLayoutManager(glm);

        }

        // create cardAttrViewAdapter
        CardAttrViewAdapter cardAttrViewAdapter = new CardAttrViewAdapter(getContext(), card.getAttributeList());
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
        CardAttrViewAdapter cardAttrViewAdapter = new CardAttrViewAdapter(getContext(), card.getAttributeList());
        recList.setAdapter(cardAttrViewAdapter);
        // create ViewPager
        ViewPager viewPager = (ViewPager) cardFragmentView.findViewById(R.id.viewPager_SlideShow);
        // providing the ViewPager with the ImageViews
        CardImagesPagerAdapter pagerAdapter = new CardImagesPagerAdapter(cardFragmentView.getContext(), card);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        cardAttrViewAdapter.setCard(card);
    }


    // TODO: load Card from offlineDeck and display it (cardId is in Bundle (args))
}
