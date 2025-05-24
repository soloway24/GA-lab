package lab.convertor;

import lab.Individual;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Map.Entry;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static lab.util.CalculationUtils.getDoubleValues;
import static lab.util.CalculationUtils.getValueSum;

@Component
public class FitnessToProbabilityConvertor {

    private static final double VALID_PROBABILITY_SUM = 1;
    private static final double ALLOWED_ERROR = 0.00000000001;
    private static final String ERROR_PREFIX = "Fitness to probability calculation failed: ";

    public Map<Individual, Double> convertToSelectionProbabilities(Map<Individual, ? extends Number> individualToFitness) {
        double fitnessSum = getValueSum(getDoubleValues(individualToFitness));
        verifyFitnessSum(fitnessSum, individualToFitness);

        return getIndividualToProbability(individualToFitness, fitnessSum);
    }

    private Map<Individual, Double> getIndividualToProbability(Map<Individual, ? extends Number> individualToFitness,
                                                               double fitnessSum) {
        Map<Individual, Double> individualToProbability = individualToFitness.entrySet().stream()
                .collect(toUnmodifiableMap(Entry::getKey, entry -> getProbability(entry, fitnessSum)));

        double probabilitySum = getValueSum(getDoubleValues(individualToProbability));
        verifyProbabilitySum(probabilitySum);

        return individualToProbability;
    }

    private double getProbability(Entry<Individual, ? extends Number> individual, double fitnessSum) {
        double probability = individual.getValue().doubleValue() / fitnessSum;
        if (probability < 0) {
            throw new IllegalStateException(ERROR_PREFIX + "Probability of individual " + individual.getKey()
                    + " is negative ! Fitness = " + individual.getValue() + ", sum = " + fitnessSum + ".");
        }
        return probability;
    }

    private void verifyProbabilitySum(double probabilitySum) {
        if (abs(VALID_PROBABILITY_SUM - probabilitySum) > ALLOWED_ERROR) {
            throw new IllegalStateException(ERROR_PREFIX + "Probabilities sum " + probabilitySum + " is not equal to "
                    + VALID_PROBABILITY_SUM + " during fitness to percentage conversion !");
        }
    }

    private void verifyFitnessSum(double sum, Map<Individual, ? extends Number> individualToFitness) {
        if (sum <= 0) {
            throw new IllegalStateException(ERROR_PREFIX + " Fitness sum should be greater than zero ! " +
                    "Individuals with fitness = " + individualToFitness + ", sum = " + sum + " .");
        }
    }
}

