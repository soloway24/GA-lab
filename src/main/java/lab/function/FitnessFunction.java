package lab.function;

import lab.Individual;
import lab.encoding.Encoding;
import lab.operator.OperatorType;
import lab.population.PopulationType;
import lab.selection.SelectorType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

public interface FitnessFunction<ARG_T extends Number, RES_T extends Number> {

    String getName();

    default int getArity() {
        return 1;
    }

    int getChromosomeLength();

    List<Encoding> getSupportedEncodings();

    RES_T getMinFitness();

    RES_T getMaxFitness();

    Optional<ARG_T> getMinX();

    Optional<ARG_T> getMaxX();

    Optional<ARG_T> getOptimalX();

    Optional<Individual> getOptimalIndividual(Encoding encoding);

    RES_T evaluate(Individual individual);

    Optional<ARG_T> convertToX(long decimalValue);

    default boolean supportsDecoding() {
        return true;
    }

    List<PopulationType> getSupportedPopulationConfigurations(OperatorType operatorType);

    boolean isSuccessful(Map<Individual, ? extends Number> individualToFitness, OperatorType operatorType, boolean hasConverged);

    default List<SelectorType> getUnsupportedSelectorTypes(OperatorType operatorType) {
        return List.of();
    }

    default boolean isConstant() {
        return false;
    }

    default Optional<Integer> getCustomRunPoolSize(SelectorType selectorType, OperatorType operatorType) {
        return empty();
    }

}