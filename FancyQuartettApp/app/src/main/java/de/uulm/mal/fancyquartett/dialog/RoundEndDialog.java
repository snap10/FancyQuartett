package de.uulm.mal.fancyquartett.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.activities.GameActivity;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.Property;

/**
 * Created by Lukas on 11.01.2016.
 */
public class RoundEndDialog extends DialogFragment {

    private Card p1Card, p2Card;
    private CardAttribute cardAttribute;
    private int playerWon;
    private String p1Name, p2Name;

    /**
     *
     */
    public RoundEndDialog() {
        // requires default constructor
    }

    /**
     *
     * @param p1Card
     * @param p2Card
     * @param cardAttribute
     * @param playerWon
     * @param p1Name
     * @param p2Name
     * @return
     */
    public static RoundEndDialog newInstance(Card p1Card, Card p2Card, CardAttribute cardAttribute, int playerWon, String p1Name, String p2Name) {
        RoundEndDialog dialog = new RoundEndDialog();
        Bundle args = new Bundle();
        args.putSerializable("p1Card", p1Card);
        args.putSerializable("p2Card", p2Card);
        args.putSerializable("cardAttribute", cardAttribute);
        args.putInt("playerWon", playerWon);
        args.putString("p1Name", p1Name);
        args.putString("p2Name", p2Name);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // read bundle data
        super.onCreateDialog(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) {
            System.out.println("in");
            this.p1Card = (Card) bundle.getSerializable("p1Card");
            this.p2Card = (Card) bundle.getSerializable("p2Card");
            this.cardAttribute = (CardAttribute) bundle.getSerializable("cardAttribute");
            this.playerWon = bundle.getInt("playerWon");
            this.p1Name = bundle.getString("p1Name");
            this.p2Name = bundle.getString("p2Name");
        }
        // get the dialogbuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_round_end, null);
        // inflate and set the layout for the dialog
        // pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("positive clicked");
                    }
                });
        // set top gui elements
        TextView textViewP1Name = (TextView) view.findViewById(R.id.textView_P1Name);
        textViewP1Name.setText(p1Name + ":");
        ImageView imageViewImageP1Card = (ImageView) view.findViewById(R.id.imageView_ImageP1Card);
        imageViewImageP1Card.setImageBitmap(p1Card.getImages().get(0).getBitmap());
        TextView textViewInfoP1Card = (TextView) view.findViewById(R.id.textView_InfoP1Card);
        if(playerWon == GameActivity.GameEngine.PLAYER1) {
            textViewInfoP1Card.setText("WINNER");
        } else {
            textViewInfoP1Card.setText("LOSER");
        }
        // set center gui elements
        Property property = cardAttribute.getProperty();
        TextView textViewP1Value = (TextView) view.findViewById(R.id.textView_P1Value);
        textViewP1Value.setText(p1Card.getValue(property) + " " + property.getUnit());
        TextView textViewProperty = (TextView) view.findViewById(R.id.textView_Property);
        textViewProperty.setText(property.getText());
        TextView textViewP2Value = (TextView) view.findViewById(R.id.textView_P2Value);
        textViewP2Value.setText(p2Card.getValue(property) + " " + property.getUnit());
        // set top gui elements
        TextView textViewP2Name = (TextView) view.findViewById(R.id.textView_P2Name);
        textViewP2Name.setText(p2Name + ":");
        ImageView imageViewImageP2Card = (ImageView) view.findViewById(R.id.imageView_ImageP2Card);
        imageViewImageP2Card.setImageBitmap(p2Card.getImages().get(0).getBitmap());
        TextView textViewInfoP2Card = (TextView) view.findViewById(R.id.textView_InfoP2Card);
        if(playerWon == GameActivity.GameEngine.PLAYER2) {
            textViewInfoP2Card.setText("WINNER");
        } else {
            textViewInfoP2Card.setText("LOSER");
        }

        System.out.println("OnCreateDialog finished");
        return  builder.create();
    }

}
