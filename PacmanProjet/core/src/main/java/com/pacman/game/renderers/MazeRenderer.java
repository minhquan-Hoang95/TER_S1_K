package com.pacman.game.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pacman.game.model.Maze;
import com.pacman.game.model.Cell;

/**
 * Renderer pour afficher le maze graphiquement sur l'écran
 *
 * Responsabilités:
 * - Convertir structure logique (Cell[][] avec murs) en dessin 2D
 * - Utiliser ShapeRenderer de LibGDX pour tracer les lignes
 * - Gérer la couleur, épaisseur, position des murs
 *
 * Concept:
 * - Chaque cellule est un carré de CELL_SIZE x CELL_SIZE pixels
 * - Si une direction n'a pas de voisin linkké = il y a un mur = on dessine
 * - Sinon il y a un passage = rien à dessiner
 *
 * Exemple:
 *   Cell(0,0) - Cell(0,1)
 *   Si Cell(0,0).east != null ET isLinked: PAS de mur vertical entre eux
 *   Si Cell(0,0).east == null: MUR vertical = on dessine une ligne
 */
public class MazeRenderer {

    // ===== CONSTANTES (CONFIGURATION) =====

    /**
     * Taille d'une cellule en pixels
     * Un carré de 32x32 pixels
     *
     * Pour un maze 10x10:
     * - Largeur totale = 10 * 32 = 320 pixels
     * - Hauteur totale = 10 * 32 = 320 pixels
     *
     * Vous pouvez changer cette valeur pour zoom in/out:
     * - Augmenter pour plus grand
     * - Diminuer pour plus petit
     */
    private static final int CELL_SIZE = 32;

    /**
     * Couleur des murs
     * Color.BLUE = ligne bleue
     *
     * Options: Color.RED, Color.GREEN, Color.WHITE, etc.
     * Ou créer custom: new Color(r, g, b, a)
     */
    private static final Color WALL_COLOR = Color.BLUE;

    /**
     * Épaisseur des lignes de mur (en pixels)
     * 1 = très fin
     * 3 = normal
     * 5+ = très épais
     *
     * IMPORTANT: en pixels! Pas relatif à CELL_SIZE
     */
    private static final float WALL_THICKNESS = 2.0f;

    // ===== COMPOSANTS GPU =====

    /**
     * ShapeRenderer de LibGDX
     * Outil pour tracer des formes géométriques (lignes, rectangles, cercles)
     *
     * Utilisation:
     * 1. renderer.begin(ShapeType.Line)  <- Mode traçage de lignes
     * 2. renderer.setColor(color)         <- Couleur pour les lignes suivantes
     * 3. renderer.line(x1,y1, x2,y2)     <- Tracer une ligne
     * 4. renderer.end()                   <- Finir le traçage
     *
     * IMPORTANT: il faut appeler end() avant begin(autre type)
     */
    private ShapeRenderer shapeRenderer;

    /**
     * Constructeur
     * Appelé une seule fois: GameScreen.__init__()
     */
    public MazeRenderer() {
        // Créer l'objet ShapeRenderer (allouer mémoire GPU)
        this.shapeRenderer = new ShapeRenderer();
    }

