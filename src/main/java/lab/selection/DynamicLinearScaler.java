package lab.selection;

import lab.Individual;

import java.util.Map;

import static lab.util.CalculationUtils.*;

public enum DynamicLinearScaler implements Scaler {

    AVERAGE {
        @Override
        public <T extends Number> double scale(T fitness, Map<Individual, T> individualToFitness) {
            return fitness.doubleValue() + getAverageFitness(individualToFitness);
        }
    },
    MEDIAN {
        @Override
        public <T extends Number> double scale(T fitness, Map<Individual, T> individualToFitness) {
            return fitness.doubleValue() + getMedian(individualToFitness.values());
        }
    },
    MAX_AVG_WORST {
        @Override
        public <T extends Number> double scale(T fitness, Map<Individual, T> individualToFitness) {
            double fAvg = getAverageFitness(individualToFitness);
            double fMax = getMaxFitness(individualToFitness);
            double fWorst = getMinDouble(getDoubleValues(individualToFitness));
            double ps = 1 + (fMax - fAvg) / (fAvg - fWorst);

            double b = -(fAvg * ps - fMax) / (ps - 1);
            return fitness.doubleValue() + b;
        }
    },

}