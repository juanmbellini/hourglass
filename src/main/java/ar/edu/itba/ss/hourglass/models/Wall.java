package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Represents a wall in the system.
 */
public final class Wall implements StateHolder<Wall.WallState> {

    /**
     * The wall's initial position.
     */
    private final Vector2D initialPoint;

    /**
     * The wall's final position.
     */
    private final Vector2D finalPoint;

    /**
     * The wall's layout.
     */
    private final WallLayout wallLayout;

    /**
     * Constructor.
     *
     * @param initialPoint The wall's initial position.
     * @param finalPoint   The wall's final position.
     */
    private Wall(final Vector2D initialPoint, final Vector2D finalPoint, final WallLayout wallLayout) {
        this.initialPoint = initialPoint;
        this.finalPoint = finalPoint;
        this.wallLayout = wallLayout;
    }

    /**
     * @return The wall's initial position.
     */
    public Vector2D getInitialPoint() {
        return initialPoint;
    }

    /**
     * @return The wall's final position.
     */
    public Vector2D getFinalPoint() {
        return finalPoint;
    }


    /**
     * @return The direction vector of thw wall (goes from the initial point to the final point).
     * @see <a href="https://es.wikipedia.org/wiki/Vector_director">Vector Director</a>
     * @see <a href="https://en.wikipedia.org/wiki/Euclidean_vector">Euclidean Vector</a>
     */
    public Vector2D getDirectionVector() {
        return finalPoint.subtract(initialPoint);
    }


    @Override
    public WallState outputState() {
        return new WallState(this);
    }

    /**
     * Creates a top wall for a {@link Silo}.
     *
     * @param length The length of the {@link Silo} (must be positive).
     * @param width  The width of the {@link Silo} (must be positive).
     * @return The created wall.
     * @throws IllegalArgumentException In case the length or width are not positive.
     */
    public static Wall getTopWall(final double length, final double width) throws IllegalArgumentException {
        final Vector2D initialPosition = new Vector2D(0, length);
        final Vector2D finalPosition = new Vector2D(width, length);
        return new Wall(initialPosition, finalPosition, WallLayout.TOP);
    }

    /**
     * Creates a left bottom wall for a {@link Silo}.
     *
     * @param width The width of the {@link Silo} (must be positive).
     * @param hole  The hole length.
     * @return The created wall.
     * @throws IllegalArgumentException In case the length or width are not positive.
     */
    public static Wall getLeftBottomWall(final double width, final double hole) throws IllegalArgumentException {
        final Vector2D initialPosition = new Vector2D(0, 0);
        final Vector2D finalPosition = new Vector2D((width - hole) / 2, 0);
        return new Wall(initialPosition, finalPosition, WallLayout.BOTTOM_LEFT);
    }

    /**
     * Creates a left bottom wall for a {@link Silo}.
     *
     * @param width The width of the {@link Silo} (must be positive).
     * @param hole  The hole length.
     * @return The created wall.
     * @throws IllegalArgumentException In case the length or width are not positive.
     */
    public static Wall getRightBottomWall(final double width, final double hole) throws IllegalArgumentException {
        final Vector2D initialPosition = new Vector2D((width + hole) / 2, 0);
        final Vector2D finalPosition = new Vector2D(width, 0);
        return new Wall(initialPosition, finalPosition, WallLayout.BOTTOM_RIGHT);
    }

    /**
     * Creates a left bottom wall for a {@link Silo}.
     *
     * @param length The length of the {@link Silo} (must be positive).
     * @return The created wall.
     * @throws IllegalArgumentException In case the length or width are not positive.
     */
    public static Wall getLeftWall(final double length) throws IllegalArgumentException {
        final Vector2D initialPosition = new Vector2D(0, 0);
        final Vector2D finalPosition = new Vector2D(0, length);
        return new Wall(initialPosition, finalPosition, WallLayout.LEFT);
    }

    /**
     * Creates a left bottom wall for a {@link Silo}.
     *
     * @param length The length of the {@link Silo} (must be positive).
     * @param width  The width of the {@link Silo} (must be positive).
     * @return The created wall.
     * @throws IllegalArgumentException In case the length or width are not positive.
     */
    public static Wall getRightWall(final double length, final double width) throws IllegalArgumentException {
        final Vector2D initialPosition = new Vector2D(width, 0);
        final Vector2D finalPosition = new Vector2D(width, length);
        return new Wall(initialPosition, finalPosition, WallLayout.RIGHT);
    }

    /**
     * Enum containing the wall layouts (i.e horizontal and vertical).
     */
    private enum WallLayout {
        /**
         * Indicates that this is a top wall.
         */
        TOP,
        /**
         * Indicates that this is a bottom left wall.
         */
        BOTTOM_LEFT,
        /**
         * Indicates that this is a bottom right wall.
         */
        BOTTOM_RIGHT,
        /**
         * Indicates that this is a right wall.
         */
        RIGHT,
        /**
         * Indicates that this is a left wall.
         */
        LEFT
    }

    /**
     * Represents the state of a {@link Wall}.
     */
    public static final class WallState implements State {
        /**
         * The wall's initial state.
         */
        private final Vector2D initialPoint;
        /**
         * The wall's final state.
         */
        private final Vector2D finalPoint;

        /**
         * Constructor.
         *
         * @param wall The {@link Wall} owning this state.
         */
        /* package */ WallState(final Wall wall) {
            initialPoint = wall.initialPoint;
            finalPoint = wall.finalPoint;
        }

        /**
         * @return The wall's initial state.
         */
        public Vector2D getInitialPoint() {
            return initialPoint;
        }

        /**
         * @return The wall's final state.
         */
        public Vector2D getFinalPoint() {
            return finalPoint;
        }
    }
}
