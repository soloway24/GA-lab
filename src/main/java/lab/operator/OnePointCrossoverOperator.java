package lab.operator;

import lab.Individual;
import lab.encoding.Encoding;

import java.util.*;

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

        List<Individual> crossedIndividuals = new ArrayList<>(parentPool.size());
        for (int i = 0; i < parentPoolShuffled.size(); i += 2) {
            Individual individual1 = parentPoolShuffled.get(i);
            Individual individual2 = parentPoolShuffled.get(i + 1);
            crossedIndividuals.addAll(crossover(individual1, individual2));
        }

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
        int splitIndex = random.nextInt(1, length);

        char[] offspring1 = chromosome1.toCharArray();
        char[] offspring2 = chromosome2.toCharArray();

        for (int j = splitIndex; j < length; j++) {
            char temp = offspring1[j];
            offspring1[j] = offspring2[j];
            offspring2[j] = temp;
        }

        return List.of(
                new Individual(new String(offspring1), encoding),
                new Individual(new String(offspring2), encoding)
        );
    }

    public static void main(String[] args) {
        OnePointCrossoverOperator operator = new OnePointCrossoverOperator(1);

        List<Individual> individuals = List.of(new Individual("1111111111", Encoding.STANDARD), new Individual("0000000000", Encoding.STANDARD));
        Map<List<String>, Integer> resultToCount = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            List<String> crossed = operator.apply(individuals).stream().map(Individual::getBinaryCode).toList();
            if (resultToCount.containsKey(crossed)) {
                resultToCount.put(crossed, resultToCount.get(crossed) + 1);
                continue;
            }
            resultToCount.put(crossed, 1);
        }
        resultToCount.forEach((k, v) -> System.out.println(k + " = " + v));
    }
}
