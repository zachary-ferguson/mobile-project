package com.example.mobileproject;

import android.graphics.Point;

import java.util.ArrayList;

public class Level {
    private ArrayList<Obstacle> obstacles;
    private double startX;
    private double startY;

    public Level(ArrayList<Obstacle> obstacles, double startX, double startY){
        this.obstacles = obstacles;
        this.startX = startX*Constants.X_UNIT;
        this.startY = startY*Constants.Y_UNIT;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public Point getStart(){
        return new Point((int)startX,(int)startY);
    }
}
