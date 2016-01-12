package de.uulm.mal.fancyquartett.activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.AssetsInstaller;
import layout.GalleryFragment;
import layout.StartFragment;
import layout.StatisticFragment;

public class MainActivity extends AppCompatActivity implements AssetsInstaller.OnAssetsInstallerCompletedListener, StartFragment.OnFragmentInteractionListener, GalleryFragment.OnFragmentInteractionListener, StatisticFragment.OnFragmentInteractionListener {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static final int STARTPOSITION = 0;
    private static final int GALLERYPOSITION = 1;
    private static final int STATISTICSPOSITION = 2;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean secondRun = pref.getBoolean("filesInstalled", false);
        if (!secondRun) {

            AssetsInstaller installer = null;
            try {
                installer = new AssetsInstaller(Settings.localAssets, getApplicationContext(), this);
                installer.execute();

            } catch (IOException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(this, "Error installing Files " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
                toast.show();
            }


        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    public Menu getMenu() {
        return menu;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_rules) {
            //TODO start new Activity with Rules 
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }


    /**
     * Callback method for onPostExecute of AsyncTask
     *
     * @param possibleException
     */
    @Override
    public void onAssetsInstallerCompleted(Exception possibleException) {
        if (possibleException == null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            pref.edit().putBoolean("filesInstalled", true).commit();
            //TODO test
            boolean test = pref.getBoolean("filesInstalled", false);
            Toast toast = Toast.makeText(this, "Files installed correctly", Toast.LENGTH_LONG);
            toast.show();

        } else {
            Toast toast = Toast.makeText(this, "Error installing Files " + possibleException.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case STARTPOSITION:
                    return new StartFragment().newInstance();
                case GALLERYPOSITION:
                    return new GalleryFragment().newInstance(null);
                case STATISTICSPOSITION:
                    return new StatisticFragment().newInstance();
                default:
                    throw new IllegalArgumentException("Wrong Fragment ID chosen");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.start);
                case 1:
                    return getString(R.string.gallery);
                case 2:
                    return getString(R.string.statistic);
                default:
                    throw new IllegalArgumentException("Wrong Fragment ID chosen");
            }

        }
    }

}
