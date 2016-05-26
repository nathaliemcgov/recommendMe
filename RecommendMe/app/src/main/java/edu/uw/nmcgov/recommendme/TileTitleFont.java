package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by iguest on 5/25/16.
 */
public class TileTitleFont extends TextView {
    public TileTitleFont(Context context) {
        super(context);
        setFont();
    }
    public TileTitleFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public TileTitleFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoCondensed-Regular.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
