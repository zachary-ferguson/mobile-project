package com.example.mobileproject;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.ArrayList;

import static android.graphics.Color.rgb;

public class ObstacleManager {
    private ArrayList<Obstacle> obstacles;
    private long startTime;

    public ObstacleManager() {
        //this.playerGap = playerGap;
        startTime =  System.currentTimeMillis();

        obstacles = new ArrayList<>();

    }

    public Obstacle playerCollide(CirclePlayer player) {
        for(Obstacle ob : obstacles){
            if(ob.playerCollide(player))
                return ob;
        }
        return null;
    }

    public void createObstacles(Level levelIn) {
        obstacles = levelIn.getObstacles();
    }

    public void update(){
        if(startTime < Constants.INIT_TIME)
            startTime = Constants.INIT_TIME;

    }
    public void draw(Canvas canvas){
        for(Obstacle ob : obstacles)
            ob.draw(canvas);
    }
}
