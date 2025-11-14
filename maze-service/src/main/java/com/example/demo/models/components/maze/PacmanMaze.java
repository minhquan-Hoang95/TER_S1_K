package com.example.demo.models.components.maze;

import java.util.*;

public class PacmanMaze {
    private final Grid grid;
    private static final Random random = new Random();
    private static final int GH_HEIGHT = 4;
    private static final int GH_WIDTH  = 8;



    public PacmanMaze(Grid grid) {
        this.grid = grid;
    }


    /**
     * Appliquer TOUTES les transformations
     */
    public Grid adapt() {
        System.out.println("🎮 Adaptation vers Pac-Man Maze...");
        applyHorizontalSymmetry();
       // makeVerticallySymmetric();  // ÉTAPE 1 : Symétrie
        braidMaze(1.0);            // ÉTAPE 2 : 15% cycles (PAS 100% !)
       addGhostHouse48();// ÉTAPE 3 : Ghost House
        addTunnels1();               // ÉTAPE 4 : Tunnels
        //grid.recomputeNeighbors();
        System.out.println("✅ Adaptation terminée");
        return grid;
    }

    /**
     * ÉTAPE 4 : Create tunnel road
     * Link all cells horizontally at tunnel row so Pacman can traverse left-right
     */
    private void addTunnels2() {
        System.out.println("🚇 Creating tunnel road...");

        int rows = grid.getRows();
        int cols = grid.getColumns();
        int tunnelRow = rows / 2;  // row 15

        System.out.println("   Tunnel row: " + tunnelRow);

        // ✅ STEP 1: Link all cells in the tunnel row horizontally
        // This creates a continuous path from left to right
        for (int c = 0; c < cols - 1 ; c++) {
            Cell current = grid.getCell(tunnelRow, c);
            Cell next = grid.getCell(tunnelRow, c + 1);

            if (current != null && next != null) {
                current.link(next);  // Create connection between adjacent cells
                System.out.println("   ✅ Linked [" + tunnelRow + "," + c + "] ↔ [" + tunnelRow + "," + (c + 1) + "]");
            }
        }

        System.out.println("🚇 Tunnel road complete! (row " + tunnelRow + " fully connected)\n");
    }

    /**
     * ÉTAPE 4 : Créer tunnels horizontaux aux côtés gauche et droit
     *
     * Tunnels permettent Pacman et les fantômes de traverser les murs extérieurs
     * Pour Pac-Man classique: des tunnels aux lignes du milieu
     */
    public void addTunnels1() {
        System.out.println("🚇 Creating tunnels...");

        int rows = grid.getRows();      // 31
        int cols = grid.getColumns();   // 28

        // Tunnels à la ligne du milieu (environ row 15)
        int tunnelRow = rows / 2;       // 15

        System.out.println("   Tunnel row: " + tunnelRow);

        // ✅ LEFT TUNNEL: Connect left edge (col 0) to some internal column
        // LEFT: col 0 ↔ col 1
        Cell leftEdge = grid.getCell(tunnelRow, 0);
        Cell leftInside = grid.getCell(tunnelRow, 1);

        if (leftEdge != null && leftInside != null) {
            leftEdge.link(leftInside);
            System.out.println("   ✅ LEFT tunnel: [" + tunnelRow + ",0] ↔ [" + tunnelRow + ",1]");
        }

        // ✅ RIGHT TUNNEL: Connect right edge (col 27) to some internal column
        // RIGHT: col 27 ↔ col 26
        Cell rightEdge = grid.getCell(tunnelRow, cols - 1);
        Cell rightInside = grid.getCell(tunnelRow, cols - 2);

        if (rightEdge != null && rightInside != null) {
            rightEdge.link(rightInside);
            System.out.println("   ✅ RIGHT tunnel: [" + tunnelRow + "," + (cols - 1) + "] ↔ [" + tunnelRow + "," + (cols - 2) + "]");
        }

        System.out.println("🚇 Tunnels complete!\n");
    }


