package com.example.mobileproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

import static android.graphics.Color.rgb;

public class ObstacleManager {
    private ArrayList<Obstacle> obstacles;
    private long startTime;
    private Bitmap waterImage;

    public ObstacleManager() {
        //this.playerGap = playerGap;
        startTime =  System.currentTimeMillis();
        BitmapFactory bf = new BitmapFactory();
        waterImage = bf.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.water);

        obstacles = new ArrayList<>();

    }

    public ArrayList<Obstacle> playerCollide(CirclePlayer player) {
        ArrayList<Obstacle> collide = new ArrayList<>();
        for(Obstacle ob : obstacles){
            if(ob.playerCollide(player))
                collide.add(ob);
        }
        return collide;
    }

    public void createObstacles(Level levelIn) {
        obstacles = levelIn.getObstacles();
    }

    public void update(){
        if(startTime < Constants.INIT_TIME)
            startTime = Constants.INIT_TIME;

    }
    public void draw(Canvas canvas){
        /**
         * Playing around with images for textures
        for(Obstacle ob : obstacles)
            if(ob.getType() == 1){
                canvas.drawBitmap(waterImage, null, ob.getRectangle(), new Paint());
            } else {
                ob.draw(canvas);
            }
         */
        for(Obstacle ob : obstacles)
            ob.draw(canvas);
    }
}
