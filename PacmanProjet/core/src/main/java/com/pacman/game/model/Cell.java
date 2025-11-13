package com.pacman.game.model;

import java.util.*;

/**
 * Represents a single cell in the grid.
 * @author Minh
 */

public class Cell {

    public int row, col;
    public Cell north, south, east, west;
    private Map<Cell, Boolean> links;

    /**
     * Constructs a Cell at the specified row and column.
     *
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.links = new HashMap<>();
    }

    /**
     * Links this cell to another cell.
     *
     * @param cell The cell to link to.
     * @param bidi If true, links back from the other cell to this cell.
     */
    public void link(Cell cell, boolean bidi) {
        if (cell == null) return; // ADD THIS LINE
        links.put(cell, true);
        if (bidi) {
            cell.link(this, false);
        }
    }

    public void link(Cell cell) {
        link(cell, true);
    }

    /**
     * Unlinks this cell from another cell.
     *
     * @param cell The cell to unlink from.
     * @param bidi If true, unlinks back from the other cell to this cell.
     */
    public void unlink(Cell cell, boolean bidi) {
        links.remove(cell);
        if (bidi) {
            cell.unlink(this, false);
        }

    }
    public void unlink(Cell cell) {
        unlink(cell, true);
    }

    /**
     * Checks if this cell is linked to another cell.
     *
     * @param cell The cell to check the link with.
     * @return True if linked, false otherwise.
     */
    public boolean isLinked(Cell cell) {
        return  cell != null && links.containsKey(cell);

    }
    /**
     * Gets all the keys of linked cells.
     */
    public Set<Cell> links() {
        return links.keySet();
    }

    /**
     * Returns a list of neighboring cells (north, south, east, west).
     *
     * @return A list of neighboring cells.
     */
    public List<Cell> neighbors() {
        List<Cell> list = new ArrayList<>();
        if (north != null) {
            list.add(north);
        }
        if (south != null) {
            list.add(south);
        }
        if (east != null) {
            list.add(east);
        }
        if (west != null) {
            list.add(west);
        }
        return list;
    }

    /** Getters for row/column */
    public int getRow() { return row; }
    public int getCol() { return col; }


    @Override
    public String toString() {
        return String.format("Cell(%d,%d)", row, col);
    }


    public void copyLinksFrom(Cell originalCell) {
        for (Cell linkedCell : originalCell.links()) {
            this.link(linkedCell, false);
        }
    }

    /**
     * Computes the distances (in number of steps) from this cell to all reachable cells
     * using Breadth-First Search (BFS).
     *
     *
     * @return a Distances object containing the shortest distance to every cell reachable from this one.
     */
    public Distances distances() {
        Distances distances = new Distances(this);
        List<Cell> frontier = new ArrayList<>();
        frontier.add(this);

        while (!frontier.isEmpty()) {
            List<Cell> newFrontier = new ArrayList<>();

            for (Cell cell : frontier) {
                int currentDistance = distances.get(cell);

                for (Cell linked : cell.links()) {
                    // Skip already visited cells
                    if (distances.get(linked) != null) continue;

                    distances.put(linked, currentDistance + 1);
                    newFrontier.add(linked);
                }
            }

            frontier = newFrontier;
        }

        // return the shortest distances map from this cell to all reachable cells
        return distances;
    }

    // translate Cell
    public Cell translate(int dr, int dc) {
        return new Cell(this.row + dr, this.col + dc);
    }

    // equals method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cell other = (Cell) obj;
        return row == other.row && col == other.col;
    }
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    /** Manhattan distance between two cells */
    public int manhattanDistance(Cell other) {
        return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
    }
    /** Euclidean distance between two cells */
    public double euclideanDistance(Cell other) {
        return Math.sqrt(Math.pow(this.row - other.row, 2) + Math.pow(this.col - other.col, 2));
    }

}
