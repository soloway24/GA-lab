package lab.v2.convertor;

import lab.v2.Individual;

import java.util.Map;
import java.util.Map.Entry;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static lab.v2.util.CalculationUtils.getDoubleValues;
import static lab.v2.util.CalculationUtils.getValueSum;
import static lab.v2.convertor.ValuesAdjuster.getAdjustedIndividualToValue;

public class ProbabilityToExpectedQuantityConvertor {

    private static final double ALLOWED_ERROR = 0.00000000001;
    private static final String ERROR_PREFIX = "Probability to expected quantity calculation failed: ";

    public Map<Individual, Double> convertToExpectedQuantities(Map<Individual, Double> individualToProbability) {
        int populationSize = individualToProbability.size();
        Map<Individual, Double> individualToExpectedQuantity = getIndividualToExpectedQuantity(individualToProbability);

        double expectedQuantitySum = getValueSum(getDoubleValues(individualToExpectedQuantity));
        verifyExpectedQuantitySum(expectedQuantitySum, populationSize);

        return getAdjustedIndividualToValue(individualToExpectedQuantity, expectedQuantitySum, populationSize);
    }

    private Map<Individual, Double> getIndividualToExpectedQuantity(Map<Individual, Double> individualToProbability) {
        return individualToProbability.entrySet().stream()
                .collect(toUnmodifiableMap(Entry::getKey, entry -> entry.getValue() * individualToProbability.size()));
    }

    private void verifyExpectedQuantitySum(double expectedQuantitySum, int populationSize) {
        if (abs(expectedQuantitySum - populationSize) > ALLOWED_ERROR) {
            throw new IllegalStateException(ERROR_PREFIX + "Expected quantity sum " + expectedQuantitySum
                    + " is not equal to population size " + populationSize + " during fitness to percentage conversion !");
        }
    }
}

