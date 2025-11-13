package com.pacman.model.components.maze.character;

import com.pacman.game.model.Cell;
import com.pacman.game.model.Direction;
import com.pacman.model.components.maze.MazeEnvironment;

public abstract class Ghost extends Character {

    protected GhostMode ghostMode = GhostMode.SCATTER;
    protected final Cell spawnCell;

    protected final MazeEnvironment mazeEnvironment;

    protected boolean frightened = false;

    // Cell where the ghost spawns
    public Ghost(String name, Cell spawnCell, MazeEnvironment mazeEnvironment) {
        super(name, spawnCell);
        this.spawnCell = spawnCell;
this.mazeEnvironment  = mazeEnvironment; }

    public GhostMode getGhostMode() {
        return ghostMode;
    }

    public void setGhostMode(GhostMode ghostMode) {
        this.ghostMode = ghostMode;
    }

    public Cell getSpawnCell() {
        return spawnCell;
    }


    public boolean isFrightened() {
        return frightened;
    }

    public void setFrightened(boolean frightened) {
        this.frightened = frightened;
        if (frightened) this.ghostMode = GhostMode.FRIGHTENED;
    }

//    @Override
//    public void update(float deltaTime) {
//        if(!alive) return; // Dead ghosts don't move
//        // Ghost movement logic can be implemented here
//        switch(ghostMode) {
//            case CHASE -> chase();
//            case SCATTER -> scatter();
//            case FRIGHTENED -> flee();
//            case DEAD -> returnToSpawn();
//            }
//        }
// }


    protected abstract void chase();

    private void returnToSpawn() {

        moveToward(spawnCell);
        if(currentCell.equals(spawnCell)) {
            alive = true;
            ghostMode = GhostMode.SCATTER;
        }
    }

    private void moveToward(Cell spawnCell) {
        // Simple pathfinding logic to move toward the spawn cell
        if(spawnCell == null || currentCell == null) return;

        Cell bestCell = null;
        double minDistance = Double.MAX_VALUE;

        for(Cell neighbor : currentCell.links()) {
           double distance = neighbor.euclideanDistance(spawnCell);

            if(distance < minDistance) {
                minDistance = distance;
                bestCell = neighbor;
            }
        }

        if(bestCell != null) {
            Direction dir = computeDirection(currentCell, bestCell);
            tryMove(dir);
        }

}
    private Direction computeDirection(Cell from, Cell to) {
        if(to.row < from.row) return Direction.UP;
        if(to.row > from.row) return Direction.DOWN;
        if(to.col < from.col) return Direction.LEFT;
        if(to.col > from.col) return Direction.RIGHT;
        return Direction.NONE;
    }
}
