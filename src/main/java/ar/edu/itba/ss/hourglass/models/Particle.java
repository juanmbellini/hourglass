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


    /**
     * Returns the contact force {@code this} particle suffers
     * because of the action of the given {@code other} particle.
     *
     * @param other                     The other particle.
     * @param elasticConstant           The elastic constant.
     * @param viscousDampingCoefficient The viscous damping coefficient.
     * @return The contact force the {@code other} particle applies on {@code this} particle.
     * @apiNote If both particles are not in contact, the force will be a {@link Vector2D#ZERO} force.
     */
    public Vector2D getContactForce(final Particle other,
                                    // TODO: maybe saved? they are not part of a particle but of the system.
                                    final double elasticConstant, final double viscousDampingCoefficient) {
        if (!doOverlap(other)) {
            return Vector2D.ZERO; // If they do not overlap, the force is zero.
        }

        final Vector2D normalUnitVector = other.position.subtract(this.position).normalize();
        final Vector2D elasticForce = getElasticForce(normalUnitVector, elasticConstant, overlap(other));
        final Vector2D dampedForce = getDampedForce(normalUnitVector, viscousDampingCoefficient,
                other.velocity.subtract(this.velocity));

        return getContactForce(elasticForce, dampedForce);
    }

    /**
     * Returns the contact force  {@code this} particle suffers
     * because of the action of colliding with the given {@code wall}.
     *
     * @param wall                      The wall.
     * @param elasticConstant           The elastic constant.
     * @param viscousDampingCoefficient The viscous damping coefficient.
     * @return The contact force the {@code wall} applies on {@code this} particle.
     * @apiNote If the particle is not in contact with the {@code wall},
     * the force will be a {@link Vector2D#ZERO} force.
     */
    public Vector2D getContactForce(final Wall wall,
                                    // TODO: maybe saved? they are not part of a particle but of the system.
                                    final double elasticConstant, final double viscousDampingCoefficient) {
        final Vector2D projectionInWall = projectionInWall(wall);
        final Vector2D fromWallNormal = projectionInWall.subtract(position);
        final double overlap = radius - fromWallNormal.getNorm();
        if (overlap <= 0) {
            return Vector2D.ZERO; // If they do not overlap, the force is zero.
        }

        final Vector2D normalUnitVector = fromWallNormal.normalize();
        final Vector2D elasticForce = getElasticForce(normalUnitVector, elasticConstant, overlap);
        final Vector2D dampedForce = getDampedForce(normalUnitVector, viscousDampingCoefficient,
                velocity.scalarMultiply(-1));

        return getContactForce(elasticForce, dampedForce);
    }

    /**
     * Returns the contact force.
     *
     * @param elasticForce The elastic force component.
     * @param dampedForce  The damped force component.
     * @return The contact force.
     * @implNote This method just sums both forces.
     */
    private Vector2D getContactForce(final Vector2D elasticForce, final Vector2D dampedForce) {
        return elasticForce.add(dampedForce);
    }

    /**
     * Calculates the elastic force.
     *
     * @param normalUnitVector The normal unit {@link Vector2D} that exists between {@code this} particle
     *                         and the body being collided.
     * @param elasticConstant  The elastic constant.
     * @param overlap          The amount of distance being overlapped.
     * @return The elastic force.
     */
    private Vector2D getElasticForce(final Vector2D normalUnitVector,
                                     final double elasticConstant, final double overlap) {
        return normalUnitVector
                .scalarMultiply(elasticConstant)
                .scalarMultiply(overlap)
                .scalarMultiply(-1);
    }

    /**
     * Calculates the demped force.
     *
     * @param normalUnitVector          The normal unit {@link Vector2D} that exists between {@code this} particle
     *                                  and the body being collided.
     * @param viscousDampingCoefficient The viscous damping coefficient.
     * @param relativeVelocity          The relative velocity between {@code this} particle and the body being collided.
     * @return The damped force.
     */
    private Vector2D getDampedForce(final Vector2D normalUnitVector,
                                    final double viscousDampingCoefficient, final Vector2D relativeVelocity) {
        return normalUnitVector
                .scalarMultiply(relativeVelocity.dotProduct(normalUnitVector))
                .scalarMultiply(viscousDampingCoefficient)
                .scalarMultiply(-1);
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

    // ================================================================================================================
    // Others
    // ================================================================================================================

    /**
     * Checks if {@code this} particle overlaps with the given {@code other} particle.
     *
     * @param other The other particle.
     * @return {@code true} if they overlap {@code this}, or {@code false} otherwise.
     */
    public boolean doOverlap(final Particle other) {
        return doOverlap(other.position, other.radius);
    }

    /**
     * Checks if another particle can be created with the given {@code position} and {@code radius} arguments.
     *
     * @param position The position where the new particle will be created.
     * @param radius   The radius of the new particle.
     * @return {@code true} if the new particle would overlap {@code this} particle, or {@code false} otherwise.
     */
    public boolean doOverlap(final Vector2D position, final double radius) {
        return overlap(this.position, position, this.radius, radius) > 0;
    }

    /**
     * Calculates how much {@code this} particle overlaps with the given {@code other particle}.
     *
     * @param other The other particle.
     * @return The overlapping amount between the particles.
     */
    public double overlap(final Particle other) {
        return overlap(this.position, other.position, this.radius, other.radius);
    }

    /**
     * Returns the projection of the {@link #position} of {@code this} particle in the given {@code wall}.
     *
     * @param wall The {@link Wall} in which the projection is made.
     * @return The projection {@link Vector2D}.
     */
    private Vector2D projectionInWall(final Wall wall) {
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
     * @apiNote A value of zero or less indicates that there is no overlapping.
     */
    private static double overlap(Vector2D position1, Vector2D position2, double radius1, double radius2) {
        final double value = radius1 + radius2 - position1.distance(position2);
        return value < 0 ? 0 : value;
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
