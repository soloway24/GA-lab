package lab.v2.function;

import lab.parameters.Encoding;
import lab.v2.Individual;
import lab.v2.operator.OperatorType;
import lab.v2.population.PopulationType;

import java.util.List;
import java.util.Optional;

public interface FitnessFunctionV2<ARG_T extends Number, RES_T extends Number> {

    String getName();

    int getChromosomeLength();

    List<Encoding> getSupportedEncodings();

    RES_T getMinFitness();

    RES_T getMaxFitness();

    Optional<ARG_T> getMinX();

    Optional<ARG_T> getMaxX();

    Optional<Individual> getOptimalIndividual(Encoding encoding);

    RES_T evaluate(ARG_T x);

    Optional<ARG_T> convertToX(long decimalValue);

    List<PopulationType> getSupportedPopulationConfigurations(OperatorType operatorType);

}