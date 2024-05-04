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

    public static boolean isSuccessfulRealFunction(FitnessFunctionV2<? extends Number, ? extends Number> function,
                                                   Map<Individual, ? extends Number> individualToFitness,
                                                   boolean hasConverged) {
        if (!hasConverged) {
            return false;
        }

        Individual best = getBestIndividual(individualToFitness);
        double bestFitness = individualToFitness.get(best).doubleValue();
        double bestX = decodeV2(best, function).doubleValue();

        double optimalX = function.getOptimalX()
                .map(Number::doubleValue)
                .orElseThrow(() -> new IllegalStateException("Cannot get function's optimalX to verify successful run. Function = " + function));
        double maxFitness = function.getMaxFitness().doubleValue();

        double actualFitnessDelta = Math.abs(maxFitness - bestFitness);
        double actualXSigma = Math.abs(optimalX - bestX);

        return actualFitnessDelta <= ALLOWED_FITNESS_DELTA
                && actualXSigma <= ALLOWED_X_SIGMA;
    }

    public static <RES_T extends Number> Individual getBestIndividual(Map<Individual, RES_T> individualToFitness) {
        return individualToFitness.entrySet().stream()
                .map(entry -> entry(entry.getKey(), entry.getValue().doubleValue()))
                .max(Entry.comparingByValue())
                .map(Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get best individual of an empty population."));
    }
}
