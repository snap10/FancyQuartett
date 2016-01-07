package de.uulm.mal.fancyquartett.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import de.uulm.mal.fancyquartett.R;
import layout.NewGameSettingsFragment;

public class NewGameSettingsActivity extends AppCompatActivity implements NewGameSettingsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NewGameSettingsFragment fragment= NewGameSettingsFragment.newInstance(null,null);
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.new_game_settings_relativelayout, fragment, "newgamesettingsfragment");
        transaction.addToBackStack("newgamesettingsfragment");
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }
}
