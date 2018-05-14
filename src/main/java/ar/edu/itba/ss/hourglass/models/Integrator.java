package ar.edu.itba.ss.hourglass.models;

import java.util.List;

/**
 * Defines behaviour for an object that can integrate the movement of a {@link Particle}.
 */
public interface Integrator {

    /**
     * Updates the {@link Particle}s.
     */
    void update();

    /**
     * Changes the {@link List} of {@link Particle}s.
     *
     * @param newParticles The new {@link Particle}s.
     */
    void setNewParticles(final List<Particle> newParticles);
}