    /**
     * ÉTAPE 3 : Ghost House 4×8 - ONE CENTER DOOR
     * Remove the middle line dividing the interior
     */
    private void addGhostHouse48new() {
        System.out.println("👻 Creating Ghost House 4×8 (ONE CENTER DOOR)...");

        int rows = grid.getRows();      // 31
        int cols = grid.getColumns();   // 28

        int midRow = rows / 2;          // 15
        int midCol = cols / 2;          // 14

        // Zone 4×8 CENTERED
        int minRow = midRow - 2;        // 13
        int maxRow = midRow + 1;        // 16
        int minCol = midCol - 4;        // 10
        int maxCol = midCol + 3;        // 17

        System.out.println("   Ghost House: rows[" + minRow + "-" + maxRow + "], cols[" + minCol + "-" + maxCol + "]");

        // STEP 1: Clear ALL internal walls
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell != null) {
                    cell.links().clear();
                }
            }
        }

        // ✅ STEP 2: Create COMPLETELY OPEN INTERIOR (no middle line!)
        // Link ALL cells together (north, south, east, west)
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell == null) continue;

                // Link to NORTH
                if (r > minRow) {
                    Cell north = grid.getCell(r - 1, c);
                    if (north != null && north.row >= minRow) {
                        cell.link(north);
                    }
                }

                // Link to EAST (this removes the middle vertical line!)
                if (c < maxCol) {
                    Cell east = grid.getCell(r, c + 1);
                    if (east != null && east.col <= maxCol) {
                        cell.link(east);
                    }
                }

                // Link to WEST (redundant but ensures no vertical divisions)
                if (c > minCol) {
                    Cell west = grid.getCell(r, c - 1);
                    if (west != null && west.col >= minCol) {
                        cell.link(west);
                    }
                }
            }
        }

        System.out.println("   ✅ Interior: COMPLETELY OPEN (no middle line)");

        // ✅ STEP 3: ONE CENTER DOOR at bottom
        Cell door = grid.getCell(maxRow, midCol);
        Cell outside = grid.getCell(maxRow + 1, midCol);

        if (door != null && outside != null) {
            door.link(outside);
            System.out.println("   ✅ Center door: [" + maxRow + "," + midCol + "] ↔ [" + (maxRow + 1) + "," + midCol + "]");
        }

        System.out.println("👻 Ghost House complete!\n");
    }


    /** Tunnels latéraux (wrap-around) façon Pac-Man */
    private void addTunnels() {
        int rows = grid.getRows();
        int cols = grid.getColumns();

        int midRow = rows / 2;

        // ligne des tunnels : juste sous la ghost house
        int tunnelRow = midRow + (GH_HEIGHT / 2) + 1;   // pour 31 rows → 15 + 2 + 1 = 18

        // cellules extrêmes gauche et droite sur cette ligne
        Cell left  = grid.getCell(tunnelRow, 0);
        Cell right = grid.getCell(tunnelRow, cols - 1);

        if (left == null || right == null) return;

        // créer le wrap-around
        left.link(right, true);

        // si tu veux que ce soit un vrai corridor propre :
        // tu peux aussi t'assurer qu'ils sont ouverts vers l'intérieur du maze
        Cell leftInner  = grid.getCell(tunnelRow, 1);
        Cell rightInner = grid.getCell(tunnelRow, cols - 2);

        if (leftInner != null)  left.link(leftInner, true);
        if (rightInner != null) right.link(rightInner, true);
    }


    /**
     * ÉTAPE 3 : Créer Ghost House centrale isolée
     *
     * Le Ghost House est une zone 3×3 au centre du maze où les fantômes spawnet.
     * Il doit être ISOLÉ du reste du maze avec UNE SEULE porte de sortie en bas.
     */
