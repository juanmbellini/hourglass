package ar.edu.itba.ss.hourglass.utils;

import ar.edu.itba.ss.hourglass.models.Particle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Provides {@link Particle}s to the system.
 */
public class ParticleProvider {

    /**
     * The max. amount of consecutive failed tries of adding a {@link Particle}
     * into the returned {@link List} of {@link Particle} by the {@link #createParticles()} method.
     */
    private static final int MAX_AMOUNT_OF_TRIES = 3000;

    /**
     * The min. radius of a {@link Particle}.
     */
    private final double minRadius;
    /**
     * The max. radius of a {@link Particle}.
     */
    private final double maxRadius;
    /**
     * The {@link Particle}s' mass.
     */
    private final double mass;
    /**
     * The min. value for a position ('x' component).
     */
    private final double xMin;
    /**
     * The max. value for a position ('x' component).
     */
    private final double xMax;
    /**
     * The min. value for a position ('y' component).
     */
    private final double yMin;
    /**
     * The max. value for a position ('y' component).
     */
    private final double yMax;


    /**
     * Constructor.
     *
     * @param minRadius The min. radius of a {@link Particle}.
     * @param maxRadius The max. radius of a {@link Particle}.
     * @param mass      The mass of a {@link Particle}.
     * @param xMin      The min. value for a position ('x' component).
     * @param xMax      The max. value for a position ('x' component).
     * @param yMin      The min. value for a position ('y' component).
     * @param yMax      The max. value for a position ('y' component).
     */
    public ParticleProvider(final double minRadius, final double maxRadius, final double mass,
                            final double xMin, final double xMax, final double yMin, final double yMax) {
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.mass = mass;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax * 0.3;
    }

    /**
     * Creates the needed {@link Particle}s.
     *
     * @return A {@link List} with the created {@link Particle}s.
     */
    public List<Particle> createParticles() {
        final List<Particle> particles = new LinkedList<>();
        int tries = 0; // Will count the amount of consecutive failed tries of adding randomly a particle into the list.
        while (tries < MAX_AMOUNT_OF_TRIES) {
            final double radius = minRadius + new Random().nextDouble() * (maxRadius - minRadius);
            final double xPosition = (xMin + radius) + new Random().nextDouble() * ((xMax - radius) - (xMin + radius));
            final double yPosition = (yMin + radius) + new Random().nextDouble() * ((yMax - radius) - (yMin + radius));
            final Vector2D position = new Vector2D(xPosition, yPosition);
            if (particles.stream().noneMatch(p -> p.doOverlap(position, radius))) {
                particles.add(new Particle(this.mass, radius, position, Vector2D.ZERO, Constants.GRAVITY));
                tries = 0; // When a particle is added, the counter of consecutive failed tries must be set to zero.
            } else {
                tries++;
            }
        }
        return particles;
    }
}
