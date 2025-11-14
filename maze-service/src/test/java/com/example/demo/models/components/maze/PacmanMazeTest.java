package com.example.demo.models.components.maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacmanMazeTest {

    @Test
    void adaptShouldApplyAllTransformations() {
        Grid grid = new Grid(31, 28);
        PacmanMaze maze = new PacmanMaze(grid);
        Grid adaptedGrid = maze.adapt();

        assertNotNull(adaptedGrid);
        assertEquals(31, adaptedGrid.getRows());
        assertEquals(28, adaptedGrid.getColumns());
    }

    @Test
    void addTunnelsShouldCreateHorizontalConnections() {
        Grid grid = new Grid(31, 28);
        PacmanMaze maze = new PacmanMaze(grid);
        maze.addTunnels1();

        int tunnelRow = grid.getRows() / 2;
        for (int c = 0; c < grid.getColumns() - 1; c++) {
            Cell current = grid.getCell(tunnelRow, c);
            Cell next = grid.getCell(tunnelRow, c + 1);
            assertTrue(current.isLinked(next));
        }
    }

    @Test
    void addGhostHouseShouldCreateIsolatedAreaWithDoors() {
        Grid grid = new Grid(31, 28);
        PacmanMaze maze = new PacmanMaze(grid);
        maze.addGhostHouse48();

        int midRow = grid.getRows() / 2;
        int midCol = grid.getColumns() / 2;
        int minRow = midRow - 2;
        int maxRow = midRow + 1;
        int minCol = midCol - 4;
        int maxCol = midCol + 3;

        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                assertNotNull(cell);
                assertFalse(cell.links().isEmpty());
            }
        }

        Cell leftDoor = grid.getCell(maxRow, minCol);
        Cell rightDoor = grid.getCell(maxRow, maxCol);
        assertTrue(leftDoor.isLinked(grid.getCell(maxRow + 1, minCol)));
        assertTrue(rightDoor.isLinked(grid.getCell(maxRow + 1, maxCol)));
    }

    @Test
    void braidMazeShouldReduceDeadEnds() {
        Grid grid = new Grid(31, 28);
        PacmanMaze maze = new PacmanMaze(grid);
        int initialDeadEnds = grid.deadEnds().size();

        maze.braidMaze(0.5);
        int reducedDeadEnds = grid.deadEnds().size();

        assertTrue(reducedDeadEnds < initialDeadEnds);
    }

    @Test
    void applyHorizontalSymmetryShouldMirrorMazeCorrectly() {
        Grid grid = new Grid(31, 28);
        PacmanMaze maze = new PacmanMaze(grid);
        maze.applyHorizontalSymmetry();

        int rows = grid.getRows();
        int cols = grid.getColumns();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols / 2; c++) {
                Cell left = grid.getCell(r, c);
                Cell right = grid.getCell(r, cols - 1 - c);
                assertEquals(left.links().size(), right.links().size());
            }
        }
    }


}
