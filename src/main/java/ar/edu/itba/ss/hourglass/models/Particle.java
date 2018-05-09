package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;


/**
 * Represents a particle in the system.
 */
public class Particle implements StateHolder<Particle.ParticleState> {

    /**
     * Half.
     */
    private static final double HALF = 0.5;

    /**
     * The particle's mass.
     */
    private final double mass;

    /**
     * The particle's radius.
     */
    private final double radius;

    /**
     * The particle's position (represented as a 2D vector).
     */
    private Vector2D position;

    /**
     * The particle's velocity (represented as a 2D vector).
     */
    private Vector2D velocity;

    /**
     * The particle's acceleration (represented as a 2D vector).
     */
    private Vector2D acceleration;


    /**
     * Constructor.
     *
     * @param mass         The particle's mass.
     * @param radius       The particle's radius.
     * @param position     The particle's position (represented as a 2D vector).
     * @param velocity     The particle's velocity (represented as a 2D vector).
     * @param acceleration The particle's acceleration (represented as a 2D vector).
     */
    public Particle(final double mass, final double radius,
                    final Vector2D position, final Vector2D velocity, final Vector2D acceleration) {
        validateMass(mass);
        validateRadius(radius);
        validateVector(position);
        validateVector(velocity);
        validateVector(acceleration);

        this.mass = mass;
        this.radius = radius;
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    // ================================================================================================================
    // Getters
    // ================================================================================================================

    /**
     * @return The particle's mass.
     */
    public double getMass() {
        return mass;
    }

    /**
     * @return The particle's radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @return The particle's position (represented as a 2D vector).
     */
    public Vector2D getPosition() {
        return position;
    }

    /**
     * @return The particle's velocity (represented as a 2D vector).
     */
    public Vector2D getVelocity() {
        return velocity;
    }

    /**
     * @return The particle's acceleration (represented as a 2D vector).
     */
    public Vector2D getAcceleration() {
        return acceleration;
    }

    /**
     * @return The particle's kinetic energy.
     */
    public double getKineticEnergy() {
        return HALF * mass * velocity.getNormSq();
    }


    // ================================================================================================================
    // Setters
    // ================================================================================================================

    // TODO: check if state will be modified from outside or if the particle receives a System and, based on it, modifies itself

    /**
     * Sets a new position for this particle.
     *
     * @param position The new position for this particle.
     */
    public void setPosition(final Vector2D position) {
        validateVector(position);
        this.position = position;
    }

    /**
     * Sets a new velocity for this particle.
     *
     * @param velocity The new velocity for this particle.
     */
    public void setVelocity(final Vector2D velocity) {
        validateVector(velocity);
        this.velocity = velocity;
    }

    /**
     * Sets a new acceleration for this particle.
     *
     * @param acceleration The new acceleration for this particle.
     */
    public void setAcceleration(final Vector2D acceleration) {
        validateVector(acceleration);
        this.acceleration = acceleration;
    }


    /**
     * Checks if another particle can be created with the given {@code position} and {@code radius} arguments.
     *
     * @param position The position where the new particle will be created.
     * @param radius   The radius of the new particle.
     * @return {@code true} if the new particle would overlap {@code this} particle, or {@code false} otherwise.
     */
    public boolean doOverlap(final Vector2D position, final double radius) {
        final double distanceBetweenCentreMasses = this.position.distance(position);
        return Double.compare(distanceBetweenCentreMasses, this.radius + radius) < 0;
    }

    /**
     * Validates the given {@code radius} value.
     *
     * @param radius The radius value to be validated.
     * @throws IllegalArgumentException In case the given {@code radius} value is not value (i.e is not positive).
     */
    private static void validateRadius(final double radius) throws IllegalArgumentException {
        Assert.isTrue(radius > 0, "The radius must be positive");
    }

    /**
     * Validates the given {@code mass} value.
     *
     * @param mass The mass value to be validated.
     * @throws IllegalArgumentException In case the given {@code mass} value is not value (i.e is not positive).
     */
    private static void validateMass(final double mass) throws IllegalArgumentException {
        Assert.isTrue(mass > 0, "The mass must be positive");
    }

    /**
     * Validates the given {@code vector}.
     *
     * @param vector The {@link Vector2D} to be validated.
     * @throws IllegalArgumentException In case the given {@code vector} is not valid (i.e is {@code null}).
     */
    private static void validateVector(final Vector2D vector) throws IllegalArgumentException {
        Assert.notNull(vector, "The given vector is null");
    }


    @Override
    public ParticleState outputState() {
        return new ParticleState(this);
    }

    /**
     * Represents the state of a given particle.o
     */
    public static final class ParticleState implements State {

        /**
         * The {@link Particle}'s mass.
         */
        private final double mass;

        /**
         * The {@link Particle}'s radius.
         */
        private final double radius;

        /**
         * The {@link Particle}'s position (represented as a 2D vector).
         */
        private final Vector2D position;

        /**
         * The {@link Particle}'s velocity (represented as a 2D vector).
         */
        private final Vector2D velocity;

        /**
         * The {@link Particle}'s acceleration (represented as a 2D vector).
         */
        private final Vector2D acceleration;

        /**
         * Constructor.
         *
         * @param particle The {@link Particle}'s whose state will be represented.
         */
        /* package */ ParticleState(final Particle particle) {
            mass = particle.getMass();
            radius = particle.getRadius();
            position = particle.getPosition(); // The Vector2D class is unmodifiable.
            velocity = particle.getVelocity(); // The Vector2D class is unmodifiable.
            acceleration = particle.getAcceleration(); // The Vector2D class is unmodifiable.
        }

        /**
         * The {@link Particle}'s mass.
         */
        public double getMass() {
            return mass;
        }

        /**
         * The {@link Particle}'s radius.
         */
        public double getRadius() {
            return radius;
        }

        /**
         * The {@link Particle}'s position (represented as a 2D vector).
         */
        public Vector2D getPosition() {
            return position;
        }

        /**
         * The {@link Particle}'s velocity (represented as a 2D vector).
         */
        public Vector2D getVelocity() {
            return velocity;
        }

        /**
         * @return The {@link Particle}'s acceleration (represented as a 2D vector).
         */
        public Vector2D getAcceleration() {
            return acceleration;
        }
    }
}
