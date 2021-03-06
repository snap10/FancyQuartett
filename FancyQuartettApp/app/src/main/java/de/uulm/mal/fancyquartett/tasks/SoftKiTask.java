package de.uulm.mal.fancyquartett.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.uulm.mal.fancyquartett.adapters.CardAttrViewAdapter;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import layout.CardFragment;

/**
 * Created by Lukas on 13.01.2016.
 */
public class SoftKiTask extends AsyncTask<Void, Void, CardAttribute> {

    private final long DELAY = 2500;

    private CardFragment.OnFragmentInteractionListener listener;
    private Card card;
    private boolean isNormalMode;

    /**
     *
     * @param card
     * @param listener
     */
    public SoftKiTask(Card card, CardFragment.OnFragmentInteractionListener listener, boolean isNormalMode) {
        this.card = card;
        this.listener = listener;
        this.isNormalMode = isNormalMode;
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
        if(isCancelled()) return null;
        if(isNormalMode) {
            return selectNormalMode();
        } else {
            return selectPointMode();
        }

    }

    /**
     * Selects 1 of 2 worst attributes based on difference between median and value.
     * @return
     */
    private CardAttribute selectNormalMode(){
        ArrayList<CardAttribute> unsortedAttrList = card.getAttributes();
        // start ki selecting
        HashMap<Double, CardAttribute> helperMap = new HashMap<Double, CardAttribute>();
        ArrayList<Double> diffList = new ArrayList<Double>();
        // fill helperMap and diffList
        for(CardAttribute cardAttribute : unsortedAttrList) {
            // get median and value
            double median = cardAttribute.getProperty().getMedian();
            double value = cardAttribute.getValue();
            // calculate difference
            double difference = 100 * ( (median - value) / median );
            if(difference < 0) difference *= (-1);
            // add to helperMap / diffList
            helperMap.put(difference, cardAttribute);
            diffList.add(difference);
        }
        // sort diffList
        Collections.sort(diffList);
        // fill sortedMap
        ArrayList<CardAttribute> sortedAttrList = new ArrayList<CardAttribute>();
        for(double difference : diffList) {
            sortedAttrList.add(helperMap.get(difference));
        }
        // select 1 of 2 worst attributes
        if(Math.random() < 0.5) {
            return sortedAttrList.get(0);
        } else {
            return sortedAttrList.get(1);
        }
    }

    private CardAttribute selectPointMode(){
        ArrayList<CardAttribute> unsortedAttrList = card.getAttributes();
        HashMap<Integer, CardAttribute> map = new HashMap<Integer, CardAttribute>();
        // fill map
        for(CardAttribute cardAttribute : unsortedAttrList) {
            map.put(card.getPoints(cardAttribute), cardAttribute);
        }
        // select 5 or 2 point attribute
        if(Math.random() < 0.5) {
            if(map.containsKey(2)) return map.get(2);
            if(map.containsKey(5)) return map.get(5);
        } else {
            if(map.containsKey(5)) return map.get(5);
            if(map.containsKey(2)) return map.get(2);
        }
        return map.get(1);
    }
}
