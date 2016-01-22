package de.uulm.mal.fancyquartett.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.controller.CardController;
import de.uulm.mal.fancyquartett.controller.PlayerController;
import de.uulm.mal.fancyquartett.controller.StatisticController;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.dialog.GameEndDialog;
import de.uulm.mal.fancyquartett.dialog.KiPlaysDialog;
import de.uulm.mal.fancyquartett.dialog.RoundEndDialog;
import de.uulm.mal.fancyquartett.enums.GameMode;
import de.uulm.mal.fancyquartett.enums.KILevel;
import de.uulm.mal.fancyquartett.interfaces.OnDialogButtonClickListener;
import de.uulm.mal.fancyquartett.interfaces.OnGameTimeUpdateListener;
import de.uulm.mal.fancyquartett.tasks.GameTimeTask;
import de.uulm.mal.fancyquartett.tasks.SoftKiTask;
import de.uulm.mal.fancyquartett.tasks.PlayerTimeOutTask;
import de.uulm.mal.fancyquartett.utils.GameEngine;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;
import layout.CardFragment;


public class GameActivity extends AppCompatActivity implements CardFragment.OnFragmentInteractionListener, LocalDeckLoader.OnLocalDeckLoadedListener {

    private Menu menu;

    // game components
    private GameEngine engine;
    private OfflineDeck offlineDeck;
    private Bundle args;

    // bundle settings
    private GameMode bundleGameMode;
    private KILevel bundleKILevel;
    private int bundleRoundTimeout;
    private int bundleMaxRounds;
    private int bundleGameTime;
    private int bundleGamePoints;
    private boolean bundleIsMultiplayer;
    private String bundleP1Name;
    private String bundleP2Name;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // get rootView
        rootView = findViewById(R.id.gameContainer);

        // disable device display-timeout in this activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // build toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gameActivity_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // check Bundle
        args = getIntent().getExtras();
        offlineDeck = (OfflineDeck) args.getSerializable("offlinedeck");
        if (offlineDeck != null) {
            engine = new GameEngine(this, getApplicationContext(), rootView, args);
           // engine.startGame();
        } else {
            SharedPreferences prefs = getSharedPreferences("savedGame", Context.MODE_PRIVATE);
            if (prefs.getBoolean("savedAvailable", false)) {
                Gson gson = new Gson();
                String json = prefs.getString("savedEngine", null);
                engine = gson.fromJson(json, GameEngine.class);
            }if(engine!=null){
                // start engine from saved game
                engine.setGameActivity(this);
                engine.setRootView(rootView);
                engine.setContext(getApplicationContext());
                engine.setFragmentManager(getSupportFragmentManager());
                //engine.startGame();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
            case R.id.action_rules:
                // TODO: show Dialog with Rules
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setTitle("Closing Game")
                .setMessage("Are you sure you want to close the game? This game will be saved after closing.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        engine.stop();
                        SharedPreferences prefs = getSharedPreferences("savedGame", Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = gson.toJson(engine);
                        prefs.edit().putString("savedEngine", json).putBoolean("savedAvailable", true).commit();
                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // calls onStop too
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onStop() {
        engine.stop();
        SharedPreferences prefs = getSharedPreferences("savedGame", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(engine);
        prefs.edit().putString("savedEngine", json).putBoolean("savedAvailable", true).commit();

        super.onStop();
    }


    @Override
    protected void onPostResume() {
        engine.setGameActivity(this);
        engine.setRootView(rootView);
        engine.setContext(getApplicationContext());
        engine.setFragmentManager(getSupportFragmentManager());
        engine.startGame();
        super.onPostResume();
    }

    /**
     * Callback Method for LocalDeckLoader
     *
     * @param offlineDeck
     */
    @Override
    public void onDeckLoaded(OfflineDeck offlineDeck) {
        // put deck to args
        args.putSerializable("offlinedeck", offlineDeck);
        // now start engine
        engine = new GameEngine(this, getApplicationContext(), rootView, args);
        engine.startGame();
    }


    /**
     * Callback for computer player thread
     *
     * @param property
     * @param value
     * @param cardAttribute
     */
    @Override
    public void onCardFragmentAttributeInteraction(Property property, double value, CardAttribute cardAttribute) {
        engine.handleCardAttrSelect(cardAttribute);
    }

}
