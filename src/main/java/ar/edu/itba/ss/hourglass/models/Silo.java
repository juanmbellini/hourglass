package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.g7.engine.models.System;
import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.hourglass.utils.Constants;
import ar.edu.itba.ss.hourglass.utils.ParticleProvider;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the silo to be simulated (i.e the {@link System} to be simulated).
 */
public class Silo implements System<Silo.SiloState> {

    /**
     * The max. amount of consecutive failed tries of re-spawning a {@link Particle}
     */
    private static final int MAX_AMOUNT_OF_TRIES = 1000;


    // ================================================================================================================
    // Shape stuff
    // ================================================================================================================

    /**
     * The silo's top wall.
     */
    private final Wall topWall;

    /**
     * The silo's left wall.
     */
    private final Wall leftWall;

    /**
     * The silo's right wall.
     */
    private final Wall rightWall;

    /**
     * The silo's bottom wall that is left to the hole.
     */
    private final Wall leftBottomWall;

    /**
     * The silo's bottom wall that is right to the hole.
     */
    private final Wall rightBottomWall;

    /**
     * The silo's length (i.e used for re-spawning {@link Particle}s).
     */
    private final double siloLength;

    /**
     * The silo's width (i.e used for re-spawning {@link Particle}s).
     */
    private final double siloWidth;


    // ================================================================================================================
    // Particles
    // ================================================================================================================

    /**
     * The {@link Particle}s in this silo.
     */
    private final List<Particle> particles;

    /**
     * The {@link ParticleProvider} that will populate this silo with {@link Particle}s.
     */
    private final ParticleProvider particleProvider;


    // ================================================================================================================
    // Integration
    // ================================================================================================================

    /**
     * The time step.
     */
    private final double timeStep;

    /**
     * The simulation duration.
     */
    private final double duration;

    /**
     * The actual time.
     */
    private double actualTime;

    /**
     * The {@link Integrator} used to move {@link Particle}s.
     */
    private final Integrator integrator;

    // ================================================================================================================
    // Physics
    // ================================================================================================================

    /**
     * The system's actual kinetic energy.
     */
    private double systemKineticEnergy;

    /**
     * The total amount of {@link Particle}s that have left the silo.
     */
    private long outsideParticles;

    /**
     * The actual amount of new {@link Particle}s outside the silo.
     */
    private long newOutside;

    /**
     * The total amount of {@link Particle}s that have been re-spawned.
     */
    private long reSpawnedAmount;


    // ================================================================================================================
    // Others
    // ================================================================================================================

    /**
     * Indicates whether this silo is clean (i.e can be used to perform the simulation from the beginning).
     */
    private boolean clean;

    // ================================================================================================================
    // Constructor
    // ================================================================================================================

    /**
     * Constructor.
     *
     * @param length             The silo's width.
     * @param width              The silo's length.
     * @param hole               The silo's hole size.
     * @param minRadius          The min. radius of a {@link Particle}.
     * @param maxRadius          The max. radius of a {@link Particle}.
     * @param mass               The mass of a {@link Particle}.
     * @param elasticConstant    The elastic constant.
     * @param dampingCoefficient The damping coefficient.
     * @param duration           The amount of time the simulation will last.
     */
    public Silo(final double length, final double width, final double hole,
                final double minRadius, final double maxRadius, final double mass,
                final double elasticConstant, final double dampingCoefficient,
                final double duration) {

        validateShape(length, width, hole);
        // TODO: validate radius, mass, etc

        // Shape stuff
        this.leftBottomWall = Wall.getLeftBottomWall(width, hole);
        this.rightBottomWall = Wall.getRightBottomWall(width, hole);
        this.leftWall = Wall.getLeftWall(length);
        this.rightWall = Wall.getRightWall(length, width);
        this.topWall = Wall.getTopWall(length, width);
        this.siloLength = length;
        this.siloWidth = width;

        // Particles
        this.particleProvider = new ParticleProvider(minRadius, maxRadius, mass,
                leftWall.getInitialPoint().getX(), rightWall.getFinalPoint().getX(),
                leftWall.getInitialPoint().getY(), leftWall.getFinalPoint().getY());
        this.particles = new LinkedList<>();
        this.particles.addAll(particleProvider.createParticles());

        // Integration
        this.timeStep = 0.001 * Math.sqrt(mass / elasticConstant);
        this.duration = duration;
        this.actualTime = 0;

        final List<Wall> walls = Stream.of(leftBottomWall, rightBottomWall, leftWall, rightWall, topWall)
                .collect(Collectors.toList());
        this.integrator = new BeemanIntegrator(particles, walls, length, width, timeStep,
                elasticConstant, dampingCoefficient, Constants.GRAVITY);

        // Physics
        saveKineticEnergy();
        this.outsideParticles = 0;
        this.newOutside = 0;
        this.reSpawnedAmount = 0;

        this.clean = true;
    }


