package com.pacman.game.model;

import com.pacman.game.model.Cell;

/**
 * Enum des 4 directions cardinales + NONE
 * Type-safe pour éviter les erreurs "north" vs "North" vs "NORTH"
 * Contient aussi la logique de calcul des voisins
 */
public enum Direction {
    // Les 4 directions + offset (dRow, dCol)
    UP(-1, 0),      // Aller haut = -1 ligne
    DOWN(1, 0),     // Aller bas = +1 ligne
    LEFT(0, -1),    // Aller gauche = -1 colonne
    RIGHT(0, 1),    // Aller droite = +1 colonne
    NONE(0, 0);     // Pas de mouvement

    // Stockage des offsets pour calcul rapide
    private final int dRow;  // Delta ligne
    private final int dCol;  // Delta colonne

    // Constructeur initialisant les offsets
    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    // Getter pour changement de ligne
    public int dRow() {
        return dRow;
    }

    // Getter pour changement de colonne
    public int dCol() {
        return dCol;
    }

    /**
     * Obtenir la cellule voisine dans cette direction
     * Retourne null si on essaie NONE ou si voisin n'existe pas
     *
     * Exemple: Direction.UP.neighborOf(cell) retourne cell.north
     */
    public Cell neighborOf(Cell cell) {
        if (cell == null) return null;

        // Utiliser switch expression pour retourner directement
        return switch (this) {
            case UP -> cell.north;        // Voisin vers le haut
            case DOWN -> cell.south;      // Voisin vers le bas
            case LEFT -> cell.west;       // Voisin vers la gauche
            case RIGHT -> cell.east;      // Voisin vers la droite
            case NONE -> null;            // Pas de voisin
        };
    }

    /**
     * Obtenir la direction opposée
     * Utilisé pour: ghost logic, tunnel wrap-around, etc.
     *
     * Exemple: Direction.UP.opposite() retourne Direction.DOWN
     */
    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;       // Opposé de haut = bas
            case DOWN -> UP;       // Opposé de bas = haut
            case LEFT -> RIGHT;    // Opposé de gauche = droite
            case RIGHT -> LEFT;    // Opposé de droite = gauche
            case NONE -> NONE;     // Opposé de rien = rien
        };
    }
}
