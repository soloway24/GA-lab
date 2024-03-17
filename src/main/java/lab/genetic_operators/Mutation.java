package lab.genetic_operators;

import lab.model.Individual;
import lab.parameters.FitnessFunction;
import lab.utils.GeneticUtils;

import java.util.List;
import java.util.Random;

public class Mutation {

    private static final Random random = new Random();
    /*L          N
100
200
300
400
500
1000
10
0,0001
0,0001/2
0,0001/3
0,0001/4
0,0001/5
0,00001
100
0,00001
0,00001/2
0,00001/3
0,00001/4
0,00001/5
0,000001
*/
   public static float MUTATION_PROBABILITY;

    public static void mutation(List<Individual> population){
        for (Individual individual: population) {
            applyMutation(individual);
        }
    }

    private static void applyMutation(Individual individual) {
        char[] data = individual.getData().toCharArray();
        for(int i = 0; i < data.length; i++){
            float value = random.nextFloat();
            if(value > MUTATION_PROBABILITY)
                continue;

            data[i] =  data[i] == '0' ? '1' : '0';
        }
        individual.setData(new String(data));
    }

}
