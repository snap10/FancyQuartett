package layout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.activities.NewGameGalleryActivity;
import de.uulm.mal.fancyquartett.data.OfflineDeck;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.enums.GameMode;
import de.uulm.mal.fancyquartett.enums.KILevel;
import de.uulm.mal.fancyquartett.interfaces.OnDialogButtonClickListener;
import de.uulm.mal.fancyquartett.interfaces.OnShakeListener;
import de.uulm.mal.fancyquartett.utils.LocalDeckLoader;
import de.uulm.mal.fancyquartett.utils.LocalDecksLoader;
import de.uulm.mal.fancyquartett.utils.ShakeDetector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewGameSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewGameSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewGameSettingsFragment extends Fragment implements LocalDeckLoader.OnLocalDeckLoadedListener, LocalDecksLoader.OnLocalDecksLoadedListener , OnShakeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private GameModePagerAdapter mGameModePagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private OnFragmentInteractionListener mListener;
    private Menu menu;
    private OfflineDeck offlineDeck;
    private View v;
    //Gamevariables
    private int roundtimeoutseconds = 0;
    private KILevel kilevel = KILevel.Medium;
    private GameMode gameMode = GameMode.ToTheEnd;
    private CompoundButton.OnCheckedChangeListener timeoutSwitchListener;
    private ViewPager.OnPageChangeListener viewPagerChangeListener;
    private View.OnClickListener cardViewClickListener;
    private SwitchCompat timeoutSwitch;
    private RadioGroup.OnCheckedChangeListener aiButtonListener;
    private SwitchCompat numberOfRoundsSwitch;
    private CompoundButton.OnCheckedChangeListener numberOfRoundsSwitchListener;
    private int maxrounds =0;
    private boolean multiplayer;
    private boolean magicmode;

    private AlertDialog randomSettingsDialog;
    private SensorManager mSensorManager;
    private ShakeDetector mShakeDetector;
    private Sensor mAccelerometer;
    private boolean isShakeEnabled;


    public NewGameSettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onResume() {
        super.onResume();
        // Register Listener
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public void onPause() {
        // Unregister Listener
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>This corresponds to {@link Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (offlineDeck != null) {
            outState.putSerializable("offlinedeck", offlineDeck);
            outState.putBoolean("multiplayer",multiplayer);
        }
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bundle
     * @return A new instance of fragment NewGameSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewGameSettingsFragment newInstance(Bundle bundle) {
        NewGameSettingsFragment fragment = new NewGameSettingsFragment();
        Bundle args = bundle;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            offlineDeck = (OfflineDeck) savedInstanceState.getSerializable("offlinedeck");
        }
        if (getArguments()!=null){
            multiplayer =getArguments().getBoolean("multiplayer");
            magicmode = getArguments().getBoolean ("magicmode");
            offlineDeck=(OfflineDeck)getArguments().getSerializable("offlinedeck");
        }
        setHasOptionsMenu(true);
        intitalizeListeners();

        // build dialog
        isShakeEnabled = true;
        randomSettingsDialog = new AlertDialog.Builder(this.getContext())
                .setIcon(R.drawable.ic_error_accent)
                .setCancelable(false)
                .setTitle(getString(R.string.randomsettingsdialogtitle))
                .setMessage(getString(R.string.randomsettingsdialogtext))
                .setPositiveButton(getResources().getString(R.string.ok_caps), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // activate random select by shaking device
                        isShakeEnabled = true;
                    }
                })
                .create();
        // workaround for button color
        randomSettingsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
                ((AlertDialog) dialog).getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.secondary_text));
            }
        });
        randomSettingsDialog.getWindow().setWindowAnimations(R.style.AppTheme_Dialog_Animation);

        // initialise ShakeDetector
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(this);

        // show tipp
        Toast.makeText(getContext(),R.string.randomsettingstoast,Toast.LENGTH_SHORT).show();
    }

    private void intitalizeListeners() {
        aiButtonListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton:
                        kilevel = KILevel.Soft;
                        break;
                    case R.id.radioButton2:
                        kilevel = KILevel.Medium;
                        break;
                    case R.id.radioButton3:
                        kilevel = KILevel.Hard;
                        break;
                }
            }
        };
        timeoutSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    EditText editRoundTimout = (EditText) v.findViewById(R.id.timoutsecondedittext);
                    editRoundTimout.setEnabled(true);
                } else {
                    EditText editRoundTimout = (EditText) v.findViewById(R.id.timoutsecondedittext);
                    editRoundTimout.setEnabled(false);
                }
            }
        };
        numberOfRoundsSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    EditText roundsEditText = (EditText) v.findViewById(R.id.maximumroundsedittext);
                    roundsEditText.setEnabled(true);
                } else {
                    EditText roundsEditText = (EditText) v.findViewById(R.id.maximumroundsedittext);
                    roundsEditText.setEnabled(false);
                }
            }
        };

        viewPagerChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        gameMode = GameMode.ToTheEnd;
                        break;
                    case 1:
                        gameMode = GameMode.Time;
                        break;
                    case 2:
                        gameMode = GameMode.Points;
                        break;
                    default:
                        throw new IllegalArgumentException("Wrong Fragment ID chosen");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        cardViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewGameGalleryActivity.class);
                startActivityForResult(intent, 0);
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_new_game_settings, container, false);
        v.requestFocus();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        View cardview = v.findViewById(R.id.chosendecktitem);
        cardview.setOnClickListener(cardViewClickListener);
        mGameModePagerAdapter = new GameModePagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager) v.findViewById(R.id.container);

            // Set up the ViewPager with the sections adapter.
            mViewPager.setAdapter(mGameModePagerAdapter);
            mViewPager.addOnPageChangeListener(viewPagerChangeListener);
            TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

        if (multiplayer){
            GameModeHotSeatFragment fragment = GameModeHotSeatFragment.newInstance();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.multiplayerFragmentContainer,fragment).commit();
        }

        if(!multiplayer){
            RadioGroup aibuttongroup = (RadioGroup) v.findViewById(R.id.aibuttongroup);
            switch (aibuttongroup.getCheckedRadioButtonId()) {
                case R.id.radioButton:
                    kilevel = KILevel.Soft;
                    break;
                case R.id.radioButton2:
                    kilevel = KILevel.Medium;
                    break;
                case R.id.radioButton3:
                    kilevel = KILevel.Hard;
                    break;
            }
            aibuttongroup.setOnCheckedChangeListener(aiButtonListener);
        }else{
            RadioGroup aibuttongroup = (RadioGroup) v.findViewById(R.id.aibuttongroup);
            aibuttongroup.setVisibility(RadioGroup.GONE);
        }

        timeoutSwitch = (SwitchCompat) v.findViewById(R.id.timeout_switch);
        timeoutSwitch.setOnCheckedChangeListener(timeoutSwitchListener);
        if (timeoutSwitch.isChecked()) {
            EditText editRoundTimout = (EditText) v.findViewById(R.id.timoutsecondedittext);
            editRoundTimout.setEnabled(true);
        } else {
            EditText editRoundTimout = (EditText) v.findViewById(R.id.timoutsecondedittext);
            editRoundTimout.setEnabled(false);
        }
        numberOfRoundsSwitch = (SwitchCompat) v.findViewById(R.id.numberofrounds_switch);
        numberOfRoundsSwitch.setOnCheckedChangeListener(numberOfRoundsSwitchListener);
        if (timeoutSwitch.isChecked()) {
            EditText editmaxrounds= (EditText) v.findViewById(R.id.maximumroundsedittext);
            editmaxrounds.setEnabled(true);
        } else {
            EditText editmaxrounds = (EditText) v.findViewById(R.id.maximumroundsedittext);
            editmaxrounds.setEnabled(false);
        }

        if (offlineDeck != null) {
            ImageView imageview = (ImageView) v.findViewById(R.id.deckicon);
            imageview.setImageBitmap(offlineDeck.getCards().get(0).getImages().get(0).getBitmap());
            TextView deckname = (TextView) v.findViewById(R.id.deckname);
            deckname.setText(offlineDeck.getName());
            TextView deckdescription = (TextView) v.findViewById(R.id.deckdescription);
            deckdescription.setText(offlineDeck.getName());
        }


        return v;
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && v != null) {
            offlineDeck = (OfflineDeck) data.getSerializableExtra("offlinedeck");
            ImageView imageview = (ImageView) v.findViewById(R.id.deckicon);
            imageview.setImageBitmap(offlineDeck.getCards().get(0).getImages().get(0).getBitmap());
            TextView deckname = (TextView) v.findViewById(R.id.deckname);
            deckname.setText(offlineDeck.getName());
            TextView deckdescription = (TextView) v.findViewById(R.id.deckdescription);
            deckdescription.setText(offlineDeck.getName());
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Callback Method for LocalDeckLoader
     * //TODO check if need, possibly not needed anymore
     *
     * @param offlineDeck
     */
    @Override
    public void onDeckLoaded(OfflineDeck offlineDeck) {
        System.out.println("SELECTED: " + offlineDeck.getName());
        this.offlineDeck = offlineDeck;
        ImageView imageview = (ImageView) getActivity().findViewById(R.id.deckicon);
        imageview.setImageBitmap(offlineDeck.getCards().get(0).getImages().get(0).getBitmap());
        TextView deckname = (TextView) getActivity().findViewById(R.id.deckname);
        deckname.setText(offlineDeck.getName());
        TextView deckdescription = (TextView) getActivity().findViewById(R.id.deckdescription);
        deckdescription.setText(offlineDeck.getName());
    }

    /**
     * @param offlineDecks
     */
    @Override
    public void onLocalDecksLoaded(ArrayList<OfflineDeck> offlineDecks) {
        if (offlineDecks != null){
            System.out.println("DECKANZAHL: " + offlineDecks.size());
            if(offlineDecks.size() > 0) {
                Random rand = new Random();
                offlineDeck = offlineDecks.get(rand.nextInt(offlineDecks.size()));
                onDeckLoaded(offlineDeck);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link Activity#onCreateOptionsMenu(Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        this.menu = menu;
        inflater.inflate(R.menu.newgamesettings_optionsmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.startGameButton) {
            if (offlineDeck != null) {
                Intent intent = new Intent(getContext(), GameActivity.class);
                intent.putExtra("offlinedeck", offlineDeck);
                intent.putExtra("gamemode", gameMode);
                intent.putExtra("kilevel", kilevel);
                intent.putExtra("multiplayer", multiplayer);
                intent.putExtra("magicmode",magicmode);
                if (magicmode){
                    intent.putExtra("playername1","AI 1");
                    intent.putExtra("playername2","AI 2");
                }
                if(multiplayer){
                    EditText player1 = (EditText)v.findViewById(R.id.playername1);
                    EditText player2 = (EditText)v.findViewById(R.id.playername2);
                    intent.putExtra("playername1",player1.getText().toString());
                    intent.putExtra("playername2",player2.getText().toString());
                }
                if (timeoutSwitch.isChecked()) {
                    EditText editText = (EditText) v.findViewById(R.id.timoutsecondedittext);
                    roundtimeoutseconds = Integer.parseInt(editText.getText().toString());
                } else {
                    roundtimeoutseconds = 0;
                }
                /**
                 * 0 if deactivated
                 * integer > 0 if activated
                 */
                intent.putExtra("roundtimeout", roundtimeoutseconds);
                SwitchCompat numberOfRoundsSwitch = (SwitchCompat) v.findViewById(R.id.numberofrounds_switch);
                if (numberOfRoundsSwitch.isChecked()) {
                    EditText numberofmaximumrounds = (EditText) v.findViewById(R.id.maximumroundsedittext);
                   maxrounds =Integer.parseInt(numberofmaximumrounds.getText().toString());
                } else {
                    maxrounds =0;
                }
                intent.putExtra("maxrounds", maxrounds);
                //Gamemodespecific
                switch (gameMode) {
                    case ToTheEnd:
                        break;
                    case Time:
                        EditText timeText = (EditText) v.findViewById(R.id.gametimeedittext);
                        int gametime = Integer.parseInt(timeText.getText().toString());
                        intent.putExtra("gametime", gametime);
                        break;
                    case Points:
                        EditText pointsText = (EditText) v.findViewById(R.id.winpointsedittext);
                        int gamepoints = Integer.parseInt(pointsText.getText().toString());
                        intent.putExtra("gamepoints", gamepoints);
                        break;
                }
                startActivity(intent);
            }else {
                Toast.makeText(getContext(),R.string.noDeckSelectedForGame,Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class GameModePagerAdapter extends FragmentPagerAdapter {

        public GameModePagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GameModeToTheEndFragment.newInstance();
                case 1:
                    return GameModeTimeFragment.newInstance();
                case 2:
                    return GameModePointsFragment.newInstance();

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
                    return getString(R.string.totheend);
                case 1:
                    return getString(R.string.time);
                case 2:
                    return getString(R.string.points);

                default:
                    throw new IllegalArgumentException("Wrong Fragment ID chosen");
            }

        }
    }

    @Override
    public void onShake(int count) {
        // do random stuff after  > 2 shakes
        if(count > 1 && isShakeEnabled) {
            // disable random select by shaking device
            isShakeEnabled = false;

            new LocalDecksLoader(getContext().getFilesDir()+Settings.localFolder, this).execute();

            Random rand = new Random();

            timeoutSwitch.setChecked(rand.nextBoolean());
            if (timeoutSwitch.isChecked()) {
                EditText editText = (EditText) v.findViewById(R.id.timoutsecondedittext);
                editText.setEnabled(true);
                int next = rand.nextInt(25) + 5;
                editText.setText("" + next);
            }else{
                EditText editText = (EditText) v.findViewById(R.id.timoutsecondedittext);
                editText.setEnabled(false);
            }

            numberOfRoundsSwitch.setChecked(rand.nextBoolean());
            if (numberOfRoundsSwitch.isChecked()) {
                EditText numberofmaximumrounds = (EditText) v.findViewById(R.id.maximumroundsedittext);
                numberofmaximumrounds.setEnabled(true);
                int next = rand.nextInt(85) + 15;
                numberofmaximumrounds.setText("" + next);
            }else{
                EditText numberofmaximumrounds = (EditText) v.findViewById(R.id.maximumroundsedittext);
                numberofmaximumrounds.setEnabled(false);
            }

            switch (rand.nextInt(3)) {
                case 0:
                    gameMode = GameMode.ToTheEnd;
                    mViewPager.setCurrentItem(0);
                    break;
                case 1:
                    gameMode = GameMode.Time;
                    mViewPager.setCurrentItem(1);
                    EditText timeText = (EditText) v.findViewById(R.id.gametimeedittext);
                    timeText.setText("" + (rand.nextInt(25) + 5));
                    break;
                case 2:
                    gameMode = GameMode.Points;
                    mViewPager.setCurrentItem(2);
                    EditText pointsText = (EditText) v.findViewById(R.id.winpointsedittext);
                    pointsText.setText("" + (rand.nextInt(85) + 15));
                    break;
            }

            if (!multiplayer) {
                RadioGroup aibuttongroup = (RadioGroup) v.findViewById(R.id.aibuttongroup);
                switch (rand.nextInt(3)) {
                    case 0:
                        kilevel = KILevel.Soft;
                        aibuttongroup.check(R.id.radioButton);
                        break;
                    case 1:
                        kilevel = KILevel.Medium;
                        aibuttongroup.check(R.id.radioButton2);
                        break;
                    case 2:
                        kilevel = KILevel.Hard;
                        aibuttongroup.check(R.id.radioButton3);
                        break;
                }
            }

            // show dialog
            randomSettingsDialog.show();
        }
    }
}
