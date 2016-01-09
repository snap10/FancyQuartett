package de.uulm.mal.fancyquartett.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.CardAttribute;
import de.uulm.mal.fancyquartett.data.Property;

/**
 * Created by Lukas on 09.01.2016.
 */
public class CardAttrViewAdapter extends RecyclerView.Adapter<CardAttrViewAdapter.CardAttrViewHolder> {

    private Context context;
    private List<CardAttribute> attrList;

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
    public CardAttrViewAdapter(Context context, List<CardAttribute> attrList) {
        this.context = context;
        this.attrList = attrList;
    }

    @Override
    public CardAttrViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_attribute_grid_item, parent, false);
        return new CardAttrViewHolder(itemView, viewType, context);
    }

    @Override
    public void onBindViewHolder(CardAttrViewHolder holder, int position) {
        final CardAttribute cardAttr = attrList.get(position);
        if(cardAttr == null) {
            Toast.makeText(this.getContext(), "Error:Some CardAttributes failed to load", Toast.LENGTH_SHORT).show();
        } else {
            Property property = cardAttr.getProperty();
            float value = cardAttr.getValue();
            // set holder attributes
            holder.cardAttrName.setText(property.getText());
            holder.cardAttrValue.setText("" + value);
            // TODO: set right icons
            holder.cardAttrIcon.setImageResource(android.R.drawable.ic_menu_report_image);
            if(property.biggerWins()) {
                holder.cardAttrArrow.setImageResource(android.R.drawable.arrow_up_float);
            } else {
                holder.cardAttrArrow.setImageResource(android.R.drawable.arrow_down_float);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(attrList.size() == 0) {
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

    /**
     * InnerClass CardAttrViewHolder extends RecyclerView.ViewHolder
     */
    public static class CardAttrViewHolder extends RecyclerView.ViewHolder {

        private View view;
        public TextView cardAttrName;
        public TextView cardAttrValue;
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
            this.cardAttrIcon = (ImageView) v.findViewById(R.id.imageView_attrIcon);
            this.cardAttrArrow = (ImageView) v.findViewById(R.id.imageView_attrArrow);
        }
    }
}
