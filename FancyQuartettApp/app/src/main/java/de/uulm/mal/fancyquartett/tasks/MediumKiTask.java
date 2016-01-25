package de.uulm.mal.fancyquartett.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;

import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import layout.CardFragment;

/**
 * Created by Lukas on 25.01.2016.
 */
public class MediumKiTask extends AsyncTask<Void, Void, CardAttribute> {

    private final long DELAY = 2500;

    private CardFragment.OnFragmentInteractionListener listener;
    private Card card;
    private boolean isNormalMode;

    /**
     *
     * @param card
     * @param listener
     */
    public MediumKiTask(Card card, CardFragment.OnFragmentInteractionListener listener, boolean isNormalMode) {
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
     * Selects 1 random attribute.
     * @return
     */
    private CardAttribute selectNormalMode() {
        ArrayList<CardAttribute> attrList = card.getAttributes();
        int i = (int)((Math.random()) * (attrList.size()-1) + 0);
        System.out.println(i);
        return attrList.get(i);
    }

    private CardAttribute selectPointMode() {
        return selectNormalMode();
    }
}
