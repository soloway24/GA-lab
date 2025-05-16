package lab.identifier;

import lab.Individual;
import lab.function.FitnessFunction;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.lang.Math.*;
import static java.util.Map.entry;
import static lab.Constants.ALLOWED_FITNESS_DELTA;
import static lab.Constants.ALLOWED_X_SIGMA;
import static lab.encoding.Decoder.decode;
import static lab.encoding.Decoder.decodeMultipleArguments;
import static lab.util.CalculationUtils.getDoubleValues;

public class SuccessfulRunIdentifier {

    public static boolean isSuccessfulRealFunction(FitnessFunction<? extends Number, ? extends Number> function,
                                                   Map<Individual, ? extends Number> individualToFitness,
                                                   boolean hasConverged) {
        if (!hasConverged) {
            return false;
        }

        Individual best = getBestIndividual(individualToFitness);
        double bestFitness = individualToFitness.get(best).doubleValue();
        double bestX = decode(best, function).doubleValue();
        List<Double> bestXs = function.getArity() == 1
                ? List.of()
                : getDoubleValues(decodeMultipleArguments(best, function));

        double optimalX = function.getOptimalX()
                .map(Number::doubleValue)
                .orElseThrow(() -> new IllegalStateException("Cannot get function's optimalX to verify successful run. Function = " + function));
        double maxFitness = function.getMaxFitness().doubleValue();

        double actualFitnessDelta = maxFitness - bestFitness;

        boolean xSigmaAllowed = function.getArity() == 1
                ? abs(optimalX - bestX) <= ALLOWED_X_SIGMA
                : getEuclidianDistance(bestXs, optimalX) <= ALLOWED_X_SIGMA;

        return actualFitnessDelta <= ALLOWED_FITNESS_DELTA && xSigmaAllowed;
    }

    public static <RES_T extends Number> Individual getBestIndividual(Map<Individual, RES_T> individualToFitness) {
        return individualToFitness.entrySet().stream()
                .map(entry -> entry(entry.getKey(), entry.getValue().doubleValue()))
                .max(Entry.comparingByValue())
                .map(Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("Cannot get best individual of an empty population."));
    }

    private static double getEuclidianDistance(List<Double> bestXs, double optimalX) {
        return sqrt(bestXs.stream()
                .mapToDouble(bestX -> pow(optimalX - bestX, 2))
                .sum());
    }
}
