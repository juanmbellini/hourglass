package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.hourglass.utils.SpaceUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Object in charge of calculating contact forces.
 */
/* package */ class ContactForceCalculator {

    /**
     * Epsilon constant used to compare doubles.
     */
    private static final double EPSILON = Math.pow(10, -12);

    /**
     * The elastic constant.
     */
    private final double elasticConstant;

    /**
     * The viscous damping coefficient.
     */
    private final double viscousDampingCoefficient;

    /**
     * Constructor.
     *
     * @param elasticConstant           The elastic constant.
     * @param viscousDampingCoefficient The viscous damping coefficient.
     */
    /* package */ ContactForceCalculator(double elasticConstant, double viscousDampingCoefficient) {
        this.elasticConstant = elasticConstant;
        this.viscousDampingCoefficient = viscousDampingCoefficient;
    }


    /**
     * Calculates the contact force between two particles.
     *
     * @param wrapper1 The {@link ParticlePropertiesWrapper} containing the particle
     *                 to which the force is being applied properties
     * @param wrapper2 The {@link ParticlePropertiesWrapper} containing the particle
     *                 applying the force properties.
     * @return The contact force the second particle is applying on the first one.
     * @apiNote If the particles are not in contact, the force will be a {@link Vector2D#ZERO} force.
     */
    /* package */ Vector2D betweenParticles(final ParticlePropertiesWrapper wrapper1,
                                            final ParticlePropertiesWrapper wrapper2) {

        final double overlap = SpaceUtils.overlap(wrapper1.getPosition(), wrapper2.getPosition(),
                wrapper1.getRadius(), wrapper2.getRadius());
        if (overlap == 0) {
            return Vector2D.ZERO; // If they do not overlap, the force is zero.
        }

        final Vector2D normalUnitVector = wrapper2.getPosition().subtract(wrapper1.getPosition()).normalize();
        return getContactForce(normalUnitVector, overlap, wrapper1.getVelocity().subtract(wrapper2.getVelocity()));

    }


    /**
     * Calculates the contact force between a particle and a wall.
     *
     * @param wrapper The {@link ParticlePropertiesWrapper} containing the needed particle's properties.
     * @param wall    The {@link Wall} against which the particle is colliding.
     * @return The contact force the {@code wall} applies on the particle.
     * @apiNote If the particle is not in contact with the {@code wall},
     * the force will be a {@link Vector2D#ZERO} force.
     */
    /* package */ Vector2D betweenParticleAndWall(final ParticlePropertiesWrapper wrapper, final Wall wall) {
        final Vector2D position = wrapper.getPosition();
        final Vector2D projectionInWall = SpaceUtils.projectionInWall(position, wall);
        // First check directions
        final double dotProduct = projectionInWall.normalize().dotProduct(wall.getDirectionVector().normalize());
        if (dotProduct + 1 < EPSILON || projectionInWall.getNorm() > wall.getDirectionVector().getNorm()) {
            // If the dot product between both normalized vector is 1, then they are in the opposite directions.
            // If the dot product is not -1, then it is 1 (they are in the same direction),
            // so we must check that the projection in the wall is not bigger than the direction vector.
            return Vector2D.ZERO;
        }

        final Vector2D fromWallNormal = wall.getInitialPoint().add(projectionInWall).subtract(position);
        final double overlap = wrapper.getRadius() - fromWallNormal.getNorm();
        if (overlap <= 0) {
            return Vector2D.ZERO; // If they do not overlap, the force is zero.
        }

        final Vector2D normalUnitVector = fromWallNormal.normalize();
        return getContactForce(normalUnitVector, overlap, wrapper.getVelocity());
    }

    /**
     * Returns the contact force.
     *
     * @param normalUnitVector The normal unit {@link Vector2D} that exists between {@code this} particle
     *                         and the body being collided.
     * @param overlap          The amount of distance being overlapped.
     * @param relativeVelocity The relative velocity between {@code this} particle and the body being collided.
     * @return The contact force.
     * @apiNote This method expects the {@code normalUnitVector} has a norm of one unit
     * (i.e {@code normalUnitVector.getNorm() == 1d}.
     */
    private Vector2D getContactForce(final Vector2D normalUnitVector,
                                     final double overlap, final Vector2D relativeVelocity) {
        final Vector2D elasticForce = getElasticForce(normalUnitVector, elasticConstant, overlap);
        final Vector2D dampedForce = getDampedForce(normalUnitVector, viscousDampingCoefficient, relativeVelocity);
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
    private static Vector2D getElasticForce(final Vector2D normalUnitVector,
                                            final double elasticConstant, final double overlap) {
        return normalUnitVector
                .scalarMultiply(elasticConstant)
                .scalarMultiply(overlap)
                .scalarMultiply(-1);
    }

    /**
     * Calculates the damped force.
     *
     * @param normalUnitVector          The normal unit {@link Vector2D} that exists between {@code this} particle
     *                                  and the body being collided.
     * @param viscousDampingCoefficient The viscous damping coefficient.
     * @param relativeVelocity          The relative velocity between {@code this} particle and the body being collided.
     * @return The damped force.
     */
    private static Vector2D getDampedForce(final Vector2D normalUnitVector,
                                           final double viscousDampingCoefficient, final Vector2D relativeVelocity) {
        return normalUnitVector
                .scalarMultiply(relativeVelocity.dotProduct(normalUnitVector))
                .scalarMultiply(viscousDampingCoefficient)
                .scalarMultiply(-1)
                ;
    }


    /**
     * Bean class that contains the needed properties of a {@link Particle}.
     * This class allows to calculate forces without having to modify a particle
     * (i.e you can calculate the contact force in future steps).
     */
    /* package */ static final class ParticlePropertiesWrapper {

        /**
         * The particle's radius.
         */
        private final double radius;

        /**
         * The particle's position.
         */
        private final Vector2D position;

        /**
         * The particle's velocity.
         */
        private final Vector2D velocity;


        /**
         * Constructor.
         *
         * @param radius   The particle's radius.
         * @param position The particle's position.
         * @param velocity The particle's velocity.
         * @apiNote This constructor allows to calculate the contact force in any time
         * (just having the time's position and velocity).
         */
        /* package */ ParticlePropertiesWrapper(final double radius, final Vector2D position, final Vector2D velocity) {
            this.radius = radius;
            this.position = position;
            this.velocity = velocity;
        }

        /**
         * Constructor.
         *
         * @param particle The particle whose properties will be saved in this wrapper.
         * @apiNote This constructor allows to calculate the contact force in the actual time.
         */
        /* package */ ParticlePropertiesWrapper(final Particle particle) {
            this(particle.getRadius(), particle.getPosition(), particle.getVelocity());
        }


        /**
         * @return The particle's radius.
         */
        /* package */ double getRadius() {
            return radius;
        }

        /**
         * @return The particle's position.
         */
        /* package */ Vector2D getPosition() {
            return position;
        }

        /**
         * @return The particle's velocity.
         */
        /* package */ Vector2D getVelocity() {
            return velocity;
        }
    }
}
