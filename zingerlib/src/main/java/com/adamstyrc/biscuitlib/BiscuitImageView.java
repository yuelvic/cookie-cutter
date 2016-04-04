package com.adamstyrc.biscuitlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by adamstyrc on 31/03/16.
 */
public class BiscuitImageView extends ImageView {

    private Paint circlePaint;
    private int circleRadius;
    private Circle circle;

    public BiscuitImageView(Context context) {
        super(context);
        init();
    }

    public BiscuitImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public BiscuitImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(value = 21)
    public BiscuitImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    public void init() {
        setScaleType(ScaleType.MATRIX);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStrokeWidth(5);
        circlePaint.setStyle(Paint.Style.STROKE);

        circleRadius = 400;

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                circle = new Circle(getWidth() / 2, getHeight() / 2, circleRadius);
                setOnTouchListener(new BiscuitTouchListener(circle));
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;


        canvas.drawCircle(cx, cy, circleRadius, circlePaint);
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
        if (circle != null) {
            circle.setRadius(circleRadius);
        }
    }

    public Bitmap getCroppedBitmap() {
        Matrix matrix = getImageMatrix();

        MatrixParams matrixParams = MatrixParams.fromMatrix(matrix);
        Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();

        int size = (int) (circle.getDiameter() / matrixParams.getScaleWidth());
        int translationX = (int) matrixParams.getX() ;
        int translationY = (int) matrixParams.getY();

        int y = circle.getTopBound() - translationY;
        y = Math.max(y, 0);
        y /= matrixParams.getScaleWidth();
        int x = circle.getLeftBound() - translationX;
        x = Math.max(x, 0);
        x /= matrixParams.getScaleWidth();

        Logger.log("x: " + x + " y: " + y + " size: " + size);
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap,
                x,
                y,
                size,
                size);

        return croppedBitmap;
    }
}
