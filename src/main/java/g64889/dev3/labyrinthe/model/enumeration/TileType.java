package g64889.dev3.labyrinthe.model.enumeration;

/**
 * Represents the different types of tiles in the Labyrinth game.
 * <p>
 * Each tile type defines a specific shape and number of openings:
 * <ul>
 *   <li>I : straight tile with two opposite openings</li>
 *   <li>L : corner tile with two adjacent openings</li>
 *   <li>T : T-shaped tile with three openings</li>
 * </ul>
 */
public enum TileType {

    /** Straight tile (two opposite openings). */
    I,

    /** Corner tile (two adjacent openings). */
    L,

    /** T-shaped tile (three openings). */
    T
}