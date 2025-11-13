package com.pacman.game.model;

import com.pacman.game.model.Cell;
import com.pacman.game.model.Direction;
import java.util.*;
import java.util.function.Consumer;

/**
 * Classe container pour un maze reçu de l'API
 * Représente la structure physique du labyrinthe
 * Fournit des méthodes pour:
 * - Affichage (accès cellules)
 * - Collision Pac-Man (canMove)
 * - Pathfinding fantômes (findPath, BFS)
 */
public class Maze {
    // ===== DONNÉES =====
    public String id;           // ID unique du maze (pour API)
    public int rows;            // Nombre de lignes
    public int cols;            // Nombre de colonnes
    public Cell[][] cells;      // Grille 2D de cellules

    /**
     * Constructeur simple
     */
    public Maze(String id, int rows, int cols, Cell[][] cells) {
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
    }

    // ===== AFFICHAGE =====

    /**
     * Obtenir cellule à position (row, col) avec vérification limites
     * Retourne null si hors limites (plutôt que exception)
     *
     * Utilisé pour: accès rapide par coordonnées, sécurisé
     */
    public Cell getCellAt(int row, int col) {
        // Vérifier que les coordonnées sont valides
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;  // Hors limites = null au lieu de crash
        }
        return cells[row][col];
    }

    // get number of cells
    public int size() {
        return rows * cols;
    }

    /** isWall between two cells */
    public boolean isWall(Cell from, Cell to) {
        if (from == null || to == null) return true;
        return !from.isLinked(to);
    }

    /**
     * Vérifier si coordonnées sont valides (pour tests)
     */
    public boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Obtenir cellule aléatoire du maze
     * Utilisé pour: spawn initial, respawn après mort
     */
    public Cell getRandomCell() {
        int row = (int)(Math.random() * rows);  // Aléatoire 0 à rows-1
        int col = (int)(Math.random() * cols);  // Aléatoire 0 à cols-1
        return cells[row][col];
    }

    /**
     * Obtenir cellule au centre du maze
     * Utilisé pour: spawn par défaut de Pac-Man
     */
    public Cell getCenterCell() {
        return getCellAt(rows / 2, cols / 2);
    }

    // ===== COLLISION & MOUVEMENT =====

    /**
     * Vérifier si mouvement est possible dans une direction
     * Regarde: 1) existe-t-il un voisin? 2) est-il linkké (pas de mur)?
     *
     * Utilisé pour: Pac-Man collision, fantômes vérification mouvement
     */
    public boolean canMove(Cell from, Direction dir) {
        if (from == null || dir == null) return false;  // Pas de data = pas possible

        Cell to = dir.neighborOf(from);  // Obtenir voisin dans la direction
        // Possible si: voisin existe ET linkké (pas de mur)
        return to != null && from.isLinked(to);
    }

    /**
     * Obtenir voisin dans une direction (peut être null)
     * Wrapper simple autour de Direction
     */
    public Cell getNeighbor(Cell cell, Direction dir) {
        if (cell == null || dir == null) return null;
        return dir.neighborOf(cell);
    }

    /**
     * Obtenir TOUS les voisins valides (linkés, pas de mur)
     * Utilisé pour: pathfinding (explorer options), IA mouvement aléatoire
     */
    public List<Cell> getValidNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        if (cell == null) return neighbors;

        // Tester chaque direction (sauf NONE)
        for (Direction dir : Direction.values()) {
            if (dir == Direction.NONE) continue;  // NONE pas intéressant

            Cell neighbor = dir.neighborOf(cell);
            // Ajouter si: existe ET linkké
            if (neighbor != null && cell.isLinked(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    /**
     * Obtenir toutes les directions valides depuis une cellule
     * Utilisé pour: interface UI, IA décision
     */
    public List<Direction> getValidDirections(Cell cell) {
        List<Direction> directions = new ArrayList<>();
        if (cell == null) return directions;

        // Tester chaque direction (sauf NONE)
        for (Direction dir : Direction.values()) {
            if (dir == Direction.NONE) continue;
            // Ajouter si mouvement possible
            if (canMove(cell, dir)) {
                directions.add(dir);
            }
        }
        return directions;
    }

    // ===== PATHFINDING (Phase 5 - IA) =====

    /**
     * BFS (Breadth-First Search) pour pathfinding
     * Retourne liste des cellules du start au goal
     *
     * Utilisé pour: IA fantômes, trouver chemin optimal
     */
    public List<Cell> findPath(Cell start, Cell goal) {
        // Cas trivials
        if (start == null || goal == null) return new ArrayList<>();
        if (start == goal) {
            List<Cell> path = new ArrayList<>();
            path.add(start);
            return path;
        }

        // Préparation BFS
        Map<Cell, Cell> cameFrom = new HashMap<>();  // Pour reconstruction chemin
        Queue<Cell> queue = new LinkedList<>();      // Files d'attente BFS
        Set<Cell> visited = new HashSet<>();         // Cellules déjà traitées

        // Initialiser avec start
        queue.add(start);
        visited.add(start);
        cameFrom.put(start, null);  // start n'a pas de parent

        // BFS: parcourir en largeur
        while (!queue.isEmpty()) {
            Cell current = queue.poll();  // Récupérer prochaine cellule

            // Arrêt si goal trouvé
            if (current == goal) {
                // Reconstruction du chemin en remontant les parents
                List<Cell> path = new ArrayList<>();
                Cell node = goal;
                while (node != null) {
                    path.add(0, node);  // Ajouter au début
                    node = cameFrom.get(node);  // Parent suivant
                }
                return path;
            }

            // Explorer voisins du current
            for (Cell neighbor : getValidNeighbors(current)) {
                if (!visited.contains(neighbor)) {  // Pas déjà traité
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);  // Mémoriser parent
                    queue.add(neighbor);  // Ajouter à queue
                }
            }
        }

        // Pas de chemin trouvé
        return new ArrayList<>();
    }

    /**
     * Distance Manhattan (approximation rapide)
     * Calcul: |row1 - row2| + |col1 - col2|
     * Utilisé pour: heuristique IA simple, pas précis dans labyrinthe
     */
    public int manhattanDistance(Cell from, Cell to) {
        if (from == null || to == null) return Integer.MAX_VALUE;
        return Math.abs(from.row - to.row) + Math.abs(from.col - to.col);
    }

    /**
     * Distance RÉELLE dans le labyrinthe
     * Calcul: nombre de liens à suivre (chemin optimal)
     * Utilisé pour: IA intelligent, calcul distances exactes
     */
    public int mazeDistance(Cell from, Cell to) {
        List<Cell> path = findPath(from, to);
        // Distance = nombre d'étapes - 1 (ou MAX si pas accessible)
        return path.isEmpty() ? Integer.MAX_VALUE : path.size() - 1;
    }

    // ===== UTILITAIRES =====

    /**
     * Itérer toutes les cellules avec fonction
     * Utilisé pour: initialisation pellets, analyses batch
     */
    public void forEachCell(CellConsumer consumer) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                consumer.accept(cells[r][c]);  // Appeler fonction pour chaque cell
            }
        }
    }

    /**
     * Obtenir list de TOUTES les cellules
     * Utilisé pour: analyses, collections
     */
    public List<Cell> getAllCells() {
        List<Cell> allCells = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                allCells.add(cells[r][c]);
            }
        }
        return allCells;
    }

    /**
     * Compter cellules accessibles (connectées par liens)
     * Utilisé pour: vérifier structure maze, validation
     */
    public int countAccessibleCells() {
        Set<Cell> accessible = new HashSet<>();  // Set pour éviter duplicates
        if (cells[0][0] == null) return 0;

        // BFS simple pour explorer toutes cellules connectées
        Queue<Cell> queue = new LinkedList<>();
        queue.add(cells[0][0]);
        accessible.add(cells[0][0]);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            for (Cell neighbor : getValidNeighbors(current)) {
                if (!accessible.contains(neighbor)) {
                    accessible.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return accessible.size();
    }

    /**
     * Info debug/affichage sur le maze
     */
    public String getInfo() {
        int accessible = countAccessibleCells();
        int total = rows * cols;
        return String.format("Maze %s: %dx%d (%d/%d cells accessible)",
            id, rows, cols, accessible, total);
    }

    @Override
    public String toString() {
        return getInfo();
    }

    // ===== INTERFACE FONCTIONNELLE =====

    /**
     * Interface pour lambda: accepter une cellule
     * Utilisé pour forEachCell
     */
    @FunctionalInterface
    public interface CellConsumer {
        void accept(Cell cell);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    /** Iterate over each cell in the grid */
    public Iterable<Cell> eachCell() {
        List<Cell> list = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                list.add(cells[i][j]);
            }
        }
        return list;
    }
    /** using Comsumer functional interface to apply action on each cell */
    public void forEachCell(Consumer<Cell> action) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                action.accept(cells[i][j]);
            }
        }
    }




    public Iterable<Cell[]> eachRow() {
        return Arrays.asList(cells);
    }
    /** for each row */
    public void forEachRow(Consumer<Cell[]> action) {
        for(Cell[] row : cells) {
            action.accept(row);
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
}
