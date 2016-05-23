package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by iguest on 5/22/16.
 */
public class RegularFontTextView extends TextView {
    public RegularFontTextView(Context context) {
        super(context);
        setFont();
    }
    public RegularFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public RegularFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Abel-Regular.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
