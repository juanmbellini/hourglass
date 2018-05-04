package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.g7.engine.simulation.StateHolder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;


/**
 * Represents a particle in the system.
 */
public class Particle implements Collisionable, StateHolder<Particle.ParticleState> {

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
     * Constructor.
     *
     * @param mass      The particle's mass.
     * @param radius    The particle's radius.
     * @param xPosition The particle's 'x' component of the position.
     * @param yPosition The particle's 'y' component of the position.
     */
    public Particle(final double mass, final double radius, final double xPosition, final double yPosition) {
        this.mass = mass;
        this.radius = radius;
        position = new Vector2D(xPosition, yPosition);
        velocity = new Vector2D(0, 0);
    }

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
     * @return The particle's kinetic energy.
     */
    public double getKineticEnergy() {
        return HALF * mass * velocity.getNormSq();
    }

    /**
     * Sets a new velocity for this particle.
     *
     * @param xVelocity The new 'x' component of the velocity.
     * @param yVelocity The new 'y' component of the velocity.
     */
    public void setVelocity(final double xVelocity, final double yVelocity) {
        velocity = new Vector2D(xVelocity, yVelocity);
    }

    //TODO: Don't know if we are going to need this.
    @Override
    public void collide(final Particle other) {
        final Vector2D deltaR = getDeltaR(other);
        final Vector2D deltaV = getDeltaV(other);
        final double deltaVByDeltaR = deltaV.dotProduct(deltaR);
        final double sigma = getSigma(other);
        final double impulse = (2 * mass * other.mass * deltaVByDeltaR) / (sigma * (mass + other.mass));
        final double xImpulse = (impulse * deltaR.getX()) / sigma;
        final double yImpulse = (impulse * deltaR.getY()) / sigma;

        setVelocity(velocity.getX() + xImpulse / mass, velocity.getY() + yImpulse / mass);
        other.setVelocity(other.velocity.getX() - xImpulse / other.mass, other.velocity.getY() - yImpulse / other.mass);
    }

    /**
     * Calculates position difference between this particle and the {@code other} particle.
     *
     * @param other The other particle.
     * @return A {@link Vector2D} with the difference of positions.
     */
    private Vector2D getDeltaR(final Particle other) {
        return other.position.subtract(this.position);
    }

    /**
     * Calculates velocity difference between this particle and the {@code other} particle.
     *
     * @param other The other particle.
     * @return A {@link Vector2D} with the difference of positions.
     */
    private Vector2D getDeltaV(final Particle other) {
        return other.velocity.subtract(this.velocity);
    }

    /**
     * Calculates the distance between the mass center of this particle and the {@code other} particle.
     *
     * @param other The other particle.
     * @return A {@link Vector2D} with the difference of positions.
     */
    private double getSigma(final Particle other) {
        return this.radius + other.radius;
    }

    /*
     * Check's if another {@link Particle}'s position overlaps this Particle's space.
     *
     * @param position A Particle's position.
     * @param radius A Particle's radius.
     * @return True if the Particle overlaps the position, false if not.
     */
    public boolean doOverlap(final Vector2D position, final double radius) {
        final Vector2D dif = this.position.subtract(position);
        return dif.getX() <= (radius + this.radius) || dif.getY() <= (radius + this.radius);
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
         * Constructor.
         *
         * @param particle The {@link Particle}'s whose state will be represented.
         */
        /* default */ ParticleState(final Particle particle) {
            mass = particle.getMass();
            radius = particle.getRadius();
            position = particle.getPosition(); // The Vector2D class is unmodifiable.
            velocity = particle.getVelocity(); // The Vector2D class is unmodifiable.
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
    }
}