    // ================================================================================================================
    // Getters
    // ================================================================================================================

    /**
     * @return The silo's top wall.
     */
    public Wall getTopWall() {
        return topWall;
    }

    /**
     * @return The silo's left wall.
     */
    public Wall getLeftWall() {
        return leftWall;
    }

    /**
     * @return The silo's right wall.
     */
    public Wall getRightWall() {
        return rightWall;
    }

    /**
     * @return The silo's bottom wall that is left to the hole.
     */
    public Wall getLeftBottomWall() {
        return leftBottomWall;
    }

    /**
     * @return The silo's bottom wall that is right to the hole.
     */
    public Wall getRightBottomWall() {
        return rightBottomWall;
    }

    /**
     * @return The {@link Particle}s in this silo.
     */
    public List<Particle> getParticles() {
        return new LinkedList<>(particles);
    }

    /**
     * @return The system's actual kinetic energy.
     */
    public double getSystemKineticEnergy() {
        return systemKineticEnergy;
    }

    /**
     * @return The actual amount of new {@link Particle}s outside the silo.
     */
    public long getNewOutside() {
        return newOutside;
    }

    /**
     * @return Indicates whether the particle's state should be saved.
     */
    private boolean shouldStoreParticles() {
        return (actualTime / timeStep) % 600 == 0;
    }

    /**
     * Indicates whether the simulation should stop.
     *
     * @return {@code true} if the simulation should stop, or {@code false} otherwise.
     */
    public boolean shouldStop() {
        return actualTime > duration;
    }


    // ================================================================================================================
    // Interface stuff
    // ================================================================================================================

    @Override
    public void update() {
        clean = false;
        integrator.update();
        saveKineticEnergy();
        final long belowZero = particles.stream()
                .map(Particle::getPosition)
                .mapToDouble(Vector2D::getY)
                .count();
        this.newOutside = belowZero - outsideParticles + reSpawnedAmount;
        this.outsideParticles += this.newOutside;
        respawn();

        actualTime += timeStep;
    }

    @Override
    public void restart() {
        if (clean) {
            return;
        }

        this.particles.clear();
        this.particles.addAll(this.particleProvider.createParticles());
        this.integrator.setNewParticles(particles);
        this.actualTime = 0;
        this.clean = true;
    }

