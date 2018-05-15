package ar.edu.itba.ss.hourglass.io;

import ar.edu.itba.ss.g7.engine.io.TextFileSaver;
import ar.edu.itba.ss.hourglass.models.Silo;

import java.io.IOException;
import java.io.Writer;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Created by Juan Marcos Bellini on 14/5/18.
 */
public class PhysicsDataSaver extends TextFileSaver<Silo.SiloState> {


    /**
     * Constructor.
     *
     * @param filePath Path to the file to be saved.
     */
    public PhysicsDataSaver(String filePath) {
        super(filePath);
    }

    @Override
    public void doSave(Writer writer, Queue<Silo.SiloState> queue) throws IOException {
        // Save Kinetic energy.
        final String kineticEnergy = "kineticEnergy = [" + queue.stream()
                .map(Silo.SiloState::getKineticEnergy)
                .map(Object::toString)
                .collect(Collectors.joining(", ")) + "];";
        // Save Flow energy.
        final String flow = "flow = [" + queue.stream()
                .map(Silo.SiloState::getNewOutside)
                .map(Object::toString)
                .collect(Collectors.joining(", ")) + "];";

        // Append results into the Writer
        writer.append(kineticEnergy)
                .append("\n")
                .append(flow)
                .append("\n");

    }
}
