package lab.function;

import lab.model.Individual;
import lab.parameters.Encoding;

import java.util.List;
import java.util.Optional;

public interface FitnessFunctionV2<ARG_T, RES_T extends Number> {

    String getName();

    int getChromosomeLength();

    List<Encoding> getEncodings();

    RES_T getMinFitness();

    RES_T getMaxFitness();

    Optional<ARG_T> getMinX();

    Optional<ARG_T> getMaxX();

    Individual getOptimalIndividual(Encoding encoding);

    RES_T evaluate(ARG_T x);

    Optional<ARG_T> convertToX(long decimalValue);

}