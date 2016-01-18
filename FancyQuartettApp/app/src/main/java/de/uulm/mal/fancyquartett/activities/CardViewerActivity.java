package de.uulm.mal.fancyquartett.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.Property;
import layout.CardFragment;
import layout.CardViewerFragment;

public class CardViewerActivity extends AppCompatActivity implements CardFragment.OnFragmentInteractionListener, CardViewerFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_viewer);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.card_viewer_fragment_container , CardViewerFragment.newInstance(getIntent().getExtras())).commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * If a card attribute is clicked, the event is routed to the Listeners of this Fragment
     *
     * @param property
     * @param value
     * @param cardAttribute
     */
    @Override
    public void onCardFragmentAttributeInteraction(Property property, double value, CardAttribute cardAttribute) {

    }

    @Override
    public void lockInteraction(boolean lock) {

    }
}
