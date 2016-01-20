package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.controller.StatisticController;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatisticFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticFragment newInstance() {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_statistic, container, false);

        // fill the statistic view with values
        StatisticController ctrl = new StatisticController(this);
        TextView v1 = (TextView) v.findViewById(R.id.num_singleplayer_games);
        v1.setText(String.valueOf(ctrl.gamesPlayed()));
        TextView v2 = (TextView) v.findViewById(R.id.num_singleplayer_games_won);
        v2.setText(String.valueOf(ctrl.gamesWon()));
        TextView v3 = (TextView) v.findViewById(R.id.num_singleplayer_games_lost);
        v3.setText(String.valueOf(ctrl.gamesLost()));
        TextView v4 = (TextView) v.findViewById(R.id.percentage_singleplayer_games_won);
        String gamesWinningPercentage = new DecimalFormat("#.# %").format((float) (ctrl.gamesPlayed() - ctrl.gamesLost()) / (float) ctrl.gamesPlayed());
        v4.setText(gamesWinningPercentage);
        TextView v5 = (TextView) v.findViewById(R.id.num_singleplayer_duels);
        v5.setText(String.valueOf(ctrl.duelsMade()));
        TextView v6 = (TextView) v.findViewById(R.id.num_singleplayer_duels_won);
        v6.setText(String.valueOf(ctrl.duelsWon()));
        TextView v7 = (TextView) v.findViewById(R.id.num_singleplayer_duels_lost);
        v7.setText(String.valueOf(ctrl.duelsLost()));
        TextView v8 = (TextView) v.findViewById(R.id.percentage_singleplayer_duels_won);
        String duelsWinningPercentage = new DecimalFormat("#.# %").format((float) (ctrl.duelsMade() - ctrl.duelsLost()) / (float) ctrl.duelsMade());
        v8.setText(duelsWinningPercentage);

        return v;
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
