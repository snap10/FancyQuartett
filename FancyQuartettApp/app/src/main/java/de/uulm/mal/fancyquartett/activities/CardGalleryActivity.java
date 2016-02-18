package de.uulm.mal.fancyquartett.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import layout.CardGalleryFragment;
import android.support.v7.app.ActionBar;

public class CardGalleryActivity extends AppCompatActivity implements CardGalleryFragment.OnFragmentInteractionListener {
    String deckname;
    private OfflineDeck offlineDeck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_gallery);
        if (getIntent().getExtras() != null) {
            offlineDeck = (OfflineDeck) getIntent().getSerializableExtra("offlinedeck");
            deckname = offlineDeck.getName();
        } else {
            finish();
        }
        CardGalleryFragment fragment = CardGalleryFragment.newInstance(getIntent().getExtras());
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.card_gallery_relativelayout, fragment, "cardgalleryfragment");
        transaction.commit();
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        if(offlineDeck != null) {
            actionbar.setTitle(offlineDeck.getName());
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }


}
