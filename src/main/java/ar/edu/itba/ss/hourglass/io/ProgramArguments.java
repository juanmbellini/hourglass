package ar.edu.itba.ss.hourglass.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class containing the program arguments.
 */
@Component
public class ProgramArguments {


    /**
     * The particles' stuff.
     */
    private final ParticleProperties particleProperties;

    /**
     * The silo's stuff.
     */
    private final SiloShapeProperties siloShapeProperties;

    /**
     * Constructor.
     *
     * @param particleProperties  The particles' stuff.
     * @param siloShapeProperties The silo's stuff.
     */
    @Autowired
    public ProgramArguments(final ParticleProperties particleProperties,
                            final SiloShapeProperties siloShapeProperties) {
        this.particleProperties = particleProperties;
        this.siloShapeProperties = siloShapeProperties;
    }

    /**
     * @return The particles' stuff.
     */
    public ParticleProperties getParticleProperties() {
        return particleProperties;
    }

    /**
     * @return The silo's stuff.
     */
    public SiloShapeProperties getSiloShapeProperties() {
        return siloShapeProperties;
    }

    /**
     * Particles' stuff.
     */
    @Component
    public static final class ParticleProperties {

        /**
         * The min. diameter for a particle.
         */
        private final double minDiameter;

        /**
         * The max. diameter for a particle.
         */
        private final double maxDiameter;

        /**
         * The normal elastic constant.
         */
        private final double normalElasticConstant;

        /**
         * The particle's mass.
         */
        private final double mass;

        /**
         * Constructor.
         *
         * @param minDiameter           The min. diameter for a particle.
         * @param maxDiameter           The max. diameter for a particle.
         * @param normalElasticConstant The normal elastic constant.
         * @param mass                  The particle's mass.
         */
        @Autowired
        public ParticleProperties(@Value("${custom.system.particle.min-diameter}") final double minDiameter,
                                  @Value("${custom.system.particle.max-diameter}") final double maxDiameter,
                                  @Value("${custom.system.particle.kn}") final double normalElasticConstant,
                                  @Value("${custom.system.particle.mass}") final double mass) {
            this.minDiameter = minDiameter;
            this.maxDiameter = maxDiameter;
            this.normalElasticConstant = normalElasticConstant;
            this.mass = mass;
        }

        /**
         * @return The min. diameter for a particle.
         */
        public double getMinDiameter() {
            return minDiameter;
        }

        /**
         * @return The max. diameter for a particle.
         */
        public double getMaxDiameter() {
            return maxDiameter;
        }

        /**
         * @return The normal elastic constant.
         */
        public double getNormalElasticConstant() {
            return normalElasticConstant;
        }

        /**
         * @return The particle's mass.
         */
        public double getMass() {
            return mass;
        }
    }

    /**
     * Silo shape's stuff.
     */
    @Component
    public static final class SiloShapeProperties {

        /**
         * The silo's length.
         */
        private final double siloLength;

        /**
         * The silo's width.
         */
        private final double siloWidth;

        /**
         * The silo's hole size.
         */
        private final double siloHoleSize;

        /**
         * Constructor.
         *
         * @param siloLength   The silo's length.
         * @param siloWidth    The silo's width.
         * @param siloHoleSize The silo's hole size.
         */
        @Autowired
        public SiloShapeProperties(@Value("${custom.system.silo.L}") final double siloLength,
                                   @Value("${custom.system.silo.W}") final double siloWidth,
                                   @Value("${custom.system.silo.D}") final double siloHoleSize) {
            this.siloLength = siloLength;
            this.siloWidth = siloWidth;
            this.siloHoleSize = siloHoleSize;
        }

        /**
         * @return The silo's length.
         */
        public double getSiloLength() {
            return siloLength;
        }

        /**
         * @return The silo's width.
         */
        public double getSiloWidth() {
            return siloWidth;
        }

        /**
         * @return The silo's hole size.
         */
        public double getSiloHoleSize() {
            return siloHoleSize;
        }
    }
}
