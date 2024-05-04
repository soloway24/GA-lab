package lab.v2.function;

import lab.parameters.Encoding;
import lab.v2.Individual;
import lab.v2.operator.OperatorType;
import lab.v2.population.PopulationType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static lab.parameters.Encoding.STANDARD;
import static lab.v2.identifier.ConvergenceIdentifier.areTheSameWithPercentage;
import static lab.v2.operator.OperatorType.NONE;
import static lab.v2.population.PopulationType.RANDOM;

public class FConstAllFunction implements FitnessFunctionV2<Number, Integer> {

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
        return List.of(RANDOM);
    }

    @Override
    public boolean isSuccessful(Map<Individual, ? extends Number> individualToFitness, OperatorType operatorType, boolean hasConverged) {
        if (operatorType == NONE) {
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