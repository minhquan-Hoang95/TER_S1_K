package com.pacman.game.model;

import java.util.*;

/**
 *
 * Stores the distance (in steps) from a root Cell to other reachable Cells.
 * Useful for pathfinding and AI in maze-based games like Pac-Man.
 */
public class Distances {

    private final Cell root;
    private final Map<Cell, Integer> cells = new HashMap<>();

    /**
     * Creates a new Distances map starting from the given root cell.
     */
    public Distances(Cell root) {
        this.root = root;
        this.cells.put(root, 0);
    }

    /** Gets the distance for a cell. Returns null if unknown. */
    public Integer get(Cell cell) {
        return cells.get(cell);
    }

    /** Sets the distance for a cell. */
    public void put(Cell cell, int distance) {
        cells.put(cell, distance);
    }

    /** Returns the set of all cells stored in this distance map. */
    public Set<Cell> cells() {
        return cells.keySet();
    }

    /**
     * Reconstructs the shortest path from the root to a given goal cell.
     * It follows the distance breadcrumbs backwards from the goal.
     */
    public Distances pathTo(Cell goal) {
        Cell current = goal;
        Distances breadcrumbs = new Distances(root);
        breadcrumbs.put(current, cells.get(current));

        while (!current.equals(root)) {
            for (Cell neighbor : current.links()) {
                if (cells.containsKey(neighbor) && cells.get(neighbor) < cells.get(current)) {
                    breadcrumbs.put(neighbor, cells.get(neighbor));
                    current = neighbor;
                    break;
                }
            }
        }

        return breadcrumbs;
    }

    /**
     * Finds the cell farthest from the root, along with its distance.
     * @return A Map.Entry<Cell, Integer> with the farthest cell and its distance.
     */
    public Map.Entry<Cell, Integer> max() {
        int maxDistance = 0;
        Cell maxCell = root;

        for (Map.Entry<Cell, Integer> entry : cells.entrySet()) {
            if (entry.getValue() > maxDistance) {
                maxCell = entry.getKey();
                maxDistance = entry.getValue();
            }
        }

        return Map.entry(maxCell, maxDistance);
    }

    public Cell getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return "Distances{root=" + root + ", size=" + cells.size() + "}";
    }

    // Find the cell with the minimum distance from the root
    public Map.Entry<Cell, Integer> min() {
        int minDistance = Integer.MAX_VALUE;
        Cell minCell = root;
        for (Map.Entry<Cell, Integer> entry : cells.entrySet()) {
            if (entry.getValue() < minDistance) {
                minCell = entry.getKey();
                minDistance = entry.getValue();
            }
        }
        return Map.entry(minCell, minDistance);

    }
}
