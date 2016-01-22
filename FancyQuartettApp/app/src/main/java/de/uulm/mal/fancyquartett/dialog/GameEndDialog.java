package de.uulm.mal.fancyquartett.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.utils.GameEngine;

/**
 * Created by Lukas on 11.01.2016.
 */
public class GameEndDialog extends DialogFragment {

    private GameEngine engine;
    private Player playerWon;

    /**
     *
     */
    public GameEndDialog() {
        // requires default constructor
    }

    /**
     *
     * @param engine
     * @param playerWon
     * @return
     */
    public static GameEndDialog newInstance(GameEngine engine, Player playerWon) {
        GameEndDialog dialog = new GameEndDialog();
        Bundle args = new Bundle();
        args.putSerializable("gameEngine", engine);
        args.putSerializable("playerWon", playerWon);
        dialog.setArguments(args);
        return dialog;
    }

    // TODO: newInstance(statistics)!

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // read bundle data
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            this.engine = (GameEngine) args.getSerializable("gameEngine");
            this.playerWon = (Player) args.getSerializable("playerWon");
            // TODO: read statistics
        }

        // get the dialogbuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get the layout infalter
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // inflate and set the layout for the dialog
        // pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_game_end, null);
        builder.setView(view)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        engine.OnDialogPositiveClick(GameEndDialog.this);
                    }
                });
        // build dialog
        Dialog dialog = builder.create();
        // color buttons
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((android.support.v7.app.AlertDialog) getDialog()).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
                ((android.support.v7.app.AlertDialog) getDialog()).getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.accent));
            }
        });

        // read playerWon gui elements
        TextView tvPName = (TextView) view.findViewById(R.id.textView_pName);
        // set playerWon gui elements
        tvPName.setText(playerWon.getName());

        // read statistic gui elements
        TextView tvStatistic = (TextView) view.findViewById(R.id.textView_statistics);
        // TODO: set statistic gui elements

        return dialog;
    }

}
