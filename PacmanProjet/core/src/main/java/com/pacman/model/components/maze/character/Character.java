package com.pacman.model.components.maze.character;

import com.pacman.game.model.Cell;
import com.pacman.game.model.Direction;

public abstract class Character {
    protected final String name;
    protected Cell currentCell;
    protected Direction direction;
    protected boolean alive = true;

    public Character(String name, Cell startCell) {
        this.name = name;
        this.currentCell = startCell;
        this.direction = Direction.NONE;
    }

    public String getName() {
        return name;
    }

    public Cell getCurrentCell() {
        return currentCell;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
    }

    public void revive(Cell startCell) {
        this.currentCell = startCell;
        this.alive = true;
    }

    /**
     * Try move in the given direction. Returns true if move was successful.
     */
    public boolean tryMove(Direction dir)
    {
        if (!alive) {
            return false; // Dead characters can't move
        }

        Cell neighbor = dir.neighborOf(currentCell);
        if(neighbor == null) {
            return false; // Out of bounds
        }
        // Only move if there's a link (no wall)
        // a wall exists between currentCell and neighbor if they are not linked
        // Mean if there is a link , we can move(no wall)
        // if there is no link, we can't move (there is a wall)
        if(!currentCell.isLinked(neighbor)) {
            return false;
        }
        this.currentCell = neighbor;
        this.direction = dir;
        return true;
    }

    /**
     * Called every frame / tick by the game loop.
     * Subclasses (Pacman, Ghosts) implement their logic here.
     * @param deltaTime time in seconds since last update (from libGDX)
     */
//    public abstract void update(float deltaTime);




}
