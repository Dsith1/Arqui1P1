package com.example.arqui1p1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Dibujo extends View {

    public Bitmap bmp;
    public Canvas lienzo;
    private Path ruta;
    private Paint dibujobmp;
    private Paint mdibujo;



    public Dibujo(Context c, AttributeSet attb){
        super(c,attb);

        ruta=new Path();
        dibujobmp= new Paint(Paint.DITHER_FLAG);

        mdibujo = new Paint();
        mdibujo.setAntiAlias(true);
        mdibujo.setDither(true);
        mdibujo.setColor(0xFF000000);
        mdibujo.setStyle(Paint.Style.STROKE);
        mdibujo.setStrokeJoin(Paint.Join.ROUND);
        mdibujo.setStrokeCap(Paint.Cap.ROUND);
        mdibujo.setStrokeWidth(9);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        lienzo = new Canvas(bmp);
    }

    public void SetColor(int color){

        switch (color){
            case 1:
                mdibujo.setColor(0xFFFF0000);
                break;

            case 2:
                mdibujo.setColor(0xFF000000);
                break;

            case 3:
                mdibujo.setColor(0xFF0000FF);
                break;
        }

    }




    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmp, 0, 0, dibujobmp);

        canvas.drawPath(ruta, mdibujo);

    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        ruta.reset();
        ruta.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            ruta.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        ruta.lineTo(mX, mY);
        // commit the path to our offscreen
        lienzo.drawPath(ruta, mdibujo);
        // kill this so we don't double draw
        ruta.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public Bitmap getBitmap()
    {
        //this.measure(100, 100);
        //this.layout(0, 0, 100, 100);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);


        return bmp;
    }



    public void clear(){
        bmp.eraseColor(Color.TRANSPARENT);
        invalidate();
        System.gc();

    }
}
