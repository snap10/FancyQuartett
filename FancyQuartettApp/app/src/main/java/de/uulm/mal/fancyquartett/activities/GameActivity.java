package de.uulm.mal.fancyquartett.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.security.Timestamp;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.Deck;
import de.uulm.mal.fancyquartett.utils.GameEngine;

public class GameActivity extends AppCompatActivity {

    GameEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        engine = new GameEngine(getApplicationContext());
    }
}
