package com.example.mobileproject;

import java.util.ArrayList;

public class LevelManager {
    private ArrayList<Level> levels;

    public LevelManager(){
        ArrayList<Obstacle> obstacles = new ArrayList<>();
        levels = new ArrayList<>();

        // Level 1
        obstacles.add(new Obstacle(2,3,2,5,0));
        obstacles.add(new Obstacle(5,6,0,3,0));
        obstacles.add(new Obstacle(5,8,2,3,0));

        obstacles.add(new Obstacle(6.75,7.25,0.75,1.25,2));
        levels.add(new Level(obstacles, 1, 4));

        // Level 2
        obstacles = new ArrayList<>();
        //Walls
        obstacles.add(new Obstacle(2,3,2,5,0));
        obstacles.add(new Obstacle(5,6,0,3,0));
        obstacles.add(new Obstacle(5,8,2,3,0));
        //Water
        obstacles.add(new Obstacle(0,5,0,1,1));
        obstacles.add(new Obstacle(9,10,0,5,1));
        //Goal
        obstacles.add(new Obstacle(6.75,7.25,0.75,1.25,2));
        levels.add(new Level(obstacles, 1, 4));
    }

    public Level getLevel(int id){
        return levels.get(id);
    }
}