//    private void addGhostHouse() {
//        System.out.println("👻 Creating Ghost House...");
//
//        int rows = grid.getRows();
//        int cols = grid.getColumns();
//
//        // Calculer centre du maze
//        int midRow = rows / 2;      // 31/2 = 15
//        int midCol = cols / 2;      // 28/2 = 14
//
//        // Définir zone 3×3 du Ghost House
//        int minRow = midRow - 1;    // 14
//        int maxRow = midRow + 1;    // 16
//        int minCol = midCol - 1;    // 13
//        int maxCol = midCol + 1;    // 15
//
//        System.out.println("   Ghost House zone: [" + minRow + "-" + maxRow + ", " + minCol + "-" + maxCol + "]");
//
//        // ÉTAPE 1 : ISOLER - Fermer TOUS les liens dans la zone 3×3
//        for (int r = minRow; r <= maxRow; r++) {
//            for (int c = minCol; c <= maxCol; c++) {
//                Cell cell = grid.getCell(r, c);
//                if (cell != null) {
//                    // Clear tous les liens pour isoler complètement
//                    cell.links().clear();
//                }
//            }
//        }
//
//        System.out.println("   ✅ Ghost House isolated (all links cleared)");
//
//        // ÉTAPE 2 : CRÉER PORTE - Ouvrir UNE seule sortie en bas au centre
//        Cell door = grid.getCell(maxRow, midCol);           // Cellule en bas du GH
//        Cell outside = grid.getCell(maxRow + 1, midCol);    // Cellule juste en dessous
//
//        if (door != null && outside != null) {
//            door.link(outside);  // Créer le passage bidirectionnel
//            System.out.println("   ✅ Door created at [" + maxRow + "," + midCol + "] → [" + (maxRow + 1) + "," + midCol + "]");
//        } else {
//            System.out.println("   ⚠️ Warning: Could not create door (null cells)");
//        }
//
//        System.out.println("👻 Ghost House complete!\n");
//    }
    /**
     * Ghost House officielle Pac-Man (forme exacte arcades)
     * hauteur = 4, largeur = 8, piliers internes, porte centrée
     */
    /**
     * ÉTAPE 3 : Ghost House 4×8 - SYMMETRIC DOORS
     * Pour maze 31×28, créer zone 4 rows × 8 cols centrée
     */
    public void addGhostHouse48() {
        System.out.println("👻 Creating Ghost House 4×8 (SYMMETRIC)...");

        int rows = grid.getRows();      // 31
        int cols = grid.getColumns();   // 28

        int midRow = rows / 2;          // 15
        int midCol = cols / 2;          // 14

        // ✅ Zone 4×8 CENTERED
        // 4 rows: 2 above center, 1 below = [13, 14, 15, 16]
        // 8 cols: 4 left, 3 right = [10, 11, 12, 13, 14, 15, 16, 17]
        int minRow = midRow - 2;        // 15-2 = 13
        int maxRow = midRow + 1;        // 15+1 = 16
        int minCol = midCol - 4;        // 14-4 = 10 ✅
        int maxCol = midCol + 3;        // 14+3 = 17 ✅

        System.out.println("   Maze: " + rows + "×" + cols);
        System.out.println("   Centre: [" + midRow + "," + midCol + "]");
        System.out.println("   Ghost House: rows[" + minRow + "-" + maxRow + "], cols[" + minCol + "-" + maxCol + "]");
        System.out.println("   Size: " + (maxRow - minRow + 1) + "×" + (maxCol - minCol + 1));

        // STEP 1: Clear ALL internal walls
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell != null) {
                    cell.links().clear();
                }
            }
        }

        // STEP 2: Create OPEN INTERIOR
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell == null) continue;

                if (r > minRow) {
                    Cell north = grid.getCell(r - 1, c);
                    if (north != null && north.row >= minRow) {
                        cell.link(north);
                    }
                }

                if (c > minCol) {
                    Cell west = grid.getCell(r, c - 1);
                    if (west != null && west.col >= minCol) {
                        cell.link(west);
                    }
                }
            }
        }

        System.out.println("   ✅ Interior: OPEN (32 cells)");

        // STEP 3: CREATE 2 SYMMETRIC DOORS

        // LEFT DOOR at bottom-left
        Cell leftDoor = grid.getCell(maxRow, minCol);
        Cell leftOutside = grid.getCell(maxRow + 1, minCol);

        if (leftDoor != null && leftOutside != null) {
            leftDoor.link(leftOutside);
            System.out.println("   ✅ LEFT door: [" + maxRow + "," + minCol + "] ↔ [" + (maxRow + 1) + "," + minCol + "]");
        }

        // RIGHT DOOR at bottom-right
        Cell rightDoor = grid.getCell(maxRow, maxCol);
        Cell rightOutside = grid.getCell(maxRow + 1, maxCol);

        if (rightDoor != null && rightOutside != null) {
            rightDoor.link(rightOutside);
            System.out.println("   ✅ RIGHT door: [" + maxRow + "," + maxCol + "] ↔ [" + (maxRow + 1) + "," + maxCol + "]");
        }

        System.out.println("👻 Ghost House 4×8 complete!\n");
    }



    private void addGhostHouse() {

        int rows = grid.getRows();
        int cols = grid.getColumns();

        int midRow = rows / 2;
        int midCol = cols / 2;

        // Dimensions officielles Pac-Man (4x8)
        final int GH_HEIGHT = 4;
        final int GH_WIDTH  = 8;

        int rowStart = midRow - (GH_HEIGHT / 2);     // midRow - 2
        int rowEnd   = rowStart + GH_HEIGHT - 1;     // midRow + 1

        int colStart = midCol - (GH_WIDTH / 2);      // midCol - 4
        int colEnd   = colStart + GH_WIDTH - 1;      // midCol + 3

        // ===============================================================
        // 1) ISOLER la Ghost House : garder liens internes, couper externes
        // ===============================================================
        for (int r = rowStart; r <= rowEnd; r++) {
            for (int c = colStart; c <= colEnd; c++) {

                Cell cell = grid.getCell(r, c);

                // Supprimer les liens vers l'extérieur
                for (Cell n : new ArrayList<>(cell.links())) {
                    if (!isInsideGhostHouse(n, rowStart, rowEnd, colStart, colEnd)) {
                        cell.unlink(n, true);
                    }
                }

                // Garder/recréer les liens internes
                Cell north = grid.getCell(r - 1, c);
                Cell south = grid.getCell(r + 1, c);
                Cell east  = grid.getCell(r, c + 1);
                Cell west  = grid.getCell(r, c - 1);

                if (north != null && isInsideGhostHouse(north, rowStart, rowEnd, colStart, colEnd))
                    cell.link(north, true);

                if (south != null && isInsideGhostHouse(south, rowStart, rowEnd, colStart, colEnd))
                    cell.link(south, true);

                if (east != null && isInsideGhostHouse(east, rowStart, rowEnd, colStart, colEnd))
                    cell.link(east, true);

                if (west != null && isInsideGhostHouse(west, rowStart, rowEnd, colStart, colEnd))
                    cell.link(west, true);
            }
        }

        // ===============================================================
        // 2) PILIERS INTERNES — STRICTEMENT SYMÉTRIQUES
        //    Piliers officiels Pac-Man, miroirs calculés proprement
        // ===============================================================

        // Colonnes GAUCHE des piliers
        int[] pillarLeftCols = {
            midCol - 3,   // pilier gauche 1
            midCol - 1    // pilier gauche 2
        };

        // Colonnes DROITE mirroirs
        int[] pillarRightCols = new int[pillarLeftCols.length];
        for (int i = 0; i < pillarLeftCols.length; i++) {
            pillarRightCols[i] = (cols - 1) - pillarLeftCols[i];
        }

        // Lignes des piliers
        int pillarStartRow = rowStart + 1;
        int pillarEndRow   = rowStart + 2;

        // Appliquer coupure N/S sur piliers pour les rendre solides
        for (int r = pillarStartRow; r <= pillarEndRow; r++) {

            // GAUCHE
            for (int pc : pillarLeftCols) {
                Cell P = grid.getCell(r, pc);
                if (P != null) {
                    if (P.north != null) P.unlink(P.north, true);
                    if (P.south != null) P.unlink(P.south, true);
                }
            }

            // DROITE (symétrie)
            for (int pc : pillarRightCols) {
                Cell P = grid.getCell(r, pc);
                if (P != null) {
                    if (P.north != null) P.unlink(P.north, true);
                    if (P.south != null) P.unlink(P.south, true);
                }
            }
        }

        // ===============================================================
        // 3) BAS SPÉCIAL PAC-MAN :  ███      ███
        // ===============================================================

        int bottom = rowEnd;

        // 3 blocs fermés à gauche
        int[] bottomLeft = {
            colStart, colStart + 1, colStart + 2
        };

        // Miroir pour droite
        int[] bottomRight = new int[bottomLeft.length];
        for (int i = 0; i < bottomLeft.length; i++) {
            bottomRight[i] = (cols - 1) - bottomLeft[i];
        }

        // Fermer GAUCHE
        for (int c : bottomLeft) {
            Cell cell = grid.getCell(bottom, c);
            Cell below = grid.getCell(bottom + 1, c);
            if (cell != null && below != null) cell.unlink(below, true);
        }

        // Fermer DROITE
        for (int c : bottomRight) {
            Cell cell = grid.getCell(bottom, c);
            Cell below = grid.getCell(bottom + 1, c);
            if (cell != null && below != null) cell.unlink(below, true);
        }

        // ===============================================================
        // 4) PORTE CENTRALE — OUVERTURE OFFICIELLE
        // ===============================================================

        int doorRow = bottom;
        int doorCol = midCol;

        Cell door = grid.getCell(doorRow, doorCol);
        Cell outside = grid.getCell(doorRow + 1, doorCol);

        // Clear all links for the door
        for (Cell n : new ArrayList<>(door.links())) {
            door.unlink(n, true);
        }

        // Single link = porte
        if (outside != null) {
            door.link(outside, true);
        }

        // ===============================================================
        // 5) METTRE À JOUR LES VOISINS DIRECTIONNELS POUR LE RENDERER
        // ===============================================================
        grid.recomputeNeighbors();

        System.out.println("=== GHOST HOUSE INTERNAL LINKS ===");
        for (int r = rowStart; r <= rowEnd; r++) {
            for (int c = colStart; c <= colEnd; c++) {
                Cell cell = grid.getCell(r, c);
                System.out.print(cell.links().size() + " ");
            }
            System.out.println();
        }

    }

    /** Helper */
    private boolean isInsideGhostHouse(Cell n, int rowStart, int rowEnd, int colStart, int colEnd) {
        return n.row >= rowStart && n.row <= rowEnd && n.col >= colStart && n.col <= colEnd;
    }

    /**
     * ÉTAPE 3 : Créer Ghost House 4×7 au centre
     *
     * Dimensions: 4 rows × 7 cols
     * Pour maze 31×28:
     *   - Centre vertical: row 15
     *   - Centre horizontal: col 14
     *   - Ghost House: rows[13-16], cols[10-16]
     */
    /**
     * ÉTAPE 3 : Ghost House 4×7 VIDE (pas de murs intérieurs)
     */
    private void addGhostHouse1() {
        System.out.println("👻 Creating Ghost House 4×7...");

        int rows = grid.getRows();
        int cols = grid.getColumns();
        int midRow = rows / 2;
        int midCol = cols / 2;

        // Zone 4×7
        int minRow = midRow - 2;    // 13
        int maxRow = midRow + 1;    // 16
        int minCol = midCol - 3;    // 11
        int maxCol = midCol + 3;    // 17

        System.out.println("   Ghost House: rows[" + minRow + "-" + maxRow + "], cols[" + minCol + "-" + maxCol + "]");

        // ✅ STEP 1: Clear ALL links in 4×7 zone
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell != null) {
                    cell.links().clear();
                }
            }
        }

        // ✅ STEP 2: Link ALL internal cells (create open space inside)
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell == null) continue;

                // Link to NORTH (if inside Ghost House)
                if (r > minRow) {
                    Cell north = grid.getCell(r - 1, c);
                    if (north != null) {
                        cell.link(north);
                    }
                }

                // Link to EAST (if inside Ghost House)
                if (c < maxCol) {
                    Cell east = grid.getCell(r, c + 1);
                    if (east != null) {
                        cell.link(east);
                    }
                }
            }
        }

        System.out.println("   ✅ Ghost House interior: OPEN (no internal walls)");

        // ✅ STEP 3: Create door at bottom center
        Cell door = grid.getCell(maxRow, midCol);
        Cell outside = grid.getCell(maxRow + 1, midCol);

        if (door != null && outside != null) {
            door.link(outside);
            System.out.println("   ✅ Door created: [" + maxRow + "," + midCol + "] ↔ [" + (maxRow + 1) + "," + midCol + "]");
        }

        System.out.println("👻 Ghost House 4×7 complete!\n");
    }

    /**
     * ÉTAPE 3 : Ghost House 4×7 - ULTRA SIMPLE
     * Juste créer un espace VIDE sans aucun mur intérieur
     */
    private void addGhostHouse2() {
        System.out.println("👻 Creating Ghost House 4×7...");

        int rows = grid.getRows();
        int cols = grid.getColumns();
        int midRow = rows / 2;      // 15
        int midCol = cols / 2;      // 14

        // Zone 4×7
        int minRow = midRow - 2;    // 13
        int maxRow = midRow + 1;    // 16
        int minCol = midCol - 3;    // 11
        int maxCol = midCol + 3;    // 17

        System.out.println("   Ghost House: rows[" + minRow + "-" + maxRow + "], cols[" + minCol + "-" + maxCol + "]");

        // ✅ ÉTAPE 1: Clear ALL internal walls (create open space)
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell != null) {
                    cell.links().clear();

                    // ✅ Link to NORTH neighbor (if exists and inside GH)
                    if (r > minRow) {
                        Cell north = grid.getCell(r - 1, c);
                        if (north != null) {
                            cell.link(north);
                        }
                    }

                    // ✅ Link to WEST neighbor (if exists and inside GH)
                    if (c > minCol) {
                        Cell west = grid.getCell(r, c - 1);
                        if (west != null) {
                            cell.link(west);
                        }
                    }
                }
            }
        }

        System.out.println("   ✅ Interior: OPEN (all cells connected)");

        // ✅ ÉTAPE 2: Create ONE door at bottom center
        Cell door = grid.getCell(maxRow, midCol);
        Cell outside = grid.getCell(maxRow + 1, midCol);

        if (door != null && outside != null) {
            door.link(outside);
            System.out.println("   ✅ Door: [" + maxRow + "," + midCol + "] ↔ [" + (maxRow + 1) + "," + midCol + "]");
        }

        System.out.println("👻 Done!\n");
    }






    /** Applique une symétrie horizontale gauche ↔ droite sur le labyrinthe existant. */
    public void applyHorizontalSymmetry() {
        int rows = grid.getRows();
        int cols = grid.getColumns();
        int mid  = cols / 2; // on ne touche qu'à la moitié gauche

        System.out.println(rows + " " + cols + " " + mid);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < mid; c++) {
                Cell left  = grid.getCell(r, c);
                Cell right = grid.getCell(r, cols - 1 - c);

                mirrorCell(left, right);
            }
        }
    }

    /**
     * ÉTAPE 3 : Ghost House 4×7 - SYMÉTRIQUE
     * Important: Keep left-right symmetry!
     */
    private void addGhostHouse4() {
        System.out.println("👻 Creating Ghost House 4×7 (SYMMETRIC)...");

        int rows = grid.getRows();
        int cols = grid.getColumns();
        int midRow = rows / 2;      // 15
        int midCol = cols / 2;      // 14

        // Zone 4×7 CENTERED
        int minRow = midRow - 2;    // 13
        int maxRow = midRow + 1;    // 16
        int minCol = midCol - 3;    // 11
        int maxCol = midCol + 3;    // 17

        System.out.println("   Ghost House: rows[" + minRow + "-" + maxRow + "], cols[" + minCol + "-" + maxCol + "]");

        // ✅ STEP 1: Clear ALL internal walls
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell != null) {
                    cell.links().clear();
                }
            }
        }

        // ✅ STEP 2: Create OPEN INTERIOR (all cells linked)
        // Use ONLY N and W links to avoid bidirectional issues
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell == null) continue;

                // Link to NORTH (if inside GH)
                if (r > minRow) {
                    Cell north = grid.getCell(r - 1, c);
                    if (north != null && north.row >= minRow) {
                        cell.link(north);
                    }
                }

                // Link to WEST (if inside GH)
                if (c > minCol) {
                    Cell west = grid.getCell(r, c - 1);
                    if (west != null && west.col >= minCol) {
                        cell.link(west);
                    }
                }
            }
        }

        System.out.println("   ✅ Interior: OPEN (no internal walls)");

        // ✅ STEP 3: Create SYMMETRIC DOORS
        // Door on left side AND right side (symmetric!)

        // Left door: [16, 12] ↔ [17, 12]
        Cell leftDoor = grid.getCell(maxRow, minCol - 1);
        Cell leftOutside = grid.getCell(maxRow + 1, minCol - 1);
        if (leftDoor != null && leftOutside != null) {
            leftDoor.link(leftOutside);
            System.out.println("   ✅ Left door: [" + maxRow + "," + (minCol - 1) + "] ↔ [" + (maxRow + 1) + "," + (minCol - 1) + "]");
        }

        // Center door: [16, 14] ↔ [17, 14]
        Cell centerDoor = grid.getCell(maxRow, midCol);
        Cell centerOutside = grid.getCell(maxRow + 1, midCol);
        if (centerDoor != null && centerOutside != null) {
            centerDoor.link(centerOutside);
            System.out.println("   ✅ Center door: [" + maxRow + "," + midCol + "] ↔ [" + (maxRow + 1) + "," + midCol + "]");
        }

        // Right door: [16, 16] ↔ [17, 16]
        Cell rightDoor = grid.getCell(maxRow, maxCol + 1);
        Cell rightOutside = grid.getCell(maxRow + 1, maxCol + 1);
        if (rightDoor != null && rightOutside != null) {
            rightDoor.link(rightOutside);
            System.out.println("   ✅ Right door: [" + maxRow + "," + (maxCol + 1) + "] ↔ [" + (maxRow + 1) + "," + (maxCol + 1) + "]");
        }

        System.out.println("👻 Done (SYMMETRIC)!\n");
    }

    /**
     * ÉTAPE 3 : Ghost House 4×7 - 2 PORTES SYMÉTRIQUES
     */
    private void addGhostHouse5() {
        System.out.println("👻 Creating Ghost House 4×7 (SYMMETRIC DOORS)...");

        int rows = grid.getRows();      // 31
        int cols = grid.getColumns();   // 28

        int midRow = rows / 2;          // 15
        int midCol = cols / 2;          // 14

        // Zone 4×7
        int minRow = midRow - 2;        // 13
        int maxRow = midRow + 1;        // 16
        int minCol = midCol - 3;        // 11
        int maxCol = midCol + 3;        // 17

        System.out.println("   Ghost House: rows[" + minRow + "-" + maxRow + "], cols[" + minCol + "-" + maxCol + "]");

        // ✅ STEP 1: Clear ALL internal walls
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell != null) {
                    cell.links().clear();
                }
            }
        }

        // ✅ STEP 2: Create OPEN INTERIOR
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell == null) continue;

                if (r > minRow) {
                    Cell north = grid.getCell(r - 1, c);
                    if (north != null && north.row >= minRow) {
                        cell.link(north);
                    }
                }

                if (c > minCol) {
                    Cell west = grid.getCell(r, c - 1);
                    if (west != null && west.col >= minCol) {
                        cell.link(west);
                    }
                }
            }
        }

        System.out.println("   ✅ Interior: OPEN");

        // ✅ STEP 3: CREATE 2 SYMMETRIC DOORS (LEFT & RIGHT)

        // LEFT DOOR: at bottom-left edge
        // Door at [maxRow, minCol] ↔ [maxRow+1, minCol]
        Cell leftDoor = grid.getCell(maxRow, minCol);
        Cell leftOutside = grid.getCell(maxRow + 1, minCol);

        if (leftDoor != null && leftOutside != null) {
            leftDoor.link(leftOutside);
            System.out.println("   ✅ LEFT door: [" + maxRow + "," + minCol + "] ↔ [" + (maxRow + 1) + "," + minCol + "]");
        }

        // RIGHT DOOR: at bottom-right edge (MIRRORED)
        // Door at [maxRow, maxCol] ↔ [maxRow+1, maxCol]
        Cell rightDoor = grid.getCell(maxRow, maxCol);
        Cell rightOutside = grid.getCell(maxRow + 1, maxCol);

        if (rightDoor != null && rightOutside != null) {
            rightDoor.link(rightOutside);
            System.out.println("   ✅ RIGHT door: [" + maxRow + "," + maxCol + "] ↔ [" + (maxRow + 1) + "," + maxCol + "]");
        }

        System.out.println("👻 Ghost House with SYMMETRIC DOORS!\n");



    }


    /**
     * DEBUG: Vérifier que le Ghost House est bien créé
     */
    private void verifyGhostHouse() {
        int midRow = grid.getRows() / 2;
        int midCol = grid.getColumns() / 2;

        System.out.println("\n🔍 VERIFYING GHOST HOUSE:");

        // Vérifier zone 3×3
        for (int r = midRow - 1; r <= midRow + 1; r++) {
            for (int c = midCol - 1; c <= midCol + 1; c++) {
                Cell cell = grid.getCell(r, c);
                if (cell != null) {
                    int linkCount = cell.links().size();
                    System.out.println("   [" + r + "," + c + "] has " + linkCount + " link(s)");

                    // La porte devrait avoir 1 lien, les autres 0
                    if (r == midRow + 1 && c == midCol) {
                        if (linkCount == 1) {
                            System.out.println("   ✅ Door cell correct (1 link)");
                        } else {
                            System.out.println("   ❌ Door cell WRONG (" + linkCount + " links)");
                        }
                    } else {
                        if (linkCount == 0) {
                            System.out.println("   ✅ Wall cell correct (0 links)");
                        } else {
                            System.out.println("   ⚠️ Wall cell has links (" + linkCount + ")");
                        }
                    }
                }
            }
        }
        System.out.println("🔍 Verification complete\n");


    }

    /**
     * Copie les liens de la cellule 'left' vers sa cellule miroir 'right'.
     * On efface d'abord tous les liens existants de 'right', puis on recrée
     * les mêmes connexions, mais reflétées horizontalement.
     */
    private void mirrorCell(Cell left, Cell right) {
        // 1) Supprimer tous les liens de la cellule de droite
        for (Cell linked : new ArrayList<>(right.links())) {
            right.unlink(linked, true);      // enlève le lien des deux côtés
        }

        // 2) Recréer les liens reflétés
        for (Cell linkedLeft : left.links()) {
            int dr = linkedLeft.row - left.row; // différence de ligne  (-1,0,1)
            int dc = linkedLeft.col - left.col; // différence de colonne (-1,0,1)

            // on inverse le déplacement horizontal pour le côté droit
            Cell mirroredNeighbor = grid.getCell(
                right.row + dr,
                right.col - dc
            );

            if (mirroredNeighbor != null) {
                right.link(mirroredNeighbor, true);
            }
        }
    }


