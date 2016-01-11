package de.uulm.mal.fancyquartett.activities;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.security.Timestamp;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.GameEngine;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;
import layout.CardFragment;


public class GameActivity extends AppCompatActivity implements LocalDeckLoader.OnLocalDeckLoadedListener{

    private GameEngine engine;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        rootView = findViewById(R.id.linLayout_Container);

        // load gameDeck
        String deckname = getIntent().getExtras().getString("deckname");
        new LocalDeckLoader(getFilesDir()+ Settings.localFolder,deckname.toLowerCase(),this).execute();


    }

    /**
     * Callback Method for LocalDeckLoader
     *
     * @param offlineDeck
     */
    @Override
    public void onDeckLoaded(OfflineDeck offlineDeck) {
        // create gameEngine
        engine = new GameEngine(getApplicationContext(),rootView, offlineDeck);
        // create cardFragment
        CardFragment fragment = CardFragment.newInstance(offlineDeck.getCards().get(0));
        getSupportFragmentManager().beginTransaction().add(R.id.linLayout_Container,fragment).commit();
    }
}
