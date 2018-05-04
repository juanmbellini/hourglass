package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

/**
 * Represents a wall in the system.
 */
public final class Wall implements Collisionable, StateHolder<Wall.WallState> {

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

    //TODO: Don't know if we are going to need this.
    @Override
    public void collide(final Particle particle) {
        final Vector2D velocity = particle.getVelocity();
        switch (wallLayout) {
        case HORIZONTAL:
            particle.setVelocity(velocity.getX(), -velocity.getY());
            break;
        case VERTICAL:
            particle.setVelocity(-velocity.getX(), velocity.getY());
            break;
        case OTHER:
        default:
            // TODO: any other?
        }
    }

    @Override
    public WallState outputState() {
        return new WallState(this);
    }

    /**
     * Creates an horizontal wall (i.e having both initial and final point with the same 'y' component).
     *
     * @param xInitialPosition The 'x' component of the initial point position.
     * @param yInitialPosition The 'y' component of the initial point position.
     * @param length           The length of the wall (must be positive).
     * @return The created wall.
     * @throws IllegalArgumentException In case the length is not positive.
     */
    public static Wall getHorizontal(final double xInitialPosition, final double yInitialPosition, final double length)
        throws IllegalArgumentException {
        validateLength(length);
        final Vector2D initialPosition = new Vector2D(xInitialPosition, yInitialPosition);
        final Vector2D finalPosition = new Vector2D(xInitialPosition + length, yInitialPosition);
        return new Wall(initialPosition, finalPosition, WallLayout.HORIZONTAL);
    }

    /**
     * Creates a vertical wall (i.e having both initial and final point with the same 'x' component).
     *
     * @param xInitialPosition The 'x' component of the initial point position.
     * @param yInitialPosition The 'y' component of the initial point position.
     * @param length           The length of the wall (must be positive).
     * @return The created wall.
     * @throws IllegalArgumentException In case the length is not positive.
     */
    public static Wall getVertical(final double xInitialPosition, final double yInitialPosition, final double length)
        throws IllegalArgumentException {
        validateLength(length);
        final Vector2D initialPosition = new Vector2D(xInitialPosition, yInitialPosition);
        final Vector2D finalPosition = new Vector2D(xInitialPosition, yInitialPosition + length);
        return new Wall(initialPosition, finalPosition, WallLayout.VERTICAL);
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
        /* default */ WallState(final Wall wall) {
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