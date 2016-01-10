package layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.CardAttrViewAdapter;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;

/**
 * Created by Lukas on 09.01.2016.
 */
public class CardFragment extends Fragment implements LocalDeckLoader.OnLocalDeckLoadedListener {

    private static final String ARG_CARDID = "cardId";

    // view attributes
    private RecyclerView recList;
    private GridLayoutManager glm;
    private CardAttrViewAdapter cardAttrViewAdapter;

    // other attributes
    OfflineDeck offlineDeck;
    Card card;

    public CardFragment() {
        // required empty public constructor
    }

    /**
     * Creates a new instance of this fragment.
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
     * @param cardId
     * @return
     */
    public static CardFragment newInstance(int cardId) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CARDID, cardId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create GridLayoutManager for RecyclerView
        glm = new GridLayoutManager(getContext(),2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);
        // initialise RecyclerView
        recList = (RecyclerView) rootView.findViewById(R.id.recycler_card_attributes);
        recList.setHasFixedSize(true);
        recList.setLayoutManager(glm);

        // TODO: delete tabbed lines (they're only temporary)
            cardAttrViewAdapter = new CardAttrViewAdapter(getContext(), null);
            recList.setAdapter(cardAttrViewAdapter);

        return rootView;
    }

    @Override
    public void onDeckLoaded(OfflineDeck offlineDeck) {
        if(offlineDeck == null) {
            Toast.makeText(this.getContext(), "Deck could not be loaded", Toast.LENGTH_LONG).show();
        } else {
            this.offlineDeck = offlineDeck;
            // TODO: some cardAttrViewAdapter stuff
        }
    }

    // TODO: load Card from offlineDeck and display it (cardId is in Bundle (args))
}
