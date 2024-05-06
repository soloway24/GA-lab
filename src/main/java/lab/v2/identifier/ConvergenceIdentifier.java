package lab.v2.identifier;

import lab.v2.Individual;
import lab.v2.operator.OperatorType;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static lab.v2.operator.OperatorType.NONE;

public class ConvergenceIdentifier {

    private static final double HOMOGENOUS_PERCENTAGE = 0.99;

    public boolean hasConverged(Map<Individual, ? extends Number> individualToFitness, OperatorType operatorType) {
        if (operatorType == NONE) {
            return areAllTheSame(individualToFitness.keySet())
                    // only for Rastrigin
//                    || areAllTheSameDoubles(individualToFitness.values())
                    ;

        }
        return isHomogenous(individualToFitness.keySet(), HOMOGENOUS_PERCENTAGE);
    }

    private boolean areAllTheSameDoubles(Collection<? extends Number> values) {
        long distinct = values.stream()
                .distinct()
                .count();
        return distinct == 1;
    }

    public static boolean areTheSameWithPercentage(Collection<Individual> individuals, double samePercentage) {
        long maxQuantity = individuals.stream()
                .collect(groupingBy(Individual::getBinaryCode, counting()))
                .values()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);
        return (double) maxQuantity / individuals.size() >= samePercentage;
    }

    public static boolean areEqualToWithPercentage(Collection<Individual> individuals, Individual compared, double equalPercentage) {
        int equalQuantity = getEqualQuantity(individuals, compared);
        return (double) equalQuantity / individuals.size() >= equalPercentage;
    }

    public static boolean areAllTheSame(Collection<Individual> individuals) {
        if (individuals.isEmpty()) {
            throw new IllegalStateException("Trying to decide areAllTheSame for an empty list if individuals.");
        }
        Individual first = individuals.iterator().next();
        return areAllEqualTo(individuals, first);
    }

    public static boolean areAllEqualTo(Collection<Individual> individuals, Individual compared) {
        int equalQuantity = getEqualQuantity(individuals, compared);
        return equalQuantity == individuals.size();
    }

    public static int getEqualQuantity(Collection<Individual> individuals, Individual compared) {
        return individuals.stream()
                .filter(individual -> individual.getBinaryCode().equals(compared.getBinaryCode()))
                .toList()
                .size();
    }

    public static boolean isHomogenous(Collection<Individual> individuals, double minPercentage) {
        Map<Integer, List<Boolean>> indexToBits = new HashMap<>();
        individuals.stream()
                .map(Individual::getBinaryCode)
                .forEach(binaryCode -> putBits(binaryCode, indexToBits));
        return isHomogenous(indexToBits, minPercentage);
    }

    private static boolean isHomogenous(Map<Integer, List<Boolean>> indexToBits, double minPercentage) {
        for (Map.Entry<Integer, List<Boolean>> entry : indexToBits.entrySet()) {
            if (isNotHomogenous(entry.getValue(), minPercentage)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNotHomogenous(List<Boolean> bits, double minPercentage) {
        int oneCount = bits.stream()
                .filter(bool -> bool)
                .toList()
                .size();
        int zeroCount = bits.stream()
                .filter(bool -> !bool)
                .toList()
                .size();
        double onePercentage = (double) oneCount / bits.size();
        double zeroPercentage = (double) zeroCount / bits.size();

        return onePercentage < minPercentage && zeroPercentage < minPercentage;
    }

    private static void putBits(String binaryCode, Map<Integer, List<Boolean>> indexToBits) {
        String[] chars = binaryCode.split("");
        IntStream.range(0, chars.length)
                .forEach(i -> {
                    boolean bit = chars[i].equals("1");
                    List<Boolean> currentIndexBits = indexToBits.getOrDefault(i, new ArrayList<>());
                    currentIndexBits.add(bit);
                    indexToBits.put(i, currentIndexBits);
                });
    }
}
