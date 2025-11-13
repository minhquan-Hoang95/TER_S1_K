package com.pacman.model.components.maze;

import com.pacman.game.model.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the full maze environment for a Pac-Man game.
 * Contains:
 *  - The logical grid of cells (links, coordinates, connectivity)
 *  - The visual/semantic map (walls, pellets, power-ups, etc.)
 *  - Spawn points for Pac-Man, ghosts, and fruits.
 *
 * This class acts as the "world" that other game entities (Pacman, Ghosts)
 * interact with and navigate through.
 */
public class MazeEnvironment {
    /** Logical grid structure representing cell adjacency (used by pathfinding) */
    private final Grid grid;

    /** 2D layout for rendering and collision logic */
    private final TilePane[][] tiles;

    /** Pac-Man's spawn position */
    private Cell pacmanSpawn;

    /** Spawn points for all ghosts */
    private final List<Cell> ghostSpawns = new ArrayList<>();

    /** Fruit spawn points */
    private final List<Cell> fruitSpawns = new ArrayList<>();



    /**
     * Creates a maze environment using an existing Grid.
     * The Grid defines connectivity (cells and links),
     * while MazeEnvironment adds semantic meaning (walls, pellets, etc.)
     */
    public MazeEnvironment(Grid grid) {
        this.grid = grid;
        this.tiles = new TilePane[grid.getRows()][grid.getColumns()];

        //
        fillDefaults();

        // Later, you can call custom generation to add walls and features
        addWalls();
    }

    private void addWalls() {
        // Example: Add border walls
        for (int r = 0; r < grid.getRows(); r++) {
            setTile(r, 0, TilePane.WALL);
            setTile(r, grid.getColumns() - 1, TilePane.WALL);
        }

        for (int c = 0; c < grid.getColumns(); c++) {
            setTile(0, c, TilePane.WALL);
            setTile(grid.getRows() - 1, c, TilePane.WALL);
        }
    }

    private void fillDefaults() {
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getColumns(); c++) {
                tiles[r][c] = TilePane.PATH;
            }
        }
    }

    // ------------------------------------------------------------
    // 🔹 Tile Management
    // ------------------------------------------------------------

    /**
     * Changes the tile type at a given location.
     */
    public void setTile(int row, int col, TilePane tile) {
        if (isValid(row, col)) {
            tiles[row][col] = tile;
        }
    }

    /**
     * Returns the tile type at a given position.
     */
    public TilePane getTile(int row, int col) {
        if (isValid(row, col)) {
            return tiles[row][col];
        }
        return TilePane.WALL; // Fallback: treat invalid cells as walls
    }

    /** Checks whether a tile is inside the maze boundaries. */
    private boolean isValid(int row, int col) {
        return row >= 0 && row < grid.getRows() && col >= 0 && col < grid.getColumns();
    }

    // ------------------------------------------------------------
    // 🔹 Spawns Management
    // ------------------------------------------------------------

    public void setPacmanSpawn(Cell cell) {
        pacmanSpawn = cell;
        setTile(cell.getRow(), cell.getCol(), TilePane.PACMAN_SPAWN);
    }

    public Cell getPacmanSpawn() {
        return pacmanSpawn;
    }

    public void addGhostSpawn(Cell cell) {
        ghostSpawns.add(cell);
        setTile(cell.getRow(), cell.getCol(), TilePane.GHOST_SPAWN);
    }

    public List<Cell> getGhostSpawns() {
        return ghostSpawns;
    }

    public void addFruitSpawn(Cell cell) {
        fruitSpawns.add(cell);
        setTile(cell.getRow(), cell.getCol(), TilePane.FRUIT_SPAWN);
    }

    public List<Cell> getFruitSpawns() {
        return fruitSpawns;
    }

    // ------------------------------------------------------------
    // 🔹 Accessors
    // ------------------------------------------------------------

    public Grid getGrid() {
        return grid;
    }

    /** Checks if a given position corresponds to a wall tile. */
    public boolean isWall(int row, int col) {
        return getTile(row, col) == TilePane.WALL;
    }

    // ------------------------------------------------------------
    // 🔹 Debugging / Visualization
    // ------------------------------------------------------------

    /**
     * Prints a simple ASCII visualization of the maze for debugging.
     */
    public void printConsole() {
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getColumns(); c++) {
                switch (tiles[r][c]) {
                    case PATH -> System.out.print(" ");
                    case WALL -> System.out.print("#");
                    case PELLET -> System.out.print(".");
                    case POWER_PELLET -> System.out.print("o");
                    case PACMAN_SPAWN -> System.out.print("P");
                    case GHOST_SPAWN -> System.out.print("G");
                    case FRUIT_SPAWN -> System.out.print("F");
                }
            }
            System.out.println();
        }
    }

}
