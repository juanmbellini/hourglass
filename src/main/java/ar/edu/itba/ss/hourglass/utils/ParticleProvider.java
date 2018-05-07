package ar.edu.itba.ss.hourglass.utils;

import ar.edu.itba.ss.hourglass.models.Particle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Provides {@link Particle}s to the system.
 */
public class ParticleProvider {


    private static final double MASS = 0.01;
    private static final double MIN_DIAMETER = 0.02;
    private static final double MAX_DIAMETER = 0.03;

    /**
     * {@link Set} containing the already created {@link Particle}s.
     */
    public final Set<Particle> particles;

    public ParticleProvider() {
        particles = new HashSet<>();
    }

    //TODO: depends on how we model the system we could define the range of positions that we want
    // and use random positions in this method too.

    /**
     * Adds another {@link Particle} in the system, if possible.
     *
     * @param position The new Particle's position
     * @return True if another Particle fits in the system, false if not.
     */
    public boolean addParticle(final Vector2D position) {
        final double radius = MIN_DIAMETER + new Random().nextDouble() * (MAX_DIAMETER - MIN_DIAMETER);

        if (particles.stream().anyMatch(p -> p.doOverlap(position, radius))) {
            return false;
        } else {
            particles.add(new Particle(MASS, radius, position, Vector2D.ZERO, Vector2D.ZERO));
            return true;
        }
    }

    /**
     * Returns the amount of {@link Particle}s in the system.
     */
    public int getAmountOfParticles() {
        return particles.size();
    }
}