    /**
     * Dessiner le maze entier à l'écran
     *
     * Algorithme:
     * 1. Pour chaque cellule du maze
     * 2. Vérifier chaque direction (nord, est, sud, ouest)
     * 3. Si pas de voisin linkké dans cette direction = il y a un mur
     * 4. Si mur: dessiner une ligne aux coordonnées correctes
     *
     * @param maze le maze à dessiner (contient Cell[][] et dimensions)
     */
    public void render(Maze maze) {
        // ===== ÉTAPE 1: PRÉPARER LE TRAÇAGE =====

        // Dire à ShapeRenderer qu'on va tracer des LIGNES
        // (pas des rectangles, cercles, polygones, etc.)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Définir la couleur pour toutes les lignes suivantes
        shapeRenderer.setColor(WALL_COLOR);  // Bleu dans notre cas

        // ✅ FIX: Définir épaisseur AVANT tracer les lignes
        // ===== ÉTAPE 2: PARCOURIR CHAQUE CELLULE =====

        // Boucle sur lignes
        for (int r = 0; r < maze.rows; r++) {
            // Boucle sur colonnes
            for (int c = 0; c < maze.cols; c++) {
                // Récupérer la cellule à cette position
                Cell cell = maze.cells[r][c];

                // ===== CALCULER POSITION PIXEL =====

                /**
                 * Coordonnées en pixels de cette cellule
                 * (0,0) = coin haut-gauche de l'écran
                 *
                 * Exemple: Cell(1, 2) avec CELL_SIZE=32
                 * x = 2 * 32 = 64 pixels depuis la gauche
                 * y = 1 * 32 = 32 pixels depuis le haut
                 *
                 * Schéma:
                 *   +---+---+---+
                 * 0 | A | B | C |
                 *   +---+---+---+
                 * 1 | D | E | F |  <- Cell E = (1, 2)
                 *   +---+---+---+       x = 2*32 = 64
                 *   0   1   2           y = 1*32 = 32
                 */
                float x = c * CELL_SIZE;
                float y = r * CELL_SIZE;

                // ===== ÉTAPE 3: DESSINER LES MURS =====

                /**
                 * LOGIQUE CLÉE:
                 * - Si cell.north == null: pas de cellule au nord
                 *   => c'est le bord du maze => on dessine un mur
                 * - Si cell.north != null ET isLinked: il y a un passage
                 *   => PAS de mur (espace libre)
                 * - Si cell.north != null ET NOT isLinked: impossible,
                 *   car le JSON crée les liens automatiquement
                 *
                 * RÉFÉRENCE: En LibGDX, coordonnées:
                 * - x augmente vers la DROITE
                 * - y augmente vers le HAUT (contrairement à certains frameworks)
                 *
                 * Position cellule:
                 *     (x,y+CS)  --- (x+CS,y+CS)
                 *         |                |
                 *         |  CELLULE      |
                 *         |                |
                 *     (x,y)   -----  (x+CS,y)
                 *
                 * où CS = CELL_SIZE
                 */

                // ===== MUR NORD (en haut) =====
                /**
                 * Mur du côté nord (haut) de la cellule
                 * Ligne HORIZONTALE du coin gauche-haut au coin droit-haut
                 *
                 * Coordonnées: (x, y+CS) à (x+CS, y+CS)
                 *
                 * Condition: si pas de cellule au nord = MUR
                 */
//                If there is a neighbor north AND NOT linked → wall
                if (cell.north == null || !cell.isLinked(cell.north)) {                // Tracer ligne horizontale en haut de la cellule
                    // De (x, y+CELL_SIZE) à (x+CELL_SIZE, y+CELL_SIZE)
                    shapeRenderer.line(
                        x,
                        y + CELL_SIZE,
                        x + CELL_SIZE,
                        y + CELL_SIZE
                    );
                }

                // If there is NO neighbor north → only draw wall if this is a true border


                // ===== MUR EST (à droite) =====
                /**
                 * Mur du côté est (droit) de la cellule
                 * Ligne VERTICALE du coin droit-haut au coin droit-bas
                 *
                 * Coordonnées: (x+CS, y+CS) à (x+CS, y)
                 *
                 * Condition: si pas de cellule à l'est = MUR
                 */
                if (cell.east == null || !cell.isLinked(cell.east)) {  // Pas de voisin est = bord = MUR
                    // Tracer ligne verticale à droite de la cellule
                    // De (x+CELL_SIZE, y) à (x+CELL_SIZE, y+CELL_SIZE)
                    shapeRenderer.line(
                        x + CELL_SIZE,
                        y,
                        x + CELL_SIZE,
                        y + CELL_SIZE
                    );
                }

                // ===== MUR SUD (en bas) =====
                /**
                 * Mur du côté sud (bas) de la cellule
                 * Ligne HORIZONTALE du coin gauche-bas au coin droit-bas
                 *
                 * Coordonnées: (x, y) à (x+CS, y)
                 *
                 * Condition: si pas de cellule au sud = MUR
                 */
                if (cell.south == null || !cell.isLinked(cell.south)) {  // Pas de voisin sud = bord = MUR
                    // Tracer ligne horizontale en bas de la cellule
                    // De (x, y) à (x+CELL_SIZE, y)
                    shapeRenderer.line(
                        x,
                        y,
                        x + CELL_SIZE,
                        y
                    );
                }

                // ===== MUR OUEST (à gauche) =====
                /**
                 * Mur du côté ouest (gauche) de la cellule
                 * Ligne VERTICALE du coin gauche-haut au coin gauche-bas
                 *
                 * Coordonnées: (x, y+CS) à (x, y)
                 *
                 * Condition: si pas de cellule à l'ouest = MUR
                 */
                if (cell.west == null || !cell.isLinked(cell.west)) {                    // Tracer ligne verticale à gauche de la cellule
                    // De (x, y) à (x, y+CELL_SIZE)
                    shapeRenderer.line(
                        x,
                        y,
                        x,
                        y + CELL_SIZE
                    );
                }
                // ----------------------------------------------------------
                // SPECIAL CASE: right border of the maze
                // ----------------------------------------------------------
//                if (c == maze.cols - 1) {
//                    shapeRenderer.line(
//                        x + CELL_SIZE,
//                        y,
//                        x + CELL_SIZE,
//                        y + CELL_SIZE
//                    );
//                }
//
//                // ----------------------------------------------------------
//                // SPECIAL CASE: bottom border of the maze
//                // ----------------------------------------------------------
//                if (r == maze.rows - 1) {
//                    shapeRenderer.line(
//                        x,
//                        y,
//                        x + CELL_SIZE,
//                        y
//                    );
//                }
            }
        }

        // ===== ÉTAPE 4: TERMINER LE TRAÇAGE =====

        /**
         * Dire à ShapeRenderer qu'on a fini
         * IMPORTANT: il faut end() avant d'appeler begin(autre type)
         *
         * Ce que end() fait:
         * - Envoyer les données au GPU
         * - Appliquer la matrice de caméra
         * - Afficher le résultat à l'écran
         */
        shapeRenderer.end();
    }

