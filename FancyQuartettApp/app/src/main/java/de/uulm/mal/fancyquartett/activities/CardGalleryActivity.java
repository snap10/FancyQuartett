package de.uulm.mal.fancyquartett.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;
import layout.CardGalleryFragment;
import layout.StartFragment;

public class CardGalleryActivity extends AppCompatActivity implements CardGalleryFragment.OnFragmentInteractionListener {
    String deckname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_gallery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getExtras()!=null){
           deckname = getIntent().getExtras().getString("deckname");
        }else{
            //TODO decide what to do...
            finish();
        }

        //TODO saveLayoutInstance in instanceState and pass it to the fragment on creation
        CardGalleryFragment fragment= CardGalleryFragment.newInstance(deckname, null);
        LocalDeckLoader loader = new LocalDeckLoader(getFilesDir()+ Settings.localFolder,deckname.toLowerCase(),fragment);
        loader.execute();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.card_gallery_relativelayout,fragment,"cardgalleryfragment");
        transaction.addToBackStack("cardgalleryfragment");
        transaction.commit();



    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }


}
