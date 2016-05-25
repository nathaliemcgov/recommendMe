package edu.uw.nmcgov.recommendme;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class SecondCustomTextView extends TextView{
    public SecondCustomTextView(Context context) {
        super(context);
        setFont();
    }
    public SecondCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public SecondCustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/OxygenMono-Regular.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
