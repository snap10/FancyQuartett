package de.uulm.mal.fancyquartett.activities;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.uulm.mal.fancyquartett.R;
import layout.CardGalleryFragment;

public class CardGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_gallery);
        CardGalleryFragment fragment= CardGalleryFragment.newInstance();



    }
}
