package de.uulm.mal.fancyquartett.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.GameActivity;


/**
 * Created by Lukas on 19.01.2016.
 */
public class KiPlaysDialog extends DialogFragment {

    private GameActivity.GameEngine engine;

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
    public static KiPlaysDialog newInstance (GameActivity.GameEngine engine) {
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
            this.engine = (GameActivity.GameEngine) args.getSerializable("gameEngine");
        }

        // get the dialogbuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get the layout infalter
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // inflate and set the layout for the dialog
        // pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_ki_plays, null);
        builder.setView(view);

        // build dialog
        Dialog dialog = builder.create();
        dialog.setCancelable(false);

        return dialog;
    }
}
