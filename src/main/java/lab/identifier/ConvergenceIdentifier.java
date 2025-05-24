package lab.identifier;

import lab.Individual;
import lab.operator.OperatorType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static lab.util.CalculationUtils.*;

@Component
public class ConvergenceIdentifier {

    private static final double HOMOGENOUS_PERCENTAGE = 0.99;
    private static final int PREV_AVG_F_SIZE = 10;
    public static final double STOPPED_EVOLVING_EPSILON = 0.0001;

    public boolean hasConverged(Map<Individual, ? extends Number> individualToFitness,
                                Deque<Double> prevAvgFs,
                                OperatorType operatorType) {
        if (operatorType == OperatorType.NONE) {
            return areAllTheSame(individualToFitness.keySet())
                    // only for Rastrigin without operators
//                    || areAllTheSameDoubles(individualToFitness.values())
                    ;
        }
        return isHomogenous(individualToFitness.keySet(), HOMOGENOUS_PERCENTAGE)
                || stoppedEvolving(individualToFitness, prevAvgFs);
    }

    private boolean stoppedEvolving(Map<Individual, ? extends Number> individualToFitness,
                                    Deque<Double> prevAvgFs) {
        double avgF = getAverage(individualToFitness.values());
        if (prevAvgFs.size() == PREV_AVG_F_SIZE) {
            prevAvgFs.removeFirst();
            prevAvgFs.addLast(avgF);
        } else {
            prevAvgFs.addLast(avgF);
        }

        if (prevAvgFs.size() < PREV_AVG_F_SIZE) {
            return false;
        }

        double minF = getMinDouble(prevAvgFs);
        double maxF = getMaxDouble(prevAvgFs);
        if (maxF - minF <= STOPPED_EVOLVING_EPSILON) {
            System.out.println("Stopped Evolving");
            return true;
        }
        return false;
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
        Map<Integer, List<Boolean>> indexToBits = new ConcurrentHashMap<>();
        individuals.stream()
                .map(Individual::getBinaryCode)
                .forEach(binaryCode -> putBits(binaryCode, indexToBits));
        return isHomogenous(indexToBits, minPercentage);
    }

    public static boolean isAtLeastOneAlleleHomogenous(Collection<Individual> individuals) {
        Map<Integer, List<Boolean>> indexToBits = new ConcurrentHashMap<>();
        individuals.stream()
                .map(Individual::getBinaryCode)
                .forEach(binaryCode -> putBits(binaryCode, indexToBits));
        return isAtLeastOneAlleleHomogenous(indexToBits);
    }

    private static boolean isHomogenous(Map<Integer, List<Boolean>> indexToBits, double minPercentage) {
        for (Map.Entry<Integer, List<Boolean>> entry : indexToBits.entrySet()) {
            if (isNotHomogenous(entry.getValue(), minPercentage)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAtLeastOneAlleleHomogenous(Map<Integer, List<Boolean>> indexToBits) {
        for (Map.Entry<Integer, List<Boolean>> entry : indexToBits.entrySet()) {
            if (isHomogenous(entry.getValue())) {
                return true;
            }
        }
        return false;
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

    private static boolean isHomogenous(List<Boolean> bits) {
        return bits.stream()
                .distinct()
                .count() == 1;
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
