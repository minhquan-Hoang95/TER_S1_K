package com.example.demo.models.components.maze;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Adapter pour transformer un labyrinthe "perfect" en labyrinthe Pac-Man jouable
 *
 * Étapes d'adaptation :
 * 1. Ajouter des cycles (rendre imparfait)
 * 2. Appliquer double symétrie
 * 3. Créer Ghost House centrale
 * 4. Ajouter tunnels latéraux
 */
public class PacmanMaze {
    private  Grid grid;

    private static final Random random = new Random();

    public PacmanMaze(Grid grid) {

        this.grid = grid;
    }

    // ------------------------------------------------------------------
    // STEP 1 – BRAID (ADD CYCLES) LIKE JAMIS BUCK, BUT IN JAVA
    // ------------------------------------------------------------------

    // ----------------------------------------------------------
    // MAIN ENTRY POINT
    // ----------------------------------------------------------
//    public Grid generate() {
//
//        // 1 — Start with a normal maze
//        grid = new Grid(rows, cols);
//        generatePerfectMaze();
//
//        // 2 — Braid it (remove dead-ends)
//        braidMaze(1.0); // 100% braid → no dead ends
//
//        // 3 — Horizontal symmetry (real Pac-Man)
//        applyHorizontalSymmetry();
//
//        // 4 — Carve rooms
//        carveMainRooms();
//
//        // 5 — Carve long central corridors
//        carveStraightHallways();
//
//        // 6 — Add ghost house
//        carveGhostHouse();
//
//        // 7 — Add wrap-around tunnels
//        addTunnel();
//
//        return grid;
//    }

