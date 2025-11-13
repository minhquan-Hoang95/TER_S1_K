package com.example.demo.models.components.maze;


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
//    public Color backgroundColorOf(Cell cell) {
//        return null;
//    }

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

    public Cell[][] getGrid() {
        return grid;
    }


    // ==================== FONCTIONS DE VÉRIFICATION ====================

    /**
     * Vérifier si le maze est symétrique HORIZONTALEMENT (gauche ↔ droite)
     *
     * @return true si parfaitement symétrique horizontalement
     */
    public boolean isHorizontallySymmetric() {
        int rows = getRows();
        int cols = getColumns();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols / 2; c++) {
                Cell left = getCell(r, c);
                Cell right = getCell(r, cols - 1 - c);

                if (left == null || right == null) continue;

                // Comparer les liens (miroir horizontal : est ↔ ouest)
                boolean leftNorth = left.north != null && left.isLinked(left.north);
                boolean rightNorth = right.north != null && right.isLinked(right.north);

                boolean leftSouth = left.south != null && left.isLinked(left.south);
                boolean rightSouth = right.south != null && right.isLinked(right.south);

                boolean leftEast = left.east != null && left.isLinked(left.east);
                boolean rightWest = right.west != null && right.isLinked(right.west);

                boolean leftWest = left.west != null && left.isLinked(left.west);
                boolean rightEast = right.east != null && right.isLinked(right.east);

                if (leftNorth != rightNorth || leftSouth != rightSouth ||
                    leftEast != rightWest || leftWest != rightEast) {
                    System.out.println("❌ Asymétrie horizontale à [" + r + "," + c + "]");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Vérifier si le maze est symétrique VERTICALEMENT (haut ↔ bas)
     *
     * @return true si parfaitement symétrique verticalement
     */
    public boolean isVerticallySymmetric() {
        int rows = getRows();
        int cols = getColumns();

        for (int r = 0; r < rows / 2; r++) {
            for (int c = 0; c < cols; c++) {
                Cell top = getCell(r, c);
                Cell bottom = getCell(rows - 1 - r, c);

                if (top == null || bottom == null) continue;

                // Comparer les liens (miroir vertical : nord ↔ sud)
                boolean topNorth = top.north != null && top.isLinked(top.north);
                boolean bottomSouth = bottom.south != null && bottom.isLinked(bottom.south);

                boolean topSouth = top.south != null && top.isLinked(top.south);
                boolean bottomNorth = bottom.north != null && bottom.isLinked(bottom.north);

                boolean topEast = top.east != null && top.isLinked(top.east);
                boolean bottomEast = bottom.east != null && bottom.isLinked(bottom.east);

                boolean topWest = top.west != null && top.isLinked(top.west);
                boolean bottomWest = bottom.west != null && bottom.isLinked(bottom.west);

                if (topNorth != bottomSouth || topSouth != bottomNorth ||
                    topEast != bottomEast || topWest != bottomWest) {
                    System.out.println("❌ Asymétrie verticale à [" + r + "," + c + "]");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Vérifier si le maze est DOUBLEMENT symétrique
     *
     * @return true si symétrique horizontal ET vertical
     */
    public boolean isDoubleSymmetric() {
        return isHorizontallySymmetric() && isVerticallySymmetric();
    }

    /**
     * Vérifier si le maze a une Ghost House (zone centrale 3x3 isolée)
     *
     * @return true si Ghost House présente et correcte
     */
    public boolean hasGhostHouse() {
        int midRow = getRows() / 2;
        int midCol = getColumns() / 2;

        int minRow = midRow - 1;
        int maxRow = midRow + 1;
        int minCol = midCol - 1;
        int maxCol = midCol + 1;

        int doorCount = 0;

        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = getCell(r, c);
                if (cell == null) return false;

                int linkCount = cell.links().size();

                // Les cellules Ghost House doivent être isolées ou avoir 1 lien (porte)
                if (linkCount > 1) {
                    System.out.println("❌ Ghost House cellule [" + r + "," + c + "] a " + linkCount + " liens");
                    return false;
                }
                if (linkCount == 1) doorCount++;
            }
        }

        // Doit avoir exactement 1 porte
        if (doorCount != 1) {
            System.out.println("❌ Ghost House a " + doorCount + " portes (attendu: 1)");
            return false;
        }

        return true;
    }

    /**
     * Vérifier si le maze a les tunnels latéraux
     *
     * @return true si tunnels présents
     */
    public boolean hasTunnels() {
        int midRow = getRows() / 2;
        Cell left = getCell(midRow, 0);
        Cell right = getCell(midRow, getColumns() - 1);

        boolean hasTunnel = left != null && right != null && left.isLinked(right);

        if (!hasTunnel) {
            System.out.println("❌ Pas de tunnel à la ligne " + midRow);
        }

        return hasTunnel;
    }

    /**
     * Vérifier si le maze est IMPARFAIT (a des cycles)
     *
     * Un maze parfait = arbre = pas de cycles
     * Un maze imparfait = graphe avec cycles
     *
     * @return true si le maze contient au moins un cycle
     */
    public boolean hasMultiplePaths() {
        // Compter les liens totaux
        int totalLinks = 0;
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getColumns(); c++) {
                Cell cell = getCell(r, c);
                if (cell != null) {
                    totalLinks += cell.links().size();
                }
            }
        }

        int edges = totalLinks / 2;  // Chaque lien est compté 2 fois
        int vertices = getRows() * getColumns();
        int minEdgesForTree = vertices - 1;

        boolean hasCycles = edges > minEdgesForTree;

        if (!hasCycles) {
            System.out.println("❌ Maze est PARFAIT (pas de cycles). Edges: " + edges + ", Min requis pour cycles: " + (minEdgesForTree + 1));
        }

        return hasCycles;
    }

    /**
     * Vérifier si toutes les zones sont accessibles (connexe)
     *
     * @return true si toutes les cellules (hors Ghost House) sont accessibles
     */
    public boolean isFullyConnected() {
        Set<Cell> visited = new HashSet<>();
        Queue<Cell> queue = new LinkedList<>();

        // Commencer à (0,0)
        Cell start = getCell(0, 0);
        if (start == null) return false;

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            for (Cell neighbor : current.links()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        int totalCells = getRows() * getColumns();
        int accessibleCells = visited.size();

        // On attend au moins 95% des cellules accessibles (Ghost House = 9 cells)
        boolean connected = accessibleCells >= totalCells * 0.95;

        if (!connected) {
            System.out.println("❌ Seulement " + accessibleCells + "/" + totalCells + " cellules accessibles");
        }

        return connected;
    }

    /**
     * VALIDATION COMPLÈTE : vérifie TOUTES les propriétés Pac-Man
     *
     * @return true si le maze respecte toutes les spécifications
     */
    public boolean isPacmanCompliant() {
        System.out.println("\n🔍 VÉRIFICATION MAZE PAC-MAN");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        boolean horizSym = isHorizontallySymmetric();
        System.out.println((horizSym ? "✅" : "❌") + " Symétrie horizontale");

        boolean vertSym = isVerticallySymmetric();
        System.out.println((vertSym ? "✅" : "❌") + " Symétrie verticale");

        boolean cycles = hasMultiplePaths();
        System.out.println((cycles ? "✅" : "❌") + " Maze imparfait (cycles)");

        boolean ghostHouse = hasGhostHouse();
        System.out.println((ghostHouse ? "✅" : "❌") + " Ghost House centrale");

        boolean tunnels = hasTunnels();
        System.out.println((tunnels ? "✅" : "❌") + " Tunnels latéraux");

        boolean connected = isFullyConnected();
        System.out.println((connected ? "✅" : "❌") + " Toutes zones accessibles");

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        boolean compliant = horizSym && vertSym && cycles && ghostHouse && tunnels && connected;
        System.out.println(compliant ? "✅ MAZE VALIDE POUR PAC-MAN" : "❌ MAZE INVALIDE");
        System.out.println();

        return compliant;
    }

    /**
     * Afficher statistiques du maze
     */
    public void printStats() {
        System.out.println("\n📊 STATISTIQUES DU MAZE");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Dimensions: " + getRows() + "x" + getColumns());
        System.out.println("Total cellules: " + (getRows() * getColumns()));
        System.out.println("Dead-ends: " + deadEnds().size());
        System.out.println("Cellules accessibles: " + countAccessibleCells());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Compter cellules accessibles
     */
    private int countAccessibleCells() {
        Set<Cell> visited = new HashSet<>();
        Queue<Cell> queue = new LinkedList<>();

        Cell start = getCell(0, 0);
        if (start == null) return 0;

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            for (Cell neighbor : current.links()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return visited.size();
    }


}

