package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

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
     * Creates an horizontal wall (i.e having both initial and final point with the same 'y' component).
     *
     * @param initialPosition The initial position of the wall.
     * @param length          The length of the wall (must be positive).
     * @return The created wall.
     * @throws IllegalArgumentException In case the length is not positive.
     * @apiNote The created wall will have the final point with the same 'y' component
     * of the given {@code initialPoint}, and the 'x' component being {@code initialPoint.getX() + length},
     * which means that it grows with 'x'.
     */
    public static Wall getHorizontal(final Vector2D initialPosition, final double length)
            throws IllegalArgumentException {
        validateInitialPoint(initialPosition);
        validateLength(length);
        final Vector2D finalPosition = new Vector2D(initialPosition.getX() + length, initialPosition.getY());
        return new Wall(initialPosition, finalPosition, WallLayout.HORIZONTAL);
    }

    /**
     * Creates a vertical wall (i.e having both initial and final point with the same 'x' component).
     *
     * @param initialPosition The initial position of the wall.
     * @param length          The length of the wall (must be positive).
     * @return The created wall.
     * @throws IllegalArgumentException In case the length is not positive.
     * @apiNote The created wall will have the final point with the same 'x' component
     * of the given {@code initialPoint}, and the 'y' component being {@code initialPoint.getY() + length},
     * which means that it grows with 'y'.
     */
    public static Wall getVertical(final Vector2D initialPosition, final double length)
            throws IllegalArgumentException {
        validateInitialPoint(initialPosition);
        validateLength(length);
        final Vector2D finalPosition = new Vector2D(initialPosition.getX(), initialPosition.getY() + length);
        return new Wall(initialPosition, finalPosition, WallLayout.VERTICAL);
    }

    /**
     * Validates the given {@code initialPoint}.
     *
     * @param initialPoint The initial point to be validated.
     * @throws IllegalArgumentException In case the initial point is not valid (i.e is {@code null}).
     */
    private static void validateInitialPoint(final Vector2D initialPoint) throws IllegalArgumentException {
        Assert.notNull(initialPoint, "The initial point must not be null");
    }

    /**
     * Validates the given {@code length}.
     *
     * @param length The length value to be validated.
     * @throws IllegalArgumentException In case the value is not valid (i.e is not positive).
     */
    private static void validateLength(final double length) throws IllegalArgumentException {
        Assert.isTrue(length > 0, "The length of the wall must be positive");
    }

    /**
     * Enum containing the wall layouts (i.e horizontal and vertical).
     */
    private enum WallLayout {
        /**
         * Indicates that the wall is horizontal (i.e having both initial and final point with the same 'y' component).
         */
        HORIZONTAL,
        /**
         * Indicates that the wall is vertical (i.e having both initial and final point with the same 'x' component).
         */
        VERTICAL,
        /**
         * Non vertical and non horizontal.
         */
        OTHER
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
