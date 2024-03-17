package lab.stop_conditions;

import lab.model.Individual;
import lab.parameters.GeneticOperatorsApplication;

import java.util.List;

public class StopCondition {

    private static float HOMOGEN_PERCENTAGE;

    public static boolean shouldStop(List<Individual> population, GeneticOperatorsApplication appliedOperators) {
        if (appliedOperators == GeneticOperatorsApplication.MUTATION || appliedOperators == GeneticOperatorsApplication.CROSSOVER_MUTATION) {
            HOMOGEN_PERCENTAGE = 0.99f;
        } else {
            HOMOGEN_PERCENTAGE = 1f;
        }

        return isHomogenPopulation(population);
    }

    private static boolean isHomogenPopulation(List<Individual> population) {
        int l = population.get(0).getData().length();
        float deviation = population.size() - population.size() * HOMOGEN_PERCENTAGE;
        for (int i = 0; i < l; i++) {
            float total = 0;
            for (int j = 0; j < population.size(); j++) {
                total += population.get(j).getData().charAt(i) - 48;
            }

            float averageValue = total;
            if (averageValue > deviation && averageValue < population.size() - deviation)
                return false;
        }
        return true;
    }
}
