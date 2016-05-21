package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.firebase.client.Firebase;
import com.firebase.client.core.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An example SurfaceView for generating graphics on
 * @author Joel Ross
 * @version Winter 2016
 */
public class DrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "SurfaceView";

    private int viewWidth, viewHeight; //size of the view

    private Bitmap bmp; //image to draw on

    private SurfaceHolder mHolder; //the holder we're going to post updates to
    private DrawingRunnable mRunnable; //the code htat we'll want to run on a background thread
    private Thread mThread; //the background thread

    private Paint redPaint; //drawing variables (pre-defined for speed)

    private RCMDFirebase firebase;
    private List<RelatedObject> list;
    private List<float[]> distAndDirection;
    private List<float[]> currXY;
    private String base;
    private int MAX;


    /**
     * We need to override all the constructors, since we don't know which will be called
     */
    public DrawingSurfaceView(Context context) {
        this(context, null);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

        Firebase.setAndroidContext(context);
        firebase = new RCMDFirebase();

        MAX = 20;


        viewWidth = 1; viewHeight = 1; //positive defaults; will be replaced when #surfaceChanged() is called

        // register our interest in hearing about changes to our surface
        mHolder = getHolder();
        mHolder.addCallback(this);

        mRunnable = new DrawingRunnable();

        //set up drawing variables ahead of timme
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.WHITE);
        redPaint.setTextSize(30);


        list = new ArrayList<RelatedObject>();
        distAndDirection = new ArrayList<float[]>();
        currXY = new ArrayList<float[]>();
        CustomTileAdapter adapter = new CustomTileAdapter(this.getContext(), list, "");
        base = ((CircleGraphicActivity) context).getFireBaseTitle();
        firebase.queryTitle(base, "", list, adapter);
        List<float[]> distAndDirection = new ArrayList<float[]>();



    }


    /**
     * Helper method for the "game loop"
     */
    public void update(){
        //update the "game state" here (move things around, etc.
        //TODO: fill in your own logic here!
        for(int i = 0; i < Math.min(list.size(), MAX); i++) {
            RelatedObject object = list.get(i);
            if(distAndDirection.size() <= i) {
                float[] distAndDirectionArray = new float[3];
                distAndDirection.add(distAndDirectionArray);
                distAndDirectionArray[0] = (float) (Math.random() * 5); // starting velocity
                distAndDirectionArray[1] = (float) (Math.random() * 360); //starting degree

                float[] currXYArray = new float[2];
                currXY.add(currXYArray);
                currXYArray[0] = viewWidth / 2 - getMiddleOfWord(object.toString());
                currXYArray[1] = viewHeight / 2 - getDistFromRatio(object.getRatio());
            }
            float[] distAndDirectionArray = distAndDirection.get(i);
            distAndDirectionArray[1] =   (float) (distAndDirectionArray[1] + distAndDirectionArray[0]);
            distAndDirectionArray[0] = (float) (distAndDirectionArray[0] * .99);

            float[] currXYArray = currXY.get(i);
            getPointOnCircle(distAndDirectionArray[1], getDistFromRatio(object.getRatio()), currXYArray);

        }
    }

    private void getPointOnCircle(float degress, float radius, float[] points) {

        int x = Math.round(viewWidth / 2);
        int y = Math.round(viewHeight / 2);

        double rads = Math.toRadians(degress - 90); // 0 becomes the top

        // Calculate the outter point of the line
        int xPosy = Math.round((float) (x + Math.cos(rads) * radius));
        int yPosy = Math.round((float) (y + Math.sin(rads) * radius));

        points[0] = xPosy;
        points[1] = yPosy;

    }


    /**
     * Helper method for the "render loop"
     * @param canvas The canvas to draw on
     */
    public void render(Canvas canvas){
        if(canvas == null) return; //if we didn't get a valid canvas for whatever reason

        canvas.drawColor(Color.BLACK); //black out the background

        canvas.drawText(base,  viewWidth / 2 - getMiddleOfWord(base), viewHeight / 2, redPaint);

        for(int i = 0; i < Math.min(list.size(), MAX); i++) {
            RelatedObject object = list.get(i);
            float[] currXYArray = currXY.get(i);

            canvas.drawText(object.toString(), currXYArray[0] - getMiddleOfWord(object.toString()), currXYArray[1], redPaint);
        }

        canvas.drawBitmap(bmp,0,0,null); //and then draw the BitMap onto the canvas.
    }

    private float getMiddleOfWord(String s) {
        return ((s.length() * 10) / 2);
    }

    private float getDistFromRatio(double ratio) {
        return (float) (50 + (ratio - 1) * 700);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // create thread only; it's started in surfaceCreated()
        Log.v(TAG, "making new thread");
        mThread = new Thread(mRunnable);
        mRunnable.setRunning(true); //turn on the runner
        mThread.start(); //start up the thread when surface is created

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (mHolder) { //synchronized to keep this stuff atomic
            viewWidth = width;
            viewHeight = height;
            bmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888); //new buffer to draw on
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        mRunnable.setRunning(false); //turn off
        boolean retry = true;
        while(retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //will try again...
            }
        }
        Log.d(TAG, "Drawing thread shut down.");
    }




    /**
     * An inner class representing a runnable that does the drawing. Animation timing could go in here.
     * http://obviam.net/index.php/the-android-game-loop/ has some nice details about using timers to specify animation
     */
    public class DrawingRunnable implements Runnable {

        private boolean isRunning; //whether we're running or not (so we can "stop" the thread)

        public void setRunning(boolean running){
            this.isRunning = running;
        }

        public void run() {
            Canvas canvas;
            while(isRunning)
            {
                canvas = null;
                try {
                    canvas = mHolder.lockCanvas(); //grab the current canvas
                    synchronized (mHolder) {
                        update(); //update the game
                        render(canvas); //redraw the screen
                    }
                }
                finally { //no matter what (even if something goes wrong), make sure to push the drawing so isn't inconsistent
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        Log.v(TAG, event.getX() + "");
        return true;
    }
}