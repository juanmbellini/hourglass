package ar.edu.itba.ss.hourglass.models;

/**
 * Defines behaviour for an object that can be collided.
 */
public interface Collisionable {

    /**
     * Makes the given {@link Particle} collide with this {@link Collisionable}.
     *
     * @param particle The {@link Particle} colliding.
     */
    void collide(final Particle particle);
}