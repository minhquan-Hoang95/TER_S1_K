package com.pacman.model.components.maze;

import com.badlogic.gdx.graphics.Color;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a grid of cells.
 */
public class Grid {
    private final int rows, columns  ;
    private final Cell[][] grid;

    /**
     * Constructs a Grid with the specified number of rows and columns.
     *
     * @param rows    the number of rows in the grid
     * @param columns the number of columns in the grid
     */
    public Grid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.grid = prepareGrid();
        configureCells();
    }

    /**
     * Prepares the grid by initializing each cell.
     * @return a 2D array of initialized cells
     */
    private Cell[][] prepareGrid() {
        Cell[][] grid = new Cell[rows][columns];
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
        return grid;
    }

    /**
     * Configures the cells in the grid by setting their neighbors.
     */
    private void configureCells() {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {

                Cell cell = grid[i][j];
                cell.north = getCell(i - 1, j); // Get the northern neighbor
                cell.south = getCell(i + 1, j); // Get the southern neighbor
                cell.west = getCell(i, j - 1); // Get the western neighbor
                cell.east = getCell(i, j + 1); // Get the eastern neighbor

            }
        }
    }

    public Cell getCell(int row, int column) {
        if(row < 0 || row >= rows) return null;
        if(column < 0  || column >= columns) return null;

        return grid[row][column];
    }

    // Getter methods
    public int getRows() {
        return rows;
    }
    public int getColumns() {
        return columns;
    }

    /** Get random cell from the grid */
    public Cell randomCell() {
        Random rand = new Random();
        int row = rand.nextInt(rows);
        int column = rand.nextInt(columns);
        return getCell(row, column);

    }

    /** Get total number of cells in the grid */
    public int size() {
        return rows * columns;
    }


    /** Iterate over each cell in the grid */
    public Iterable<Cell> eachCell() {
        List<Cell> list = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                list.add(grid[i][j]);
            }
        }
        return list;
    }
    /** using Comsumer functional interface to apply action on each cell */
    public void forEachCell(Consumer<Cell> action) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                action.accept(grid[i][j]);
            }
        }
    }




    public Iterable<Cell[]> eachRow() {
        return Arrays.asList(grid);
    }
    /** for each row */
    public void forEachRow(Consumer<Cell[]> action) {
        for(Cell[] row : grid) {
            action.accept(row);
        }
    }

    public String contentsOf(Cell cell) {
        return " ";
    }

    /** Background color of the cell */
    public Color backgroundColorOf(Cell cell) {
        return null;
    }

    /**
     * Converts the grid to an ASCII representation.
     */
    /** Equivalent to Ruby's to_s_v2 – renders the maze as ASCII text */
    public String toAscii()
    {
        // StringBuilder to build the ASCII representation
        StringBuilder output = new StringBuilder();

        output.append("+").append("---+".repeat(columns)).append("\n");
        for (int i = 0; i < rows; i++) {
            StringBuilder top = new StringBuilder("|");
            StringBuilder bottom = new StringBuilder("+");

            for (int j = 0; j < columns; j++) {
                Cell cell = grid[i][j];
                if (cell == null) continue;

                String body = " " + contentsOf(cell) + " ";

                // Determine the east boundary
                String eastBoundary = cell.isLinked(cell.east) ? " " : "|";
                top.append(body).append(eastBoundary);

                // Determine the south boundary
                String southBoundary = cell.isLinked(cell.south) ? "   " : "---";
                bottom.append(southBoundary).append("+");
            }
            output.append(top).append("\n");
            output.append(bottom).append("\n");
        }

        return output.toString();
    }

    @Override
    public String toString() {
        return toAscii();
    }

    /** Dead ends in the grid */
    public List<Cell> deadEnds() {
        List<Cell> deadEnds = new ArrayList<>();
        for (Cell cell : eachCell()) {
            if (cell.links().size() == 1) {
                deadEnds.add(cell);
            }
        }
        return deadEnds;
    }

    /** Braid the maze by removing dead ends with probability p (0.0–1.0) */
    public void braid(double p) {
        List<Cell> ends = new ArrayList<>(deadEnds());
        Collections.shuffle(ends);
        Random rand = new Random();
        for (Cell cell : ends) {
            if (cell.links().size() != 1 || rand.nextDouble() > p) continue;

            // Find unlinked neighbors
            List<Cell> neighbors = new ArrayList<>();
            for (Cell n : cell.neighbors()) {
                if (!cell.isLinked(n)) neighbors.add(n);
            }

            if (neighbors.isEmpty()) continue;

            // Prefer neighbors that are also dead ends
            List<Cell> best = new ArrayList<>();
            for (Cell n : neighbors)
                if (n.links().size() == 1)
                    best.add(n);

            if (best.isEmpty()) best = neighbors;

            Cell neighbor = best.get(rand.nextInt(best.size()));
            cell.link(neighbor, true);
        }
    }
    public String info() {
        return String.format("Grid %dx%d | Cells=%d | Dead ends=%d",
            rows, columns, rows * columns, deadEnds().size());
    }







}

