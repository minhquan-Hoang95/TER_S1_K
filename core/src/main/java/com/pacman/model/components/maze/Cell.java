package com.pacman.model.components.maze;

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
}
