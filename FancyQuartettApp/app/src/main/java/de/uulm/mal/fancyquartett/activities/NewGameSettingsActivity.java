package de.uulm.mal.fancyquartett.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.uulm.mal.fancyquartett.R;
import layout.NewGameSettingsFragment;

public class NewGameSettingsActivity extends AppCompatActivity implements NewGameSettingsFragment.OnFragmentInteractionListener {
    NewGameSettingsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            fragment = (NewGameSettingsFragment)manager.getFragment(savedInstanceState, "fragment");

        }else{

            fragment = NewGameSettingsFragment.newInstance(getIntent().getExtras());
        }
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.new_game_settings_relativelayout, fragment, "newgamesettingsfragment");
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fragment!=null){
            //Save the fragment's instance
            getSupportFragmentManager().putFragment(outState, "fragment", fragment);
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }
}