    @Override
    public SiloState outputState() {
        return new SiloState(this);
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Validates the given shape values.
     *
     * @param length The silo's length to be validated.
     * @param width  The silo's width to be validated.
     * @param hole   The silo's hole size to be validated.
     * @throws IllegalArgumentException If any value is not valid.
     */
    private static void validateShape(final double length, final double width, final double hole)
            throws IllegalArgumentException {
        Assert.isTrue(length > width && width > hole, "The given values of shape are not valid");
    }

    /**
     * Saves the actual kinetic energy of the system.
     */
    private void saveKineticEnergy() {
        this.systemKineticEnergy = particles.stream().mapToDouble(Particle::getKineticEnergy).sum();
    }

    /**
     * Performs the respawn of the {@link Particle}s.
     */
    private void respawn() {
        final List<Particle> insideTheSilo = particles.stream()
                .filter(particle -> particle.getPosition().getY() > 0)
                .collect(Collectors.toList());
        if (insideTheSilo.isEmpty()) {
            for (Particle particle : particles) {
                respawn(particle, 0, 3 * particle.getRadius());
            }
            this.integrator.reportReSpawned(particles);
        }

        final double mean = insideTheSilo.stream()
                .map(Particle::getPosition)
                .mapToDouble(Vector2D::getY)
                .average()
                .orElseThrow(() -> new RuntimeException("This should not happen"));
        final double standardDeviation = insideTheSilo.stream()
                .map(Particle::getPosition)
                .mapToDouble(Vector2D::getY)
                .map(y -> y - mean)
                .map(y -> y * y)
                .average()
                .orElseThrow(() -> new RuntimeException("This should not happen"));
        final List<Particle> mustRespawn = particles
                .stream()
                .filter(particle -> particle.getPosition().getY() < -siloLength / 10d)
                .collect(Collectors.toList());
        final double minY = mean + 2 * standardDeviation;
        for (Particle particle : mustRespawn) {
            respawn(particle, minY + 4 * particle.getRadius(), minY + 8 * particle.getRadius());
        }
        this.integrator.reportReSpawned(mustRespawn);
    }

    /**
     * Performs the respawn of the given {@code particle}.
     *
     * @param particle    The {@link Particle} to be re-spawned.
     * @param yRespawnMin The min. 'y' at which the given {@code particle} can be re-spawned.
     * @param yRespawnMax The max. 'y' at which the given {@code particle} can be re-spawned.
     */
    private void respawn(final Particle particle, final double yRespawnMin, final double yRespawnMax) {
        int tries = 0;
        while (tries < MAX_AMOUNT_OF_TRIES) {
            final double radius = particle.getRadius();
            final double xPosition = radius + new Random().nextDouble() * (siloWidth - 2 * radius);
            final double yPosition = yRespawnMin + new Random().nextDouble() * (yRespawnMax - yRespawnMin);
            final Vector2D position = new Vector2D(xPosition, yPosition);
            if (particles.stream().noneMatch(p -> p.doOverlap(position, radius))) {
                particle.setPosition(position);
                particle.setVelocity(Vector2D.ZERO);
                particle.setAcceleration(Constants.GRAVITY);
                this.reSpawnedAmount++;
                return;
            } else {
                tries++;
            }
        }
    }

    /**
     * Represents the state of a {@link Silo}.
     */
    public static final class SiloState implements State {

        /**
         * The silo's top wall.
         */
        private final Wall.WallState topWall; // TODO: maybe is not needed

        /**
         * The silo's left wall.
         */
        private final Wall.WallState leftWall;

        /**
         * The silo's right wall.
         */
        private final Wall.WallState rightWall;

        /**
         * The silo's bottom wall that is left to the hole.
         */
        private final Wall.WallState leftBottomWall;

        /**
         * The silo's bottom wall that is right to the hole.
         */
        private final Wall.WallState rightBottomWall;

        /**
         * The {@link Particle}s in this silo.
         */
        private final List<Particle.ParticleState> particleStates;

        /**
         * The system's kinetic energy.
         */
        private final double kineticEnergy;

        /**
         * The new amount of particle's that have left the silo in this state (i.e the flow).
         */
        private final long newOutside;


        /**
         * Constructor.
         *
         * @param silo The {@link Silo} owning this state.
         */
        public SiloState(final Silo silo) {
            this.topWall = silo.getTopWall().outputState();
            this.leftWall = silo.getLeftWall().outputState();
            this.rightWall = silo.getRightWall().outputState();
            this.leftBottomWall = silo.getLeftBottomWall().outputState();
            this.rightBottomWall = silo.getRightBottomWall().outputState();
            this.particleStates = silo.shouldStoreParticles() ? silo.getParticles().stream()
                    .map(Particle.ParticleState::new)
                    .collect(Collectors.toList()) : new LinkedList<>();
            this.kineticEnergy = silo.getSystemKineticEnergy();
            this.newOutside = silo.getNewOutside();
        }

        /**
         * @return The silo's top wall.
         */
        public Wall.WallState getTopWall() {
            return topWall;
        }

        /**
         * @return The silo's left wall.
         */
        public Wall.WallState getLeftWall() {
            return leftWall;
        }

        /**
         * @return The silo's right wall.
         */
        public Wall.WallState getRightWall() {
            return rightWall;
        }

        /**
         * @return The silo's bottom wall that is left to the hole.
         */
        public Wall.WallState getLeftBottomWall() {
            return leftBottomWall;
        }

        /**
         * @return The silo's bottom wall that is right to the hole.
         */
        public Wall.WallState getRightBottomWall() {
            return rightBottomWall;
        }

        /**
         * @return The {@link Particle}s in this silo.
         */
        public List<Particle.ParticleState> getParticleStates() {
            return particleStates;
        }

        /**
         * @return The system's kinetic energy.
         */
        public double getKineticEnergy() {
            return kineticEnergy;
        }

        /**
         * @return The new amount of particle's that have left the silo in this state (i.e the flow).
         */
        public long getNewOutside() {
            return newOutside;
        }

    }
}
