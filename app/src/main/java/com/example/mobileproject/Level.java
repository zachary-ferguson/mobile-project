package com.example.mobileproject;

import android.graphics.Point;

import java.util.ArrayList;

public class Level {
    private ArrayList<Obstacle> obstacles;
    private int startX;
    private int startY;

    public Level(ArrayList<Obstacle> obstacles, int startX, int startY){
        this.obstacles = obstacles;
        this.startX = startX*Constants.X_UNIT;
        this.startY = startY*Constants.Y_UNIT;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public Point getStart(){
        return new Point(startX,startY);
    }
}
