package lab.function;

import lab.Individual;
import lab.encoding.Encoding;
import lab.operator.OperatorType;
import lab.population.PopulationType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static lab.encoding.Encoding.STANDARD;
import static lab.identifier.ConvergenceIdentifier.areAllEqualTo;
import static lab.identifier.ConvergenceIdentifier.areTheSameWithPercentage;
import static lab.util.MetricUtils.getZeroCount;

public class FhFunction implements FitnessFunction<Number, Long> {

    private static final double SAME_PERCENTAGE = 0.9;
    private static final Individual OPTIMAL_INDIVIDUAL = new Individual("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", STANDARD);

    private static final Map<Encoding, Individual> ENCODING_TO_OPTIMAL = Map.of(
            STANDARD, OPTIMAL_INDIVIDUAL
    );

    private static FhFunction instance;

    private FhFunction() {
    }

    public static FhFunction getInstance() {
        return ofNullable(instance)
                .orElseGet(() -> {
                    instance = new FhFunction();
                    return instance;
                });
    }

    @Override
    public String getName() {
        return "FH";
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
    public Long getMinFitness() {
        return 0L;
    }

    @Override
    public Long getMaxFitness() {
        return 100L;
    }

    @Override
    public Optional<Number> getMinX() {
        return empty();
    }

    @Override
    public Optional<Number> getMaxX() {
        return empty();
    }

    @Override
    public Optional<Number> getOptimalX() {
        return empty();
    }

    @Override
    public Optional<Individual> getOptimalIndividual(Encoding encoding) {
        return ofNullable(ENCODING_TO_OPTIMAL.get(encoding))
                .map(Individual::new);
    }

    @Override
    public Long evaluate(Individual individual) {
        return getZeroCount(individual);
    }

    @Override
    public Optional<Number> convertToX(long decimalValue) {
        return empty();
    }

    @Override
    public List<PopulationType> getSupportedPopulationConfigurations(OperatorType operatorType) {
        if (operatorType == OperatorType.NONE) {
            return List.of(PopulationType.ONE_OPTIMAL);
//            return List.of(PopulationType.ONE_OPTIMAL, PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL);
        }

        return List.of(PopulationType.ZERO_OPTIMAL, PopulationType.ONE_OPTIMAL);
//        return List.of(PopulationType.ZERO_OPTIMAL, PopulationType.ONE_OPTIMAL, PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL);
    }

    @Override
    public boolean isSuccessful(Map<Individual, ? extends Number> individualToFitness, OperatorType operatorType, boolean hasConverged) {
        if (operatorType == OperatorType.NONE) {
            return hasConverged && areAllEqualTo(individualToFitness.keySet(), OPTIMAL_INDIVIDUAL);
        }

        return hasConverged && areTheSameWithPercentage(individualToFitness.keySet(), SAME_PERCENTAGE);
    }

    @Override
    public boolean supportsDecoding() {
        return false;
    }

}