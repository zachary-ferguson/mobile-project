package com.example.mobileproject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import static android.graphics.Color.rgb;

public class Obstacle implements GameObject {
    private Rect rectangle;
    private int type; // 0 - Wall 1 - Water 2 - Goal 3 - Sand
    private int colorArr[] = {rgb(171, 171, 171), rgb(28,236,255), rgb(255,213,28), rgb(242, 208, 107)};

    public Obstacle(double left, double right, double top, double bottom, int type) {
        this.rectangle = new Rect((int)(Constants.X_UNIT*left),(int)(Constants.Y_UNIT*top),(int)(Constants.X_UNIT*right),(int)(Constants.Y_UNIT*bottom));
        this.type = type;
    }

    public Rect getRectangle() {
        return rectangle;
    }
    public int getType(){
        return type;
    }

    public boolean playerCollide(CirclePlayer player){
        return Rect.intersects(rectangle, player.getShape().getBounds());
    }

    public void moveDown(float y) {
        rectangle.top += y;
        rectangle.bottom += y;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(colorArr[type]);
        canvas.drawRect(rectangle, paint);
    }
    @Override
    public void update() {

    }
}
