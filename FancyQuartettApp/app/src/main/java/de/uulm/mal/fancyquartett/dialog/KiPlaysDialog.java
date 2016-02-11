package de.uulm.mal.fancyquartett.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.utils.GameEngine;


/**
 * Created by Lukas on 19.01.2016.
 */
public class KiPlaysDialog extends DialogFragment {

    private GameEngine engine;

    /**
     *
     */
    public KiPlaysDialog() {
        // requires default constructor
    }

    /**
     *
     * @param engine
     * @return
     */
    public static KiPlaysDialog newInstance (GameEngine engine) {
        KiPlaysDialog dialog = new KiPlaysDialog();
        Bundle args = new Bundle();
        args.putSerializable("gameEngine", engine);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // read bundle data
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            this.engine = (GameEngine) args.getSerializable("gameEngine");
        }

        // get the dialogbuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        // get the layout infalter
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // inflate and set the layout for the dialog
        // pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_ki_plays, null);
        TextView tvPlayerName = (TextView) view.findViewById(R.id.textView_PlayerName);
        tvPlayerName.setText(engine.getPlayer(engine.getCurPlayer()).getName());

        builder.setView(view)
            .setPositiveButton(getString(R.string.pause_game_caps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    engine.stop();
                    engine.getGameActivity().onBackPressed();
                }
            });

        // build dialog
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        // color buttons
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // workaround for fancy NullPointerException
                AlertDialog alertDialog = ((AlertDialog) getDialog());
                if(alertDialog != null) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
                }
            }
        });

        return dialog;
    }


}
