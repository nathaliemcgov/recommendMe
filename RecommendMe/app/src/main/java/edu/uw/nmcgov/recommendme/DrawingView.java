package edu.uw.nmcgov.recommendme;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * A basic custom view for drawing on.
 * @author Joel Ross
 * @version Winter 2016
 */
public class DrawingView extends View {

    private static final String TAG = "SurfaceView";

    private int viewWidth, viewHeight; //size of the view

    private Bitmap bmp; //image to draw on

    //drawing values
    private Paint redPaint; //drawing variables (pre-defined for speed)


    /**
     * We need to override all the constructors, since we don't know which will be called
     * All the constructors eventually call init()
     */
    public DrawingView(Context context) {
        this(context, null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

        viewWidth = 1; viewHeight = 1; //positive defaults; will be replaced when #surfaceChanged() is called

        //set up drawing variables ahead of timme
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.RED);

    }

    /**
     * Override method that is called when the size of the display is specified (or changes
     * due to rotation).
     */
    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        //store new size of the view
        viewWidth = w;
        viewHeight = h;

        //create a properly-sized bitmap to draw on
        bmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);

    }

    /**
     * Override this method to specify drawing. It is like our "paintComponent()" method.
     */
    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas); //make sure to have the parent do any drawing it is supposed to!

        canvas.drawColor(Color.BLACK); //black out the background

        canvas.drawCircle(viewWidth/2, viewHeight/2, 100f, redPaint); //we can draw directly onto the canvas
    }

}