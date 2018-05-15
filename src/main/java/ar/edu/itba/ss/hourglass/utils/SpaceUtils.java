package ar.edu.itba.ss.hourglass.utils;

import ar.edu.itba.ss.hourglass.models.Wall;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Class implementing several methods that help the calculation of space properties.
 */
public class SpaceUtils {

    /**
     * Returns the projection of the given {@code position} in the given {@code wall}.
     *
     * @param wall The {@link Wall} in which the projection is made.
     * @return The projection {@link Vector2D}.
     */
    public static Vector2D projectionInWall(final Vector2D position, final Wall wall) {
        final Vector2D directionVector = wall.getDirectionVector();
        final Vector2D fromInitial = position.subtract(wall.getInitialPoint());

        return directionVector
                .scalarMultiply(fromInitial.dotProduct(directionVector))
                .scalarMultiply(1 / directionVector.getNormSq());
    }

    /**
     * Calculates how much to particles with the given
     * {@code position1}, {@code position2}, {@code radius1}, and {@code radius2} would overlap.
     *
     * @param position1 The first particle's position.
     * @param position2 The second particle's position.
     * @param radius1   The first particle's radius.
     * @param radius2   The second particle's radius.
     * @return The overlapping amount.
     * @apiNote A value of zero indicates that there is no overlapping.
     */
    public static double overlap(Vector2D position1, Vector2D position2, double radius1, double radius2) {
        final double value = radius1 + radius2 - position1.distance(position2);
        return value < 0 ? 0 : value;
    }
}
