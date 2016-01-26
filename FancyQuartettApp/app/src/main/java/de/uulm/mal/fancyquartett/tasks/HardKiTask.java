package de.uulm.mal.fancyquartett.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import layout.CardFragment;

/**
 * Created by Lukas on 25.01.2016.
 */
public class HardKiTask extends AsyncTask<Void, Void, CardAttribute> {

    private final long DELAY = 2500;

    private CardFragment.OnFragmentInteractionListener listener;
    private Card computersCard;
    private Card playersCard;
    private boolean isNormalMode;

    public HardKiTask(Card computersCard, Card playersCard, CardFragment.OnFragmentInteractionListener listener, boolean isNormalMode) {
        this.computersCard = computersCard;
        this.playersCard = playersCard;
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
        ArrayList<CardAttribute> unsortedAttrList = computersCard.getAttributes();
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
        // select 1 of 2 best attributes
        if(Math.random() < 0.5) {
            return sortedAttrList.get(sortedAttrList.size()-1);
        } else {
            return sortedAttrList.get(sortedAttrList.size()-2);
        }
    }

    private CardAttribute selectPointMode(){

        // Variant 1: Spy out player's card and check if there is a 5-points-attribute that would win
        CardAttribute fivePointsWinnerAttr = null;
        for(CardAttribute playerattr : playersCard.getAttributes()) {
            for(CardAttribute computerattr : computersCard.getAttributes()) {
                boolean biggerWins = playerattr.getProperty().biggerWins();
                boolean computerIsBigger = computerattr.getValue() > playerattr.getValue();
                if((computerIsBigger && biggerWins) || (!computerIsBigger && !biggerWins)) {
                    // comptuer would win
                    if(computersCard.getPoints(computerattr) == 5) {
                        fivePointsWinnerAttr = computerattr;
                        System.out.println("AI found 5-points-attribute that would win!");
                        break;
                    }
                } else {
                    // player would win
                }
            }
            break;
        }
        if(fivePointsWinnerAttr == null) System.out.println("AI found no 5-points-attribute that would win..");
        // add a randomness parameter to make computer weaker
        //TODO test out best value
        boolean random = Math.random() < 0.5; // this is a fifty-fifty chance
        if(fivePointsWinnerAttr != null && random) return fivePointsWinnerAttr;
        else return selectNormalMode();

        // Variant 2: Just search for a 5-or-2-points-attribute. Loses too often.
        /*
        ArrayList<CardAttribute> unsortedAttrList = card.getAttributes();
        HashMap<Boolean, CardAttribute> checkMap = new HashMap<Boolean, CardAttribute>();
        // fill map(s)
        for(CardAttribute cardAttribute : unsortedAttrList) {
            // fill checkMap
            int points = computersCard.getPoints(cardAttribute);
            if(points == 5 || points == 2) {
                // check if good 5 or 2 point attribute exists
                System.out.println("found "+points+" points attribute");
                System.out.println("with value "+cardAttribute.getValue()+" and median "+cardAttribute.getMedian());
                double median = cardAttribute.getMedian();
                double value = cardAttribute.getValue();
                boolean isHigherWins = cardAttribute.isHigherWins();
                if(isHigherWins) {
                    if(median < value) checkMap.put(true, cardAttribute);
                    else checkMap.put(false, cardAttribute);
                } else {
                    if(median > value) checkMap.put(true, cardAttribute);
                    else checkMap.put(false, cardAttribute);
                }
            } else {
                checkMap.put(false, cardAttribute);
            }
        }
        // check if good 5 point attribute is available
        if(checkMap.containsKey(true)) return checkMap.get(true);
        else return selectNormalMode();
        */
    }
}
