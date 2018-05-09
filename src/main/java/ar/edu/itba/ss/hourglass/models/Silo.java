package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.g7.engine.models.System;
import ar.edu.itba.ss.g7.engine.simulation.State;
import ar.edu.itba.ss.hourglass.utils.ParticleProvider;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the silo to be simulated (i.e the {@link System} to be simulated).
 */
public class Silo implements System<Silo.SiloState> {

    /**
     * The silo's top wall.
     */
    private final Wall topWall; // TODO: maybe is not needed

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
     * The normal elastic constant.
     */
    private final double normalElasticConstant;

    /**
     * The {@link Particle}s in this silo.
     */
    private final List<Particle> particles;

    /**
     * The {@link ParticleProvider} that will populate this silo with {@link Particle}s.
     */
    private final ParticleProvider particleProvider;


    /**
     * Constructor.
     *
     * @param width                 The silo's length.
     * @param length                The silo's width.
     * @param hole                  The silo's hole size.
     * @param normalElasticConstant The normal elastic constant.
     * @param minRadius             The min. radius of a {@link Particle}.
     * @param maxRadius             The max. radius of a {@link Particle}.
     * @param mass                  The mass of a {@link Particle}.
     */
    public Silo(final double length, final double width, final double hole,
                final double minRadius, final double maxRadius, final double mass,
                final double normalElasticConstant) {
        validateShape(length, width, hole);
        // TODO: validate radius, mass and elasticConstant
        final double bottomWallLength = (width - hole) / 2d;
        this.leftBottomWall = Wall.getHorizontal(Vector2D.ZERO, bottomWallLength);
        this.rightBottomWall = Wall.getHorizontal(new Vector2D(bottomWallLength + hole, 0), bottomWallLength);
        this.leftWall = Wall.getVertical(Vector2D.ZERO, length);
        this.rightWall = Wall.getVertical(new Vector2D(width, 0), length);
        this.topWall = Wall.getHorizontal(new Vector2D(length, 0), width);
        this.normalElasticConstant = normalElasticConstant;
        this.particleProvider = new ParticleProvider(minRadius, maxRadius, mass,
                leftBottomWall.getInitialPoint().getX(), rightWall.getFinalPoint().getX(),
                leftBottomWall.getInitialPoint().getY(), rightWall.getFinalPoint().getY());
        this.particles = new LinkedList<>();
        this.particles.addAll(particleProvider.createParticles());
    }

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

    public boolean isStabilized() {
        return false; // TODO: implement
    }

    @Override
    public void update() {
        // TODO: implement
    }

    @Override
    public void restart() {
        this.particles.clear();
        this.particles.addAll(this.particleProvider.createParticles());
    }

    @Override
    public SiloState outputState() {
        return new SiloState(this);
    }

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
            this.particleStates = silo.particles.stream().map(Particle.ParticleState::new).collect(Collectors.toList());
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
    }
}
