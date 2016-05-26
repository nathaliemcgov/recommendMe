package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by iguest on 5/26/16.
 */
public class RegularTextView extends TextView {

    public RegularTextView(Context context) {
        super(context);
        setFont();
    }
    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public RegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Abel-Regular.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
