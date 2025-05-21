package lab.operator;

import lab.Individual;
import lab.encoding.Encoding;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.Collections.shuffle;
import static lab.operator.OperatorType.CROSSOVER;
import static lab.util.CalculationUtils.getIndexedIndividuals;

public class OnePointCrossoverOperator implements Operator {

    private final Random random = new Random();
    private final double probability;

    public OnePointCrossoverOperator(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return "CROSSOVER";
    }

    @Override
    public OperatorType getOperatorType() {
        return CROSSOVER;
    }

    @Override
    public List<Individual> apply(List<Individual> individuals) {
        return crossover(individuals);
    }

    private List<Individual> crossover(List<Individual> parentPool) {
        if (parentPool.size() % 2 != 0) {
            throw new IllegalArgumentException("Cannot split parent pool of length " + parentPool.size() + "by pairs of two during OP crossover.");
        }

        List<Individual> parentPoolShuffled = new ArrayList<>(parentPool);
        shuffle(parentPoolShuffled);

        List<Pair<Individual, Individual>> pairs = IntStream
                .iterate(0, i -> i < parentPoolShuffled.size(), i -> i + 2)
                .mapToObj(i -> Pair.of(parentPoolShuffled.get(i), parentPoolShuffled.get(i + 1)))
                .toList();

        List<Individual> crossedIndividuals = new ArrayList<>(
                pairs.stream()
                        .flatMap(pair -> crossover(pair.getLeft(), pair.getRight()).stream())
                        .toList()
        );

        shuffle(crossedIndividuals);
        return getIndexedIndividuals(crossedIndividuals);
    }

    private List<Individual> crossover(Individual individual1, Individual individual2) {
        String chromosome1 = individual1.getBinaryCode();
        String chromosome2 = individual2.getBinaryCode();

        double crossoverSpin = random.nextDouble();
        if (chromosome1.equals(chromosome2) || crossoverSpin >= probability) {
            return List.of(individual1, individual2);
        }

        if (chromosome1.length() != chromosome2.length()) {
            throw new IllegalStateException("Trying to crossover chromosomes of different lengths: 1 = " + individual1
                    + ", 2 = " + individual2 + ".");
        }
        if (individual1.getEncoding() != individual2.getEncoding()) {
            throw new IllegalStateException("Trying to crossover individuals of different encodings: 1 = " + individual1
                    + ", 2 = " + individual2 + ".");
        }
        Encoding encoding = individual1.getEncoding();

        int length = chromosome1.length();

        StringBuilder offspring1 = new StringBuilder(length);
        StringBuilder offspring2 = new StringBuilder(length);

        int splitIndex = random.nextInt(1, length - 1);

        for (int i = 0; i < length; i++) {
            if (i < splitIndex) {
                offspring1.append(chromosome1.charAt(i));
                offspring2.append(chromosome2.charAt(i));
            } else {
                offspring1.append(chromosome2.charAt(i));
                offspring2.append(chromosome1.charAt(i));
            }
        }

        return List.of(
                new Individual(offspring1.toString(), encoding),
                new Individual(offspring2.toString(), encoding)
        );
    }
}
