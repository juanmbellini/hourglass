package ar.edu.itba.ss.hourglass;

import ar.edu.itba.ss.g7.engine.io.DataSaver;
import ar.edu.itba.ss.g7.engine.simulation.SimulationEngine;
import ar.edu.itba.ss.hourglass.io.OvitoFileSaverImpl;
import ar.edu.itba.ss.hourglass.io.PhysicsDataSaver;
import ar.edu.itba.ss.hourglass.io.ProgramArguments;
import ar.edu.itba.ss.hourglass.models.Silo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.LinkedList;

/**
 * Main class.
 */
@SpringBootApplication
public class Hourglass implements CommandLineRunner, InitializingBean {

    /**
     * The {@link Logger} object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Hourglass.class);

    /**
     * The {@link SimulationEngine}.
     */
    private final SimulationEngine<Silo.SiloState, Silo> engine;

    /**
     * A {@link DataSaver} to create the Ovito file.
     */
    private final DataSaver<Silo.SiloState> ovitoFileSaver;

    /**
     * A {@link DataSaver} to create a physics file.
     */
    private final DataSaver<Silo.SiloState> physicsFileSaver;

    /**
     * Constructor.
     */
    @Autowired
    public Hourglass(final ProgramArguments programArguments) {
        final double siloLength = programArguments.getSiloShapeProperties().getSiloLength();
        final double siloWidth = programArguments.getSiloShapeProperties().getSiloWidth();
        final double siloHole = programArguments.getSiloShapeProperties().getSiloHoleSize();
        final double minRadius = programArguments.getParticleProperties().getMinDiameter() / 2;
        final double maxRadius = programArguments.getParticleProperties().getMaxDiameter() / 2;
        final double mass = programArguments.getParticleProperties().getMass();
        final double normalElasticConstant = programArguments.getParticleProperties().getNormalElasticConstant();
        final double viscousDampingCoefficient = programArguments.getParticleProperties().getViscousDampingCoefficient();
        final double duration = programArguments.getDuration();
        final Silo silo = new Silo(siloLength, siloWidth, siloHole, minRadius, maxRadius, mass,
                normalElasticConstant, viscousDampingCoefficient, duration);

        this.engine = new SimulationEngine<>(silo);
        this.ovitoFileSaver = new OvitoFileSaverImpl(programArguments.getOutputStuff().getOvitoFilePath());
        this.physicsFileSaver = new PhysicsDataSaver(programArguments.getOutputStuff().getPhysicsFilePath());
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.engine.initialize();
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Hello, Hourglass!");
        // First, simulate
        simulate();
        // Then, save
        save();
        LOGGER.info("Bye-bye!");
        System.exit(0);
    }


    /**
     * Performs the simulation phase of the program.
     */
    private void simulate() {
        LOGGER.info("Starting simulation...");
        this.engine.simulate(Silo::shouldStop);
        LOGGER.info("Finished simulation");
    }

    /**
     * Performs the save phase of the program.
     */
    private void save() {
        LOGGER.info("Saving outputs...");
        ovitoFileSaver.save(new LinkedList<>(this.engine.getResults()));
        physicsFileSaver.save(new LinkedList<>(this.engine.getResults()));
        // TODO: save here
        LOGGER.info("Finished saving output in all formats.");
    }

    /**
     * Entry point.
     *
     * @param args Program Arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Hourglass.class, args);
    }
}