    /**
     * Libérer les ressources GPU
     *
     * IMPORTANT: doit être appelé dans dispose() de l'écran
     * Sinon = fuite mémoire GPU!
     *
     * Ce que dispose() fait:
     * - Libérer mémoire GPU allouée pour ShapeRenderer
     * - Fermer les buffers
     * - Préparer la réutilisation d'autres renderers
     */
    public void dispose() {
        // Libérer la mémoire GPU du ShapeRenderer
        shapeRenderer.dispose();
    }

    // ===== MÉTHODES BONUS (Optionnel - pour future utilisation) =====

    /**
     * Optionnel: Changer la taille des cellules à la volée
     * Permet du zoom in/out
     *
     * NOTE: Cette version de MazeRenderer n'utilise pas cette méthode
     * On pourrait la rendre configurable si on veut zoom
     */
    public void setCellSize(int newSize) {
        // Modifier CELL_SIZE... mais c'est une constante!
        // Il faudrait le rendre non-constant pour vraiment utiliser ça
        // Exemple pour la future:
        // this.cellSize = newSize;
    }

    /**
     * Optionnel: Changer la couleur des murs
     *
     * Usage: renderer.setWallColor(Color.RED);
     *
     * NOTE: Cette version n'a pas cette méthode
     * On pourrait l'ajouter facilement:
     * this.wallColor = color;
     */
    public void setWallColor(Color color) {
        // this.wallColor = color;
    }
}
