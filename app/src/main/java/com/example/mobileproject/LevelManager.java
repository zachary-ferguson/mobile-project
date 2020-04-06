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
        levels.add(new Level(obstacles, 1, 4,10000,5000));

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
        levels.add(new Level(obstacles, 1, 4,10000,5000));

        // Level 3
        obstacles = new ArrayList<>();
        //Sand
        obstacles.add(new Obstacle(4,7,0,5,3));
        //Goal
        obstacles.add(new Obstacle(8.75,9.25,2.25,2.75,2));
        levels.add(new Level(obstacles, 1, 2.5, 10000, 5000));

        // Level 4
        obstacles = new ArrayList<>();
        //Water
        obstacles.add(new Obstacle(4,7,0,2,1));
        obstacles.add(new Obstacle(4,7,3,5,1));
        //Sand
        obstacles.add(new Obstacle(4,7,2,3,3));
        //Goal
        obstacles.add(new Obstacle(8.75,9.25,2.25,2.75,2));
        levels.add(new Level(obstacles, 1, 2.5, 10000, 5000));

        // Level 5
        obstacles = new ArrayList<>();
        //Wall
        obstacles.add(new Obstacle(3,5,4,5,0));
        // Water
        obstacles.add(new Obstacle(6,7,0,4,1));
        // Sand
        obstacles.add(new Obstacle(3,5,0,4,3));
        // Goal
        obstacles.add(new Obstacle(8.5,9,0.75,1.25,2));
        levels.add(new Level(obstacles, 1, 2.5, 10000, 5000));

        // Level 6
        obstacles = new ArrayList<>();
        //Wall
        obstacles.add(new Obstacle(0,10,0,0.5,0));
        obstacles.add(new Obstacle(0,10,4.5,5,0));
        obstacles.add(new Obstacle(0,0.5,0,5,0));
        obstacles.add(new Obstacle(9.5,10,0,5,0));
        obstacles.add(new Obstacle(1.25,1.75,1.25,5,0));
        obstacles.add(new Obstacle(1.25,8.75,1.25,1.75,0));
        obstacles.add(new Obstacle(8.25,8.75,1.25,3.75,0));
        obstacles.add(new Obstacle(3.25,8.75,3.25,3.75,0));
        // Goal
        obstacles.add(new Obstacle(7.25,7.75,2.25,2.75,2));
        levels.add(new Level(obstacles, 0.85, 4, 10000, 5000));

    }

    public Level getLevel(int id){
        return levels.get(id);
    }
}
