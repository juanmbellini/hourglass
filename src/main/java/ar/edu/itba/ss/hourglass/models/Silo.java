package ar.edu.itba.ss.hourglass.models;

import ar.edu.itba.ss.g7.engine.models.System;
import ar.edu.itba.ss.g7.engine.simulation.State;

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
     * The {@link Particle}s in this silo.
     */
    private final List<Particle> particles;


    /**
     * Constructor.
     *
     * @param topWall         The silo's top wall.
     * @param leftWall        The silo's left wall.
     * @param rightWall       The silo's right wall.
     * @param leftBottomWall  The silo's bottom wall that is left to the hole.
     * @param rightBottomWall The silo's bottom wall that is right to the hole.
     */
    public Silo(Wall topWall, Wall leftWall, Wall rightWall, Wall leftBottomWall, Wall rightBottomWall) {
        this.topWall = topWall;
        this.leftWall = leftWall;
        this.rightWall = rightWall;
        this.leftBottomWall = leftBottomWall;
        this.rightBottomWall = rightBottomWall;
        this.particles = new LinkedList<>(); // TODO: fill with the provider.
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
        // TODO: implement
    }

    @Override
    public SiloState outputState() {
        return new SiloState(this);
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
