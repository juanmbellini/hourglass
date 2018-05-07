package ar.edu.itba.ss.hourglass;

import ar.edu.itba.ss.g7.engine.simulation.SimulationEngine;
import ar.edu.itba.ss.hourglass.models.Silo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
     * Constructor.
     */
    @Autowired
    public Hourglass() {
        // TODO: initialize
        final Silo silo = new Silo(null, null, null, null, null);
        this.engine = new SimulationEngine<>(silo);
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
        this.engine.simulate(Silo::isStabilized);
        LOGGER.info("Finished simulation");
    }

    /**
     * Performs the save phase of the program.
     */
    private void save() {
        LOGGER.info("Saving outputs...");
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
