package lab.function;

import lab.operator.OperatorType;
import lab.population.PopulationType;
import lab.encoding.Encoding;
import lab.Individual;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static lab.encoding.Encoding.STANDARD;
import static lab.identifier.ConvergenceIdentifier.areTheSameWithPercentage;

public class FConstAllFunction implements FitnessFunction<Number, Integer> {

    private static final double SAME_PERCENTAGE = 0.9;
    private static final Individual OPTIMAL_STANDARD_INDIVIDUAL = new Individual("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", STANDARD);

    private static final Map<Encoding, Individual> ENCODING_TO_OPTIMAL = Map.of(
            STANDARD, OPTIMAL_STANDARD_INDIVIDUAL
    );

    private static FConstAllFunction instance;

    private FConstAllFunction() {
    }

    public static FConstAllFunction getInstance() {
        return ofNullable(instance)
                .orElseGet(() -> {
                    instance = new FConstAllFunction();
                    return instance;
                });
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
    public Integer evaluate(Individual individual) {
        return 100;
    }

    @Override
    public Optional<Number> convertToX(long decimalValue) {
        return empty();
    }

    @Override
    public List<PopulationType> getSupportedPopulationConfigurations(OperatorType operatorType) {
        return List.of(PopulationType.RANDOM);
    }

    @Override
    public boolean isSuccessful(Map<Individual, ? extends Number> individualToFitness, OperatorType operatorType, boolean hasConverged) {
        if (operatorType == OperatorType.NONE) {
            return hasConverged;
        }

        return hasConverged && areTheSameWithPercentage(individualToFitness.keySet(), SAME_PERCENTAGE);
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public boolean supportsDecoding() {
        return false;
    }

    //    @Override
//    public List<SelectorType> getUnsupportedSelectorTypes(OperatorType operatorType) {
//        return operatorType == NONE
//                ? List.of(SUS)
//                : List.of();
//    }
}