package lab.function;

import lab.Individual;
import lab.encoding.Encoding;
import lab.operator.OperatorType;
import lab.population.PopulationType;
import lab.selection.SelectorType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.*;
import static lab.encoding.Encoding.STANDARD;
import static lab.identifier.ConvergenceIdentifier.areAllEqualTo;
import static lab.identifier.ConvergenceIdentifier.areEqualToWithPercentage;
import static lab.util.MetricUtils.getZeroCount;

public class FhdFunction implements FitnessFunction<Number, Long> {

    private static final double SAME_PERCENTAGE = 0.9;
    private static final Individual OPTIMAL_INDIVIDUAL = new Individual("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", STANDARD);

    private static final Map<Encoding, Individual> ENCODING_TO_OPTIMAL = Map.of(
            STANDARD, OPTIMAL_INDIVIDUAL
    );

    private static FhdFunction instance;

    private final int selectiveAdvantage;

    public FhdFunction(int selectiveAdvantage) {
        this.selectiveAdvantage = selectiveAdvantage;
    }

    @Override
    public String getName() {
        return "FHD " + selectiveAdvantage;
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
        return 100L;
    }

    @Override
    public Long getMaxFitness() {
        return (long) selectiveAdvantage * getChromosomeLength();
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
        long zeroCount = getZeroCount(individual);
        return getChromosomeLength() - zeroCount + zeroCount * selectiveAdvantage;
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

        return hasConverged && areEqualToWithPercentage(individualToFitness.keySet(), OPTIMAL_INDIVIDUAL, SAME_PERCENTAGE);
    }

    @Override
    public boolean supportsDecoding() {
        return false;
    }

//    @Override
//    public Optional<Integer> getCustomRunPoolSize(SelectorType selectorType, OperatorType operatorType) {
//        return selectorType == SelectorType.SUS && operatorType == OperatorType.MUTATION
//                ? of(20)
//                : empty();
//    }
}