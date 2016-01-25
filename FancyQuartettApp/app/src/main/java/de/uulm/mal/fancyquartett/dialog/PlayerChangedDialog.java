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
import de.uulm.mal.fancyquartett.utils.GameEngine;

/**
 * Created by Lukas on 25.01.2016.
 */
public class PlayerChangedDialog extends DialogFragment{

    private GameEngine engine;

    /**
     *
     */
    public PlayerChangedDialog() {
        // requires default constructor
    }

    /**
     *
     * @param engine
     * @return
     */
    public static PlayerChangedDialog newInstance (GameEngine engine) {
        PlayerChangedDialog dialog = new PlayerChangedDialog();
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
        View view = inflater.inflate(R.layout.dialog_player_changed, null);
        builder.setView(view)
                .setPositiveButton(getResources().getString(R.string.close_caps), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        TextView tvCurPlayer = (TextView) view.findViewById(R.id.textView_CurPlayer);
        tvCurPlayer.setText(engine.getPlayer(engine.getCurPlayer()).getName());

        // build dialog
        Dialog dialog = builder.create();
        dialog.setCancelable(false);

        return dialog;
    }
}
