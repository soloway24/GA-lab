package lab.v2.function;

import lab.parameters.Encoding;
import lab.v2.Individual;
import lab.v2.operator.OperatorType;
import lab.v2.population.PopulationType;
import lab.v2.selection.SelectorType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FitnessFunctionV2<ARG_T extends Number, RES_T extends Number> {

    String getName();

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

}