package de.uulm.mal.fancyquartett.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.dialog.RulesDialog;
import de.uulm.mal.fancyquartett.enums.GameMode;
import de.uulm.mal.fancyquartett.enums.KILevel;
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
        toolbar.setNavigationIcon(R.drawable.ic_clear_white);
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
                return true;
            case R.id.action_rules:
                RulesDialog dialog = new RulesDialog().newInstance();
                dialog.show(getSupportFragmentManager(),"RulesDialog");
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_error_accent)
                .setTitle("Leaving current Game")
                .setMessage("Are you sure you want to cancel the game? This game will be saved automatically.")
                .setPositiveButton(getResources().getString(R.string.yes_caps), new DialogInterface.OnClickListener() {
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
                .setNegativeButton(getResources().getString(R.string.no_caps), null)
                .create();
        // workaround for button color
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
                ((AlertDialog) dialog).getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.secondary_text));
            }
        });
        dialog.getWindow().setWindowAnimations(R.style.AppTheme_Dialog_Animation);
        dialog.show();
    }

    @Override
    protected void onStop() {
        engine.stop();
        if(!engine.isGameOver()) {
            SharedPreferences prefs = getSharedPreferences("savedGame", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = gson.toJson(engine);
            prefs.edit().putString("savedEngine", json).putBoolean("savedAvailable", true).commit();
        }
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
