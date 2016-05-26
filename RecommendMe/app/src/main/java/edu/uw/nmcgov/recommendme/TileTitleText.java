package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by iguest on 5/26/16.
 */
public class TileTitleText extends TextView {
    public TileTitleText(Context context) {
        super(context);
        setFont();
    }
    public TileTitleText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public TileTitleText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/CabinCondensed-Regular.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
