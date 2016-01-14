package de.uulm.mal.fancyquartett.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.Card;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.Property;

/**
 * Created by Lukas on 09.01.2016.
 */
public class CardAttrViewAdapter extends RecyclerView.Adapter<CardAttrViewAdapter.CardAttrViewHolder> {

    private Context context;
    private List<CardAttribute> attrList;
    private Card card;
    private OnCardAttrClickListener attrClickListener;
    /**
     *
     * @param context
     */
    public CardAttrViewAdapter(Context context) {
        this.context = context;
    }

    /**
     *
     * @param context
     * @param attrList
     */
    public CardAttrViewAdapter(Context context, List<CardAttribute> attrList,OnCardAttrClickListener listener) {
        this.context = context;
        this.attrList = attrList;
        this.attrClickListener=listener;
    }

    @Override
    public CardAttrViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_attribute, parent, false);
        return new CardAttrViewHolder(itemView, viewType, context);
    }

    @Override
    public void onBindViewHolder(CardAttrViewHolder holder, int position) {
        final CardAttribute cardAttr = attrList.get(position);
        if(cardAttr == null) {
            Toast.makeText(this.getContext(), "Error:Some CardAttributes failed to load", Toast.LENGTH_SHORT).show();
        } else {
            Property property = cardAttr.getProperty();
            double value = cardAttr.getValue();
            // set holder attributes
            holder.cardAttrName.setText(property.getAttributeName());
            holder.cardAttrValue.setText("" + value);
            holder.cardAttrUnit.setText(property.getUnit());
            // TODO: set right icons
            holder.cardAttrIcon.setImageResource(android.R.drawable.ic_menu_report_image);
            if(property.biggerWins()) {
                holder.cardAttrArrow.setImageResource(android.R.drawable.arrow_up_float);
            } else {
                holder.cardAttrArrow.setImageResource(android.R.drawable.arrow_down_float);
            }
            holder.view.setOnClickListener(getOnClickListener(property,value,cardAttr,position));
        }
    }

    private View.OnClickListener getOnClickListener(final Property property, final double value, final CardAttribute attribute, final int position) {
      View.OnClickListener onClickListener = new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              //TODO highlight selected Item
                CardView view = (CardView)v;
                view.setCardBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                attrClickListener.onCardAttrClicked(property, value, attribute);
          }
      };
        return onClickListener;
    }

    @Override
    public int getItemCount() {
        if(attrList == null) {
            return 0;
        } else {
            return attrList.size();
        }
    }

    /**
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    public void setCard(Card card) {
        this.card = card;
        attrList=card.getAttributes();
        notifyDataSetChanged();
    }
    /*
     * Listener-Interface for CardAttr-OnClick forwarding
     */
    public interface OnCardAttrClickListener {
        /**
         *  If a card attribute is clicked, the Listeners are informed with parameters
         *
         * @param property
         * @param value
         * @param attribute
         */
        public void onCardAttrClicked(Property property, double value, CardAttribute attribute);
    }

    /**
     * InnerClass CardAttrViewHolder extends RecyclerView.ViewHolder
     */
    public static class CardAttrViewHolder extends RecyclerView.ViewHolder {

        private View view;

        public TextView cardAttrName;
        public TextView cardAttrValue;
        public TextView cardAttrUnit;
        public ImageView cardAttrIcon;
        public ImageView cardAttrArrow;

        protected int index;
        protected Context context;


        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * @param v
         * @param index
         * @param context
         */
        public CardAttrViewHolder(View v, int index, final Context context) {
            super(v);
            this.view = v;
            this.cardAttrName = (TextView) v.findViewById(R.id.textView_attrName);
            this.cardAttrValue = (TextView) v.findViewById(R.id.textView_attrValue);
            this.cardAttrUnit = (TextView) v.findViewById(R.id.textView_attrUnit);
            this.cardAttrIcon = (ImageView) v.findViewById(R.id.imageView_attrIcon);
            this.cardAttrArrow = (ImageView) v.findViewById(R.id.imageView_attrArrow);
        }
    }
}
