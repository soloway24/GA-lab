package lab.operator;

import lab.Individual;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static java.util.Optional.ofNullable;
import static lab.operator.OperatorType.MUTATION;
import static lab.util.CalculationUtils.getBinomialMutationCount;
import static lab.util.CalculationUtils.getIndexedIndividuals;

public class MutationOperator implements Operator {

    private static final Map<Pair<Integer, Integer>, Double> MUTATION_PROBABILITIES = Map.of(
            Pair.of(10, 100), 0.0001,
            Pair.of(100, 100), 0.00001
    );

    private final Random random = new Random();

    @Override
    public String getName() {
        return "MUTATION";
    }

    @Override
    public OperatorType getOperatorType() {
        return MUTATION;
    }

    @Override
    public List<Individual> apply(List<Individual> individuals) {
        return mutate(individuals);
    }

    private List<Individual> mutate(List<Individual> shuffledParentPool) {
        if (shuffledParentPool.isEmpty()) {
            throw new IllegalArgumentException("Cannot mutate empty parent pool.");
        }

        Individual first = shuffledParentPool.get(0);
        int chromosomeLength = first.getBinaryCode().length();

        int totalBits = shuffledParentPool.size() * chromosomeLength;

//        double probability = ofNullable(MUTATION_PROBABILITIES.get(Pair.of(chromosomeLength, shuffledParentPool.size())))
//                .orElseThrow(() -> new IllegalArgumentException("No mutation distribution config present for chromosome length: "
//                        + chromosomeLength + " and population size: " + shuffledParentPool.size()));

//        int mutationCount = getMutationCount(totalBits, probability);
        int mutationCount = getBinomialMutationCount(chromosomeLength, shuffledParentPool.size());
        return mutateWithBitCount(shuffledParentPool, chromosomeLength, totalBits, mutationCount);
    }

    private List<Individual> mutateWithBitCount(List<Individual> individuals, int chromosomeLength, int totalBits, int mutationCount) {
        if (mutationCount == 0) {
            return getIndexedIndividuals(individuals);
        }

        Set<Integer> indicesToMutate = new HashSet<>(mutationCount);

        int i = 0;
        while (i < mutationCount) {
            int index = random.nextInt(totalBits);
            if (indicesToMutate.contains(index)) {
                continue;
            }
            indicesToMutate.add(index);
            i++;
        }

        List<Individual> mutatedIndividuals = mutateByIndices(individuals, indicesToMutate, chromosomeLength);
        return getIndexedIndividuals(mutatedIndividuals);
    }

    private List<Individual> mutateByIndices(List<Individual> individuals, Set<Integer> indicesToMutate, int chromosomeLength) {
        List<Individual> individualsCopy = new ArrayList<>(individuals);
        indicesToMutate
                .forEach(index -> {
                    int individualIndex = index / chromosomeLength;
                    int bitIndex = index % chromosomeLength;

                    Individual selectedIndividual = individualsCopy.get(individualIndex);
                    Individual mutatedIndividual = mutate(selectedIndividual, bitIndex);
                    individualsCopy.set(individualIndex, mutatedIndividual);
                });
        return individualsCopy;
    }

    private Individual mutate(Individual individual, int bitIndex) {
        String binaryCode = individual.getBinaryCode();
        String newBinaryCode = mutate(binaryCode, bitIndex);
        return new Individual(individual.getIndex(), newBinaryCode, individual.getEncoding());
    }

    private String mutate(String binaryCode, int bitIndex) {
        char[] chars = binaryCode.toCharArray();
        chars[bitIndex] = mutateBit(chars[bitIndex]);
        return String.valueOf(chars);
    }

    private int getMutationCount(int totalBits, double probability) {
        int mutationCount = 0;
        for (int i = 0; i < totalBits; i++) {
            if (random.nextDouble() < probability) {
                mutationCount++;
            }
        }
        return mutationCount;
    }

    private char mutateBit(char bit) {
        return bit == '0'
                ? '1'
                : '0';
    }
}
