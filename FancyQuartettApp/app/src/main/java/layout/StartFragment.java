package layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.activities.NewGameSettingsActivity;
import de.uulm.mal.fancyquartett.data.Settings;
import de.uulm.mal.fancyquartett.utils.GameEngine;
import de.uulm.mal.fancyquartett.utils.OnlineDecksLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private GameEngine engine;

    public StartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the #
     * provided parameters.
     *
     * @return A new instance of fragment StartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartFragment newInstance(Bundle bundle) {
        StartFragment fragment = new StartFragment();
        Bundle args = bundle;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            engine = (GameEngine) getArguments().getSerializable("savedEngine");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_start, container, false);
        if (engine != null) {
            CardView resumeGameCard = (CardView) v.findViewById(R.id.resumeGameCard);
            resumeGameCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), GameActivity.class);
                    intent.putExtra("engine", engine);
                    startActivity(intent);
                }
            });
            resumeGameCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AppTheme_Dialog);
                    builder.setMessage(R.string.deleteSavedGameQuestion).setTitle(R.string.deleteTitle);

                    // Add the buttons
                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Toast.makeText(getContext(), R.string.savedGameDeleted, Toast.LENGTH_SHORT).show();
                            SharedPreferences prefs = getActivity().getSharedPreferences("savedGame", getContext().MODE_PRIVATE);
                            prefs.edit().remove("savedGame").putBoolean("savedAvailable", false).commit();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //DO nothing

                        }
                    });
                    AlertDialog dialog = builder.create();
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
                    return false;
                }
            });
            ImageView lastGameDeckIcon = (ImageView) v.findViewById(R.id.lastgame_deckicon);
            lastGameDeckIcon.setImageBitmap(engine.getGameDeck().getDeckimage().getBitmap());
            TextView lastGameDeckName = (TextView) v.findViewById(R.id.lastgame_deckname);
            lastGameDeckName.setText(engine.getGameDeck().getName());
            TextView lastGameDeckDescription = (TextView) v.findViewById(R.id.lastgame_deckdescription);
            lastGameDeckDescription.setText(engine.getGameDeck().getDescription());
            TextView lastPlayedTextView = (TextView) v.findViewById(R.id.lastgame_lastplayed);
            lastPlayedTextView.setText(getString(R.string.last_played_on) + ": " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(engine.getLastPlayed()));
            TextView kiLevel = (TextView) v.findViewById(R.id.lastgame_ki_level);
            if (engine.isMultiplayer()) {
                kiLevel.setText(getResources().getString(R.string.multiplayer));
            } else if (engine.isMagicMode()) {
                kiLevel.setText(getResources().getString(R.string.newMagicMode));

            } else {
                kiLevel.setText(getString(R.string.ki_level) + ": " + engine.getKiLevel().toString());

            }
        } else {
            CardView resumeGameCard = (CardView) v.findViewById(R.id.resumeGameCard);
            resumeGameCard.setVisibility(View.GONE);
        }
        Button newSinglePlayerButton = (Button) v.findViewById(R.id.newSingleplayerButton);
        newSinglePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewGameSettingsActivity.class);
                intent.putExtra("multiplayer", false);
                startActivity(intent);
            }
        });
        Button newMultiplayerButton = (Button) v.findViewById(R.id.newMultiplayerButton);
        newMultiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewGameSettingsActivity.class);
                intent.putExtra("multiplayer", true);
                startActivity(intent);
            }
        });
        Button newMagicButton = (Button) v.findViewById(R.id.newMagicButton);
        newMagicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewGameSettingsActivity.class);
                intent.putExtra("magicmode", true);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement layout.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
