package lab.operator;

import lab.Individual;
import lab.encoding.Encoding;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

class MutationOperatorTest {

    private final MutationOperator operator = new MutationOperator();

    @Test
    public void mutate() {
        List<Individual> individuals = IntStream.range(0, 100)
                .mapToObj(i -> Individual.createRandomIndividual(10, 1, Encoding.STANDARD))
                .toList();

        System.out.println("Initial population: " + individuals);
        List<Individual> mutatedIndividuals = operator.apply(individuals);
        System.out.println("Mutated population: " + mutatedIndividuals);
        List<String> initialCodes = individuals.stream()
                .map(Individual::getBinaryCode)
                .toList();
        List<String> mutatedCodes = mutatedIndividuals.stream()
                .map(Individual::getBinaryCode)
                .toList();
        System.out.println(initialCodes.equals(mutatedCodes));

        int i = 1;
        while (initialCodes.equals(mutatedCodes)) {
            mutatedIndividuals = operator.apply(individuals);
            System.out.println("Mutated population: " + mutatedIndividuals);

            mutatedCodes = mutatedIndividuals.stream()
                    .map(Individual::getBinaryCode)
                    .toList();
            System.out.println(initialCodes.equals(mutatedCodes));
            i++;
        }
        System.out.println("i = " + i);
    }
}