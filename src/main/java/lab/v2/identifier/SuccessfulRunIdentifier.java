package lab.v2.identifier;

import lab.v2.Individual;
import lab.v2.function.FitnessFunctionV2;

import java.util.Map;
import java.util.Map.Entry;

import static java.util.Map.entry;
import static lab.v2.Constants.ALLOWED_FITNESS_DELTA;
import static lab.v2.Constants.ALLOWED_X_SIGMA;
import static lab.v2.encoding.DecoderV2.decodeV2;

public class SuccessfulRunIdentifier {

    public static <ARG_T extends Number, RES_T extends Number> boolean isSuccessfulRealFunction(FitnessFunctionV2<ARG_T, RES_T> function,
                                                                                                Map<Individual, RES_T> individualToFitness,
                                                                                                boolean hasConverged) {
        if (!hasConverged) {
            return false;
        }

        Individual best = getBestIndividual(individualToFitness);
        double bestFitness = individualToFitness.get(best).doubleValue();
        double bestX = decodeV2(best, function).doubleValue();

        double maxX = function.getMaxX()
                .map(Number::doubleValue)
                .orElseThrow(() -> new IllegalStateException("Cannot get function's maxX to verify successful run. Function = " + function));
        double maxFitness = function.getMaxFitness().doubleValue();

        double actualFitnessDelta = Math.abs(maxFitness - bestFitness);
        double actualXSigma = Math.abs(maxX - bestX);

        return actualFitnessDelta <= ALLOWED_FITNESS_DELTA
                && actualXSigma <= ALLOWED_X_SIGMA;
    }

    private static <RES_T extends Number> Individual getBestIndividual(Map<Individual, RES_T> individualToFitness) {
        return individualToFitness.entrySet().stream()
                .map(entry -> entry(entry.getKey(), entry.getValue().doubleValue()))
                .max(Entry.comparingByValue())
                .map(Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get best individual of an empty population."));
    }
}
