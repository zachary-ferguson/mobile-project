package com.example.mobileproject;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;

public class CirclePlayer implements GameObject {
    private ShapeDrawable ball;
    private int color;
    private Rect bounds;

    public CirclePlayer(ShapeDrawable ball, int color, int startX, int startY, int width, int height) {
        this.ball = ball;
        int x = startX;
        int y = startY;
        bounds = new Rect(x,y,width+x,height+y);
        this.ball.setBounds(bounds);
        this.color = color;
    }

    public ShapeDrawable getShape(){
        return this.ball;
    }

    @Override
    public void draw(Canvas canvas) {
        ball.getPaint().setColor(color);
        //System.out.println(ball.getPaint().getColor());
        ball.draw(canvas);
    }

    @Override
    public void update() {

    }

    public void update(Point point){
        bounds.set(point.x - bounds.width()/2, point.y - bounds.height()/2, point.x + bounds.width()/2, point.y + bounds.height()/2);
        ball.setBounds(bounds);
    }
}
