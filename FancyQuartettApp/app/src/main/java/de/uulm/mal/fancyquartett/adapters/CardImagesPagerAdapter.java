package de.uulm.mal.fancyquartett.adapters;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.uulm.mal.fancyquartett.R;
import de.uulm.mal.fancyquartett.data.Card;

/**
 * Created by Snap10 on 10/01/16.
 */
public class CardImagesPagerAdapter extends PagerAdapter {

    private final Context context;
    Card card;
    public CardImagesPagerAdapter(Context context,Card card) {
        super();
        this.card=card;
        this.context = context;
    }


    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate(ViewGroup)}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (position==0){
            TextView cardDescription = new TextView(context);
            cardDescription.setPadding(10,10,10,10);
            cardDescription.setTextColor(Color.WHITE);
            cardDescription.setText(card.getDescription());
            container.addView(cardDescription);
            return cardDescription;
        }else{
            ImageView imageView = new ImageView(context);
            ViewPager.LayoutParams params = new ViewPager.LayoutParams();
            params.height=ViewPager.LayoutParams.MATCH_PARENT;
            params.width=ViewPager.LayoutParams.MATCH_PARENT;
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(card.getImages().get(position-1).getBitmap());
            container.addView(imageView);
            return imageView;
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return card.getImages().size()+1;
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with <code>object</code>
     * @param object Object to check for association with <code>view</code>
     * @return true if <code>view</code> is associated with the key object <code>object</code>
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