    // ----------------------------------------------------------
    // STEP 1 — CLASSICAL MAZE (Recursive Backtracker)
    // ----------------------------------------------------------
    private void generatePerfectMaze() {
        Stack<Cell> stack = new Stack<>();
        Cell start = grid.randomCell();
        Set<Cell> visited = new HashSet<>();
        visited.add(start);
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell cell = stack.peek();
            List<Cell> neighbors = new ArrayList<>();

            if (cell.north != null && !visited.contains(cell.north)) neighbors.add(cell.north);
            if (cell.south != null && !visited.contains(cell.south)) neighbors.add(cell.south);
            if (cell.east  != null && !visited.contains(cell.east))  neighbors.add(cell.east);
            if (cell.west  != null && !visited.contains(cell.west))  neighbors.add(cell.west);

            if (neighbors.isEmpty()) {
                stack.pop();
            } else {
                Cell next = neighbors.get(random.nextInt(neighbors.size()));
                cell.link(next);
                visited.add(next);
                stack.push(next);
            }
        }
    }

    // ----------------------------------------------------------
    // STEP 2 — BRAID (REMOVE DEAD-ENDS)
    // ----------------------------------------------------------
    public void braidMaze(double p) {
        List<Cell> deadEnds = new ArrayList<>(grid.deadEnds());
        Collections.shuffle(deadEnds, random);

        for (Cell cell : deadEnds) {

            // Still a dead end?
            if (cell.links().size() != 1) continue;
            if (random.nextDouble() > p) continue;

            // Collect neighbors not linked yet
            List<Cell> neighbors = new ArrayList<>();
            for (Cell n : cell.neighbors())
                if (!cell.isLinked(n))
                    neighbors.add(n);

            if (neighbors.isEmpty()) continue;

            // Prefer linking to other dead ends
            List<Cell> best = new ArrayList<>();
            for (Cell n : neighbors)
                if (n.links().size() == 1)
                    best.add(n);

            List<Cell> selection = best.isEmpty() ? neighbors : best;
            Cell neighbor = selection.get(random.nextInt(selection.size()));

            cell.link(neighbor);
        }
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

    /** Iterate over each cell in the grid */
    public Iterable<Cell> eachCell() {
        List<Cell> list = new ArrayList<>();
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getColumns(); j++) {
                list.add(grid.getGrid()[i][j]);
            }
        }
        return list;
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

    // ----------------------------------------------------------
    // STEP 3 — HORIZONTAL SYMMETRY ONLY
    // ----------------------------------------------------------
    public void applyHorizontalSymmetry() {
        int mid = grid.getColumns() / 2;

        // Step 1: Clear right half
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = mid; c < grid.getColumns(); c++) {
                Cell cell = grid.getCell(r, c);
                for (Cell link : new ArrayList<>(cell.links())) {
                    cell.unlink(link);
                }
            }
        }

        // Step 2: Mirror left to right
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < mid; c++) {
                Cell left = grid.getCell(r, c);
                Cell right = grid.getCell(r, grid.getColumns() - 1 - c);

                for (Cell n : left.links()) {
                    int nr = n.row;
                    int nc = n.col;

                    Cell mirrorNeighbor = grid.getCell(nr, grid.getColumns() - 1 - nc);
                    if (mirrorNeighbor != null) {
                        right.link(mirrorNeighbor);
                    }
                }
            }
        }
    }

    // ----------------------------------------------------------
    // STEP 4 — CARVE PAC-MAN ROOMS
    // ----------------------------------------------------------
    public void carveMainRooms() {
        // Example room sizes (adjustable for your grid)
        carveRoom(3, 3, 6, 8); // top-left
        carveRoom(3, grid.getColumns() - 9, 6, grid.getColumns() - 4); // top-right

        carveRoom(grid.getRows() - 7, 3, grid.getRows() - 4, 8); // bottom-left
        carveRoom(grid.getRows() - 7, grid.getColumns() - 9, grid.getRows() - 4, grid.getColumns() - 4); // bottom-right
    }

    public void carveRoom(int r1, int c1, int r2, int c2) {
        for (int r = r1; r <= r2; r++) {
            for (int c = c1; c <= c2; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell == null) continue;

                for (Cell link : new ArrayList<>(cell.links())) {
                    cell.unlink(link);
                }
            }
        }
    }

    // ----------------------------------------------------------
    // STEP 5 — CARVE LONG STRAIGHT HALLWAYS
    // ----------------------------------------------------------
    public void carveStraightHallways() {
        int midRow = grid.getRows() / 2;

        // Horizontal highway
        carveHorizontalLine(midRow - 5);
        carveHorizontalLine(midRow + 5);

        // Central vertical column
        carveVerticalLine(grid.getColumns() / 2 - 1);
        carveVerticalLine(grid.getColumns() / 2 + 1);
    }

    public void carveHorizontalLine(int row) {
        for (int c = 0; c < grid.getColumns(); c++) {
            Cell cell = grid.getCell(row, c);
            if (cell == null) continue;

            for (Cell link : new ArrayList<>(cell.links())) {
                cell.unlink(link);
            }
        }
    }

    public void carveVerticalLine(int col) {
        for (int r = 0; r < grid.getRows(); r++) {
            Cell cell = grid.getCell(r, col);
            if (cell == null) continue;

            for (Cell link : new ArrayList<>(cell.links())) {
                cell.unlink(link);
            }
        }
    }

    // ----------------------------------------------------------
    // STEP 6 — GHOST HOUSE
    // ----------------------------------------------------------
    public void carveGhostHouse() {
        int midRow = grid.getRows() / 2;
        int midCol = grid.getColumns() / 2;

        carveRoom(midRow - 1, midCol - 2, midRow + 1, midCol + 2);

        // Add door
        Cell door = grid.getCell(midRow + 1, midCol);
        Cell outside = grid.getCell(midRow + 2, midCol);
        if (door != null && outside != null) {
            door.link(outside);
        }
    }

    // ----------------------------------------------------------
    // STEP 7 — TUNNEL
    // ----------------------------------------------------------
    public void addTunnel() {
        int mid = grid.getRows() / 2;
        Cell left = grid.getCell(mid, 0);
        Cell right = grid.getCell(mid, grid.getColumns() - 1);
        if (left != null && right != null) left.link(right);
    }
    public Grid result() {
        return grid;
    }

}
