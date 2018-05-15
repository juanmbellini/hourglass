package ar.edu.itba.ss.hourglass.io;

import ar.edu.itba.ss.g7.engine.io.OvitoFileSaver;
import ar.edu.itba.ss.hourglass.models.Particle;
import ar.edu.itba.ss.hourglass.models.Silo;
import ar.edu.itba.ss.hourglass.models.Wall;

import java.io.IOException;
import java.io.Writer;

/**
 * {@link OvitoFileSaver} for the {@link Silo}.
 */
public class OvitoFileSaverImpl extends OvitoFileSaver<Silo.SiloState> {

    /**
     * Constructor.
     *
     * @param filePath Path to the file to be saved.
     */
    public OvitoFileSaverImpl(String filePath) {
        super(filePath);
    }

    @Override
    public void saveState(Writer writer, Silo.SiloState siloState, int frame) throws IOException {
        final StringBuilder data = new StringBuilder()
                // First, headers
                .append(siloState.getParticleStates().size() + 2 * 5)
                .append("\n")
                .append(frame)
                .append("\n");

        for (Particle.ParticleState particle : siloState.getParticleStates()) {
            saveParticle(data, particle);
        }
        saveWall(data, siloState.getLeftWall());
        saveWall(data, siloState.getRightWall());
        saveWall(data, siloState.getLeftBottomWall());
        saveWall(data, siloState.getRightBottomWall());
        saveWall(data, siloState.getTopWall());
        // Append data into the Writer
        writer.append(data);
    }

    /**
     * Saves a {@link ar.edu.itba.ss.hourglass.models.Particle.ParticleState}
     * into the {@code data} {@link StringBuilder}.
     *
     * @param data     The {@link StringBuilder} that is collecting data.
     * @param particle The {@link ar.edu.itba.ss.hourglass.models.Particle.ParticleState} with the data.
     */
    private static void saveParticle(final StringBuilder data, Particle.ParticleState particle) {
        data.append("")
                .append(particle.getPosition().getX())
                .append(" ")
                .append(particle.getPosition().getY())
                .append(" ")
                .append(particle.getVelocity().getX())
                .append(" ")
                .append(particle.getVelocity().getY())
                .append(" ")
                .append(particle.getRadius())
                .append(" ")
                .append(1)
                .append(" ")
                .append(1)
                .append(" ")
                .append(1)
                .append("\n");
    }

    /**
     * Saves a {@link ar.edu.itba.ss.hourglass.models.Wall.WallState}
     * into the {@code data} {@link StringBuilder}.
     *
     * @param data The {@link StringBuilder} that is collecting data.
     * @param wall The {@link ar.edu.itba.ss.hourglass.models.Wall.WallState} with the data.
     */
    private static void saveWall(final StringBuilder data, final Wall.WallState wall) {
        data.append("")
                .append(wall.getInitialPoint().getX())
                .append(" ")
                .append(wall.getInitialPoint().getY())
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0.005)
                .append(" ")
                .append(1)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append("\n")
                .append(wall.getFinalPoint().getX())
                .append(" ")
                .append(wall.getFinalPoint().getY())
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0.005)
                .append(" ")
                .append(1)
                .append(" ")
                .append(0)
                .append(" ")
                .append(0)
                .append("\n");
    }
}