//
//    /**
//     * ÉTAPE 1 : Symétrie VERTICALE - VERSION CORRECTE
//     */
//    private void makeVerticallySymmetric() {
//        int rows = grid.getRows();
//        int cols = grid.getColumns();
//
//        // SAUVEGARDER tous les liens de la moitié gauche D'ABORD
//        Map<String, Set<String>> savedLinks = new HashMap<>();
//
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < cols / 2; c++) {
//                Cell cell = grid.getCell(r, c);
//                if (cell == null) continue;
//
//                Set<String> links = new HashSet<>();
//                if (cell.north != null && cell.isLinked(cell.north)) links.add("north");
//                if (cell.south != null && cell.isLinked(cell.south)) links.add("south");
//                if (cell.east != null && cell.isLinked(cell.east)) links.add("east");
//                if (cell.west != null && cell.isLinked(cell.west)) links.add("west");
//
//                savedLinks.put(r + "," + c, links);
//            }
//        }
//
//        // NETTOYER TOUS les liens
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < cols; c++) {
//                Cell cell = grid.getCell(r, c);
//                if (cell != null) cell.links().clear();
//            }
//        }
//
//        // RECRÉER avec symétrie
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < cols / 2; c++) {
//                Cell leftCell = grid.getCell(r, c);
//                Cell rightCell = grid.getCell(r, cols - 1 - c);
//
//                if (leftCell == null || rightCell == null) continue;
//
//                Set<String> links = savedLinks.get(r + "," + c);
//                if (links == null) continue;
//
//                // Appliquer les liens sauvegardés
//                if (links.contains("north")) {
//                    if (leftCell.north != null) leftCell.link(leftCell.north);
//                    if (rightCell.north != null) rightCell.link(rightCell.north);
//                }
//                if (links.contains("south")) {
//                    if (leftCell.south != null) leftCell.link(leftCell.south);
//                    if (rightCell.south != null) rightCell.link(rightCell.south);
//                }
//                if (links.contains("east")) {
//                    if (leftCell.east != null) leftCell.link(leftCell.east);
//                    if (rightCell.west != null) rightCell.link(rightCell.west); // MIROIR
//                }
//                if (links.contains("west")) {
//                    if (leftCell.west != null) leftCell.link(leftCell.west);
//                    if (rightCell.east != null) rightCell.link(rightCell.east); // MIROIR
//                }
//            }
//        }
//    }
//
    /**
     * ÉTAPE 2 : Braiding
     */
    void braidMaze(double probability) {
        List<Cell> deadEnds = new ArrayList<>(grid.deadEnds());
        Collections.shuffle(deadEnds, random);

        for (Cell cell : deadEnds) {
            if (cell.links().size() != 1) continue;
            if (random.nextDouble() > probability) continue;

            List<Cell> neighbors = new ArrayList<>();
            for (Cell n : cell.neighbors()) {
                if (!cell.isLinked(n)) neighbors.add(n);
            }

            if (neighbors.isEmpty()) continue;

            List<Cell> best = new ArrayList<>();
            for (Cell n : neighbors) {
                if (n.links().size() == 1) best.add(n);
            }

            Cell neighbor = best.isEmpty() ?
                neighbors.get(random.nextInt(neighbors.size())) :
                best.get(random.nextInt(best.size()));

            cell.link(neighbor);
        }
    }
//
//    /**
//     * ÉTAPE 3 : Ghost House
//     */
//    private void addGhostHouse() {
//        int midRow = grid.getRows() / 2;
//        int midCol = grid.getColumns() / 2;
//
//        for (int r = midRow - 1; r <= midRow + 1; r++) {
//            for (int c = midCol - 1; c <= midCol + 1; c++) {
//                Cell cell = grid.getCell(r, c);
//                if (cell != null) cell.links().clear();
//            }
//        }
//
//        Cell door = grid.getCell(midRow + 1, midCol);
//        Cell outside = grid.getCell(midRow + 2, midCol);
//        if (door != null && outside != null) door.link(outside);
//    }
//
//    /**
//     * ÉTAPE 4 : Tunnels
//     */
//    private void addTunnels() {
//        int midRow = grid.getRows() / 2;
//        Cell left = grid.getCell(midRow, 0);
//        Cell right = grid.getCell(midRow, grid.getColumns() - 1);
//        if (left != null && right != null) left.link(right);
//    }

    public Grid result() {
        return grid;
    }
}
