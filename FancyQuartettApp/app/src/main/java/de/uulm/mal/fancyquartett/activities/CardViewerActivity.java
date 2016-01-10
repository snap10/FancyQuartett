package de.uulm.mal.fancyquartett.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.uulm.mal.fancyquartett.R;
import layout.CardViewerFragment;
import layout.CardGalleryFragment;
import layout.CardViewerDetailFragment;

public class CardViewerActivity extends AppCompatActivity implements  CardViewerFragment.OnFragmentInteractionListener, CardViewerDetailFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_viewer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.card_viewer_fragment_container , CardViewerFragment.newInstance(getIntent().getExtras())).commit();
        }

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
