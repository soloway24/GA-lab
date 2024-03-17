package lab.genetic_operators;

import lab.model.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crossover {

    private static final Random random = new Random();

    public static void crossover(List<Individual> population) {
        List<Individual> populationCopy = new ArrayList<>(population);
        while (populationCopy.size() > 1) {
            int index1 = random.nextInt(populationCopy.size());
            int index2 = random.nextInt(populationCopy.size());

            if (index1 != index2) {
                Individual parent1 = populationCopy.get(index1);
                Individual parent2 = populationCopy.get(index2);

                applyCrossover(parent1, parent2);

                populationCopy.remove(parent1);
                populationCopy.remove(parent2);
            }
        }
    }

    private static void applyCrossover(Individual individual1, Individual individual2) {
        String data1 = individual1.getData();
        String data2 = individual2.getData();

        int crossIndex = new Random().nextInt(data1.length());

        String data1Left = data1.substring(0, crossIndex);
        String data1Right = data1.substring(crossIndex);

        String data2Left = data2.substring(0, crossIndex);
        String data2Right = data2.substring(crossIndex);

        individual1.setData(data1Left + data2Right);
        individual2.setData(data2Left + data1Right);
    }
}
