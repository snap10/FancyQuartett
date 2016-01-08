package de.uulm.mal.fancyquartett.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.adapters.DeckGalleryViewAdapter;

public class DeckGalleryActivity extends AppCompatActivity implements DeckGalleryActivityFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_gallery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }

}
