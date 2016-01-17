package de.uulm.mal.fancyquartett.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
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
import de.uulm.mal.fancyquartett.data.Player;
import de.uulm.mal.fancyquartett.data.Property;
import de.uulm.mal.fancyquartett.interfaces.OnDialogButtonClickListener;

/**
 * Created by Lukas on 11.01.2016.
 */
public class RoundEndDialog extends DialogFragment {

    private GameActivity.GameEngine engine;
    private CardAttribute cardAttribute;
    private int playerWon;
    private Player p1, p2;

    /**
     *
     */
    public RoundEndDialog() {
        // requires default constructor
    }

    /**
     *
     * @param engine
     * @param p1
     * @param p2
     * @param cardAttribute
     * @param playerWon
     * @return
     */
    public static RoundEndDialog newInstance(GameActivity.GameEngine engine, Player p1, Player p2, CardAttribute cardAttribute, int playerWon) {
        RoundEndDialog dialog = new RoundEndDialog();
        Bundle args = new Bundle();
        args.putSerializable("gameEngine", engine);
        args.putSerializable("p1", p1);
        args.putSerializable("p2", p2);
        args.putSerializable("cardAttribute", cardAttribute);
        args.putInt("playerWon", playerWon);
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
            this.p1 = (Player) args.get("p1");
            this.p2 = (Player) args.get("p2");
            this.cardAttribute = (CardAttribute) args.getSerializable("cardAttribute");
            this.playerWon = args.getInt("playerWon");
        }

        // get the dialogbuilder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // inflate and set the layout for the dialog
        // pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_round_end, null);
        builder.setView(view)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        engine.OnDialogPositiveClick(RoundEndDialog.this);
                    }
                });
        // build dialog
        Dialog dialog = builder.create();

        // read player1 gui elements
        TextView tvP1Name = (TextView) view.findViewById(R.id.textView_P1Name);
        ImageView ivP1Image = (ImageView) view.findViewById(R.id.imageView_ImageP1Card);
        TextView tvP1Info = (TextView) view.findViewById(R.id.textView_InfoP1Card);
        // set player1 gui
        setPlayerGUI(tvP1Name, ivP1Image, tvP1Info, p1);

        // set center gui elements
        Property property = cardAttribute.getProperty();
        TextView tvP1Value = (TextView) view.findViewById(R.id.textView_P1Value);
        TextView tvProperty = (TextView) view.findViewById(R.id.textView_Property);
        TextView tvP2Value = (TextView) view.findViewById(R.id.textView_P2Value);
        // set center gui
        setCompareGUI(property, tvProperty, tvP1Value, tvP2Value);

        // set top gui elements
        TextView tvP2Name = (TextView) view.findViewById(R.id.textView_P2Name);
        ImageView ivP2Image = (ImageView) view.findViewById(R.id.imageView_ImageP2Card);
        TextView tvP2Info = (TextView) view.findViewById(R.id.textView_InfoP2Card);
        // set player2 gui
        setPlayerGUI(tvP2Name, ivP2Image, tvP2Info, p2);

        return  dialog;
    }

    public void setPlayerGUI(TextView tvName, ImageView ivImage, TextView tvInfo, Player player) {
        // display player name
        tvName.setText(player.getName() + ":");
        // display first image from current player card
        ivImage.setImageBitmap(player.getCurrentCard().getImages().get(0).getBitmap());
        // display winner / loser
        if(playerWon != engine.STANDOFF) {
            if(playerWon == player.getId()) {
                tvInfo.setText("WINNER");
                tvInfo.setBackgroundColor(Color.GREEN);
            } else {
                tvInfo.setText("LOSER");
                tvInfo.setBackgroundColor(Color.RED);
            }
        } else {
            tvInfo.setText("STANDOFF");
            tvInfo.setBackgroundColor(Color.BLACK);
            tvInfo.setTextColor(Color.WHITE);
        }

    }

    public void setCompareGUI(Property property, TextView tvProperty, TextView tvP1Value, TextView tvP2Value) {
        // display player1 value
        tvP1Value.setText(p1.getCurrentCard().getValue(property) + " " + property.getUnit());
        // display property
        tvProperty.setText(property.getAttributeName());
        // display player2 value
        tvP2Value.setText(p2.getCurrentCard().getValue(property) + " " + property.getUnit());
    }

}
