package lab.v2.selection;

import lab.model.Individual;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableMap;

public class FitnessToPercentageConvertor<T extends Number> {

    private static final double VALID_PERCENTAGE_SUM = 1;
    private static final String ERROR_PREFIX = "Fitness to percentage calculation failed:";

    public Map<Individual, Double> convertToSelectionPercentages(Map<Individual, T> individualToFitness) {
        double fitnessSum = getValueSum(getDoubleValues(individualToFitness));
        return getIndividualToPercentage(individualToFitness, fitnessSum);
    }

    private List<Double> getDoubleValues(Map<Individual, ? extends Number> individualToValue) {
        return individualToValue.values().stream()
                .map(Number::doubleValue)
                .toList();
    }

    private double getValueSum(List<Double> values) {
        double sum = values.stream()
                .reduce(Double::sum)
                .orElseThrow(() -> new IllegalStateException(ERROR_PREFIX + " provided individuals list is empty! Individuals = "
                        + values + " ."));
        verifyFitnessSum(sum, values);
        return sum;
    }

    private Map<Individual, Double> getIndividualToPercentage(Map<Individual, T> individualToFitness, double fitnessSum) {
        Map<Individual, Double> individualToPercentage = individualToFitness.entrySet().stream()
                .collect(toUnmodifiableMap(Map.Entry::getKey, entry -> getPercentage(entry, fitnessSum)));
        verifyPercentageSum(individualToPercentage);
        return individualToPercentage;
    }

    private double getPercentage(Map.Entry<Individual, T> individual, double fitnessSum) {
        return individual.getValue().doubleValue() / fitnessSum;
    }

    private void verifyPercentageSum(Map<Individual, Double> individualToPercentage) {
        double percentageSum = getValueSum(getDoubleValues(individualToPercentage));
        if (percentageSum != VALID_PERCENTAGE_SUM) {
            throw new IllegalStateException("Percentage sum " + percentageSum + " is not equal to " + VALID_PERCENTAGE_SUM
                    + " during fitness to percentage conversion !");
        }
    }

    private void verifyFitnessSum(double sum, List<Double> values) {
        if (sum <= 0) {
            throw new IllegalStateException(ERROR_PREFIX + " fitness sum should be greater than zero ! Individuals = " + values
                    + ", sum = " + sum + " .");
        }
    }
}

