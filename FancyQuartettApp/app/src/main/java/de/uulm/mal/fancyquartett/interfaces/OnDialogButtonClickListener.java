package de.uulm.mal.fancyquartett.interfaces;

import android.support.v4.app.DialogFragment;

/**
 * Created by Lukas on 14.01.2016.
 */
public interface OnDialogButtonClickListener {

    public void OnDialogPositiveClick(DialogFragment dialog);
    public void OnDialogNevativeClick(DialogFragment dialog);
    public void OnDialogNeutralClick(DialogFragment dialog);

}
