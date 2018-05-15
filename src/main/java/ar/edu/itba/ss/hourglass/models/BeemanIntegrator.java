package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.hourglass.models.ContactForceCalculator.ParticlePropertiesWrapper;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Object in charge of integrating the movement of a {@link Particle}.
 */
/* package */ class BeemanIntegrator implements Integrator {

    /**
     * The {@link List} of {@link Particle}s that are going to be integrated.
     */
    private final List<Particle> particles;

    /**
     * The {@link Wall}s of the silo.
     */
    private final List<Wall> walls;

    /**
     * The silo's length.
     */
    private final double siloLength;

    /**
     * The silo's width.
     */
    private final double siloWidth;

    /**
     * The time step (i.e used for the integration).
     */
    private final double timeStep;

    /**
     * A {@link Map} containing the previous accelerations.
     */
    private final Map<Particle, Vector2D> previousAccelerations;

    /**
     * A {@link ContactForceCalculator} used to calculate the next acceleration.
     */
    private final ContactForceCalculator contactForceCalculator;

    /**
     * The gravity (i.e as a {@link Vector2D}).
     */
    private final Vector2D gravity;


    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param particles         The {@link List} of {@link Particle}s that are going to be integrated.
     * @param walls             The {@link Wall}s of the silo.
     * @param siloLength        The silo's length.
     * @param siloWidth         The silo's width.
     * @param timeStep          The time step (i.e used for the integration).
     * @param elasticConstant   The elastic constant.
     * @param dampedCoefficient THe damped coefficient.
     * @param gravity           The gravity (i.e as a {@link Vector2D}).
     */
    /* package */ BeemanIntegrator(final List<Particle> particles, final List<Wall> walls,
                                   final double siloLength, final double siloWidth, final double timeStep,
                                   final double elasticConstant, final double dampedCoefficient,
                                   final Vector2D gravity) {
        this.particles = particles;
        this.walls = walls;
        this.siloLength = siloLength;
        this.siloWidth = siloWidth;
        this.timeStep = timeStep;
        this.gravity = gravity;
        this.previousAccelerations = new HashMap<>();
        setPreviousAccelerations();
        this.contactForceCalculator = new ContactForceCalculator(elasticConstant, dampedCoefficient);
    }


    // ================================================================================================================
    // Interface methods
    // ================================================================================================================

    @Override
    public void update() {
        final Map<Particle, List<Particle>> neighborHoods = particles.stream()
                .collect(Collectors.toMap(Function.identity(), particle -> particles.stream()
                        .parallel()
                        .filter(p -> !p.equals(particle))
                        .filter(p -> particle.doOverlap(p.getPosition(), p.getRadius()))
                        .collect(Collectors.toList())));
        final Map<Particle, Vector2D> actualAccelerations = particles.stream()
                .collect(Collectors.toMap(Function.identity(), Particle::getAcceleration));
        neighborHoods.entrySet()
                .stream()
                .parallel()
                .map(entry -> update(entry.getKey(), entry.getValue()))
                .forEach(IntegrationResults::updateParticle);
        this.previousAccelerations.putAll(actualAccelerations);
    }

    @Override
    public void setNewParticles(final List<Particle> newParticles) {
        this.particles.clear();
        this.particles.addAll(newParticles);
        setPreviousAccelerations();
    }

    @Override
    public void reportReSpawned(final List<Particle> particles) {
        for (Particle particle : particles) {
            previousAccelerations.put(particle, gravity);
        }
    }

    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Sets the new initial previous accelerations values.
     */
    private void setPreviousAccelerations() {
        this.previousAccelerations.clear();
        final Map<Particle, Vector2D> newValues = particles.stream()
                .collect(Collectors.toMap(Function.identity(), p -> gravity));
        this.previousAccelerations.putAll(newValues);
    }

    /**
     * Calculates the integration results for the given {@code particle}.
     *
     * @param particle  The {@link Particle} to which the integration will be performed.
     * @param neighbors The neighbors of the {@link Particle}
     *                  (i.e those which might be in contact with the given {@code particle}).
     * @return The {@link IntegrationResults}.
     */
    private IntegrationResults update(final Particle particle, final List<Particle> neighbors) {

        final Vector2D particleNextPosition = nextPosition(particle);
        final Map<Particle, Vector2D> neighborsNextPosition = neighbors.stream()
                .collect(Collectors.toMap(Function.identity(), this::nextPosition));

        final Vector2D particlePredictedVelocity = predictVelocity(particle);
        final Map<Particle, Vector2D> neighborsPredictedVelocity = neighbors.stream()
                .collect(Collectors.toMap(Function.identity(), this::predictVelocity));

        final ParticlePropertiesWrapper particleWrapper = new ParticlePropertiesWrapper(particle.getRadius(),
                particleNextPosition, particlePredictedVelocity);


        final Stream<Vector2D> neighborsContactForceStream = neighbors.stream()
                .map(p -> new ParticlePropertiesWrapper(p.getRadius(),
                        neighborsNextPosition.get(p), neighborsPredictedVelocity.get(p)))
                .map(neighborWrapper -> contactForceCalculator.betweenParticles(particleWrapper, neighborWrapper));
        final Stream<Vector2D> wallsContactForceStream = walls.stream()
                .map(wall -> contactForceCalculator.betweenParticleAndWall(particleWrapper, wall));
        final Vector2D totalContactForce = Stream.concat(neighborsContactForceStream, wallsContactForceStream)
                .reduce(Vector2D.ZERO, Vector2D::add);

        final Vector2D totalForce = totalContactForce.add(gravity.scalarMultiply(particle.getMass()));
        final Vector2D acceleration = totalForce.scalarMultiply(1 / particle.getMass());

        final Vector2D correctedVelocity = correctVelocity(particle, acceleration);


        return new IntegrationResults(particle, particleNextPosition, correctedVelocity, acceleration);
    }

    /**
     * Calculates the new position for the given {@code particle}.
     *
     * @param particle The {@link Particle} to which the next position will be calculated.
     * @return The new position.
     * @throws IllegalStateException In case the particle does not have a previous acceleration.
     */
    private Vector2D nextPosition(final Particle particle) throws IllegalStateException {
        final Vector2D actualPosition = particle.getPosition();
        final Vector2D actualVelocity = particle.getVelocity();
        final Vector2D actualAcceleration = particle.getAcceleration();
        final Vector2D previousAcceleration = Optional.ofNullable(previousAccelerations.get(particle))
                .orElseThrow(() -> new IllegalStateException("Particle not being integrated" +
                        " or not previous acceleration calculated"));

        return actualPosition
                .add(actualVelocity.scalarMultiply(timeStep))
                .add(actualAcceleration.scalarMultiply((2d / 3d) * timeStep * timeStep))
                .subtract(previousAcceleration.scalarMultiply((1d / 6d) * timeStep * timeStep));
    }

    /**
     * Calculates the velocity prediction for the given {@code particle}.
     *
     * @param particle The {@link Particle} to which the velocity prediction will be calculated.
     * @return The velocity prediction.
     * @throws IllegalStateException In case the particle does not have a previous acceleration.
     */
    private Vector2D predictVelocity(final Particle particle) throws IllegalStateException {
        final Vector2D actualVelocity = particle.getVelocity();
        final Vector2D actualAcceleration = particle.getAcceleration();
        final Vector2D previousAcceleration = Optional.ofNullable(previousAccelerations.get(particle))
                .orElseThrow(() -> new IllegalStateException("Particle not being integrated" +
                        " or not previous acceleration calculated"));

        return actualVelocity
                .add(actualAcceleration.scalarMultiply((3d / 2d) * timeStep))
                .subtract(previousAcceleration.scalarMultiply((1d / 2d) * timeStep));
    }

    /**
     * Calculates the velocity correction for the given {@code particle}.
     *
     * @param particle The {@link Particle} to which the velocity correction will be calculated.
     * @return The velocity correction.
     * @throws IllegalStateException In case the particle does not have a previous acceleration.
     */
    private Vector2D correctVelocity(final Particle particle, final Vector2D nextAcceleration)
            throws IllegalStateException {
        final Vector2D actualVelocity = particle.getVelocity();
        final Vector2D actualAcceleration = particle.getAcceleration();
        final Vector2D previousAcceleration = Optional.ofNullable(previousAccelerations.get(particle))
                .orElseThrow(() -> new IllegalStateException("Particle not being integrated" +
                        " or not previous acceleration calculated"));

        return actualVelocity
                .add(nextAcceleration.scalarMultiply((1d / 3d) * timeStep))
                .add(actualAcceleration.scalarMultiply((5d / 6d) * timeStep))
                .subtract(previousAcceleration.scalarMultiply((1d / 6d) * timeStep));
    }



    /**
     * Class containing the integration results for a given {@link Particle}.
     */
    private static final class IntegrationResults {

        /**
         * The particle to which the integration has been performed.
         */
        private final Particle particle;
        /**
         * The next position for the {@link Particle}.
         */
        private final Vector2D position;
        /**
         * The next velocity for the {@link Particle}.
         */
        private final Vector2D velocity;
        /**
         * The next acceleration for the {@link Particle}.
         */
        private final Vector2D acceleration;

        /**
         * Constructor.
         *
         * @param particle     The particle to which the integration has been performed.
         * @param position     The next position for the {@link Particle}.
         * @param velocity     The next velocity for the {@link Particle}.
         * @param acceleration The next acceleration for the {@link Particle}.
         */
        private IntegrationResults(final Particle particle,
                                   final Vector2D position, final Vector2D velocity, final Vector2D acceleration) {
            this.particle = particle;
            this.position = position;
            this.velocity = velocity;
            this.acceleration = acceleration;
        }

        /**
         * Performs the update phase of the integration (i.e sets the integration results to the {@link Particle}).
         */
        private void updateParticle() {
            particle.setPosition(position);
            particle.setVelocity(velocity);
            particle.setAcceleration(acceleration);
        }
    }
}
