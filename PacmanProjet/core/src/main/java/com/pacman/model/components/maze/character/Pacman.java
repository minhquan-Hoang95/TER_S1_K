package com.pacman.model.components.maze.character;

import com.pacman.game.model.Cell;
import com.pacman.model.components.maze.Grid;

public class Pacman extends Character {
    private int score = 0;
    private int lives;
    private final Grid grid;
    private PacmanController controller;
    public Pacman(Cell startCell, Grid grid) {
        super("Pacman", startCell);
        this.grid = grid;
        this.controller = controller;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public void loseLife()
    {
//        this.lives = Math.max(0, this.lives - 1);
//        if(lives == 0){
//            // Game Over logic can be handled here or in the controller
//
//        }
        if(lives > 0){
            lives--;
        }
        if(lives == 0) {
            kill();
        }
    }

    public void setController(PacmanController controller) {
        this.controller = controller;
    }

//    @Override
//    public void update(float deltaTime) {
//        System.out.println("Nothing to update for Pacman yet.");
//        if(!alive || controller == null) return; // No update if dead or no controller
//
//        // Ask controller for desired direction
//        Direction desiredDirection = controller.decideDirection(this, grid);
//        if(desiredDirection == null) return;
//
//        // Try to move in that direction
//        if(desiredDirection != Direction.NONE) {
//            boolean moved = tryMove(desiredDirection);
//            if(!moved) {
//                System.out.println("Pacman blocked trying to move " + desiredDirection);
//                // You could later add logic to continue in old direction if blocked, etc.
//            }
//        }
//
//        // Additional logic like checking for pellet collisions can be added here
//    }
}
