package lab.selection.linear;

import lab.selection.Scaler;
import lab.selection.SelectionContext;

import static lab.util.CalculationUtils.*;

public enum DynamicLinearScaler implements Scaler {

    AVERAGE {
        @Override
        public <T extends Number> double scale(T fitness, SelectionContext selectionContext) {
            return fitness.doubleValue() + getAverageFitness(selectionContext.getIndividualToFitness());
        }
    },
    MEDIAN {
        @Override
        public <T extends Number> double scale(T fitness, SelectionContext selectionContext) {
            return fitness.doubleValue() + getMedian(selectionContext.getIndividualToFitness().values());
        }
    },
    MAX_AVG_WORST {
        @Override
        public <T extends Number> double scale(T fitness, SelectionContext selectionContext) {
            double fAvg = getAverageFitness(selectionContext.getIndividualToFitness());
            double fMax = getMaxFitness(selectionContext.getIndividualToFitness());
            double fWorst = getMinDouble(getDoubleValues(selectionContext.getIndividualToFitness()));
            double ps = 1 + (fMax - fAvg) / (fAvg - fWorst);

            double b = -(fAvg * ps - fMax) / (ps - 1);
            return fitness.doubleValue() + b;
        }
    },

}