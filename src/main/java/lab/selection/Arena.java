package lab.selection;

import lab.model.Individual;
import lab.parameters.ContestType;
import lab.parameters.FitnessFunction;
import lab.utils.GeneticUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Arena {

    private static final Random random = new Random();

    public static void generateParents(List<Individual> population, List<Individual> parents, ContestType contestType, FitnessFunction function) {
        switch (contestType) {
            case UNIQUE_ENTRY -> uniqueEntryContest(population, parents, function);
            case MULTIPLE_ENTRY -> multipleEntryContest(population, parents, function);
        }
    }

    private static void multipleEntryContest(List<Individual> population, List<Individual> parents, FitnessFunction function) {
        while (parents.size() != population.size()) {
            int index1 = random.nextInt(population.size());
            int index2 = random.nextInt(population.size());
            if (index1 != index2) {
                parents.add(getWinner(population.get(index1), population.get(index2), function).clone());
            }
        }
    }

    private static void uniqueEntryContest(List<Individual> population, List<Individual> parents, FitnessFunction function) {
        List<Individual> populationCopy = new ArrayList<>(population);

        while (population.size() != parents.size()) {
            int index1 = random.nextInt(populationCopy.size());
            int index2 = random.nextInt(populationCopy.size());
            if (index1 != index2) {
                Individual contestant1 = populationCopy.get(index1);
                Individual contestant2 = populationCopy.get(index2);

                Individual winner = getWinner(contestant1, contestant2, function);
                parents.add(winner.clone());

                populationCopy.remove(contestant1);
                populationCopy.remove(contestant2);
            }

            if (populationCopy.size() < 2) {
                ArrayList<Individual> individuals = new ArrayList<>(population);
                individuals.addAll(populationCopy);
                populationCopy = individuals;
            }
        }
    }


    private static Individual getWinner(Individual contestant1, Individual contestant2, FitnessFunction function) {
        double val1 = contestant1.getHealth(function);
        double val2 = contestant2.getHealth(function);

        Individual winner, looser;
        if (val1 > val2) {
            winner = contestant1;
            looser = contestant2;
        } else {
            winner = contestant2;
            looser = contestant1;
        }

        float winnerValue = random.nextFloat();
        return winnerValue > GeneticUtils.P_SWAP ? looser : winner;
    }
}
