package com.example.mobileproject;

import android.graphics.Point;

import java.util.ArrayList;

public class Level {
    private ArrayList<Obstacle> obstacles;
    private double startX;
    private double startY;
    private long birdieTime;
    private long eagleTime;

    public Level(ArrayList<Obstacle> obstacles, double startX, double startY, long birdieTime, long eagleTime){
        this.obstacles = obstacles;
        this.startX = startX*Constants.X_UNIT;
        this.startY = startY*Constants.Y_UNIT;
        this.birdieTime = birdieTime;
        this.eagleTime = eagleTime;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public Point getStart(){
        return new Point((int)startX,(int)startY);
    }

    public int getStarRank(long msTime) {
        if(msTime < eagleTime){
            return 3;
        } else if(msTime <birdieTime){
            return 2;
        } else {
            return 1;
        }
    }
}
