package de.uulm.mal.fancyquartett.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import de.uulm.mal.fancyquartett.R;

/**
 * Created by Lukas on 24.01.2016.
 */
public class RulesDialog extends DialogFragment {

    /**
     *
     */
    public RulesDialog() {
        // requires default constructor
    }

    /**
     *
     * @return
     */
    public static RulesDialog newInstance() {
        RulesDialog dialog = new RulesDialog();
        Bundle args = new Bundle();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // read bundle data
        super.onCreateDialog(savedInstanceState);

        // get the dialogbuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog);
        // get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // inflate and set the layout for the dialog
        // pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_rules, null);
        builder.setView(view)
                .setTitle("Rules")
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.close_caps), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        // build dialog
        Dialog dialog = builder.create();
        // color buttons
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((android.support.v7.app.AlertDialog) getDialog()).getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
            }
        });

        return dialog;
    }
}
