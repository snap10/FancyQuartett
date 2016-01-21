package de.uulm.mal.fancyquartett.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;

import de.uulm.mal.fancyquartett.adapters.CardAttrViewAdapter;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import layout.CardFragment;

/**
 * Created by Lukas on 13.01.2016.
 */
public class SoftKiTask extends AsyncTask<Void, Void, CardAttribute> {

    private final long DELAY = 5000;

    private CardFragment.OnFragmentInteractionListener listener;
    private Card card;

    /**
     *
     * @param card
     * @param listener
     */
    public SoftKiTask(Card card, CardFragment.OnFragmentInteractionListener listener) {
        this.card = card;
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(CardAttribute cardAttribute) {
        if(cardAttribute == null) return;
        listener.onCardFragmentAttributeInteraction(cardAttribute.getProperty(), cardAttribute.getValue(), cardAttribute);
    }

    @Override
    protected CardAttribute doInBackground(Void... params) {
        if(isCancelled()) return null;
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace(); // flies when user left game during delay
        }
        ArrayList<CardAttribute> attrList = card.getAttributes();
        CardAttribute cardAttribute = null;
        double difference = 0.0f;
        for(int i=0; i<attrList.size(); i++) {
            CardAttribute ca = attrList.get(i);
            double median = ca.getProperty().getMedian();
            double value = ca.getValue();
            // calculate difference
            double diffTemp = 100 * ( (median - value) / median );
            // check if diffTemp > difference
            //TODO what value has difference?????
            //TODO please consider biggerWins==false, therefore use Math.abs() to have the absolute Value
            if(diffTemp > difference && diffTemp > 0) {
                cardAttribute = ca;

            }
            // check if cardAttribute was found
            if(cardAttribute == null) {
                // select random cardAttribute if no cardAttribute was found
                int indexRandom = (int)((Math.random()) * (attrList.size()-1) + 0);
                cardAttribute = attrList.get(indexRandom);
            }
        }
        return cardAttribute;
    }
}
