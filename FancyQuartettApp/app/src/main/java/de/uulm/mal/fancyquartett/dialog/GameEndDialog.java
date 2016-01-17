package de.uulm.mal.fancyquartett.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.data.Player;

/**
 * Created by Lukas on 11.01.2016.
 */
public class GameEndDialog extends DialogFragment {

    private GameActivity.GameEngine engine;
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
    public static GameEndDialog newInstance(GameActivity.GameEngine engine, Player playerWon) {
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
            this.engine = (GameActivity.GameEngine) args.getSerializable("gameEngine");
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

        // read playerWon gui elements
        TextView tvPName = (TextView) view.findViewById(R.id.textView_pName);
        // set playerWon gui elements
        tvPName.setText(playerWon.getName() + " wins!");

        // read statistic gui elements
        TextView tvStatistic = (TextView) view.findViewById(R.id.textView_statistics);
        // TODO: set statistic gui elements

        return dialog;
    }

}
