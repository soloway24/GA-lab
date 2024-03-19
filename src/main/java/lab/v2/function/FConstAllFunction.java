package lab.v2.function;

import lab.model.Individual;
import lab.parameters.Encoding;
import lab.v2.parameters.OperatorsApplicationType;
import lab.v2.population.PopulationConfiguration;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;
import static lab.model.Individual.ALL_100_ZEROS_INDIVIDUAL;
import static lab.parameters.Encoding.STANDARD;
import static lab.v2.population.PopulationConfiguration.RANDOM;

public class FConstAllFunction implements FitnessFunctionV2<Number, Integer> {

    private static FConstAllFunction instance;

    private FConstAllFunction() {
    }

    public static FConstAllFunction getInstance() {
        return ofNullable(instance)
                .orElse(new FConstAllFunction());
    }

    @Override
    public String getName() {
        return "FconstALL";
    }

    @Override
    public int getChromosomeLength() {
        return 100;
    }

    @Override
    public List<Encoding> getSupportedEncodings() {
        return List.of(STANDARD);
    }

    @Override
    public Integer getMinFitness() {
        return 100;
    }

    @Override
    public Integer getMaxFitness() {
        return 100;
    }

    @Override
    public Optional<Number> getMinX() {
        return of(0);
    }

    @Override
    public Optional<Number> getMaxX() {
        return empty();
    }

    @Override
    public Optional<Individual> getOptimalIndividual(Encoding encoding) {
        return of(ALL_100_ZEROS_INDIVIDUAL);
    }

    @Override
    public Integer evaluate(Number x) {
        return 100;
    }

    @Override
    public Optional<Number> convertToX(long decimalValue) {
        return empty();
    }

    @Override
    public List<PopulationConfiguration> getSupportedPopulationConfigurations(OperatorsApplicationType operatorsApplicationType) {
        return List.of(RANDOM);
    }
}