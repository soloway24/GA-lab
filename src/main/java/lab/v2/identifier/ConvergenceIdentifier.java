package lab.v2.identifier;

import lab.v2.Individual;
import lab.v2.operator.OperatorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static lab.v2.operator.OperatorType.NONE;

public class ConvergenceIdentifier {

    private static final double HOMOGENOUS_PERCENTAGE = 0.99;
    private static final double ALL_THE_SAME_PERCENTAGE = 1;

    public boolean hasConverged(List<Individual> individuals, OperatorType operatorType) {
        if (operatorType == NONE) {
            return areAllTheSame(individuals);
        }
        return isHomogenous(individuals, HOMOGENOUS_PERCENTAGE);
    }

    public static boolean areAllTheSame(List<Individual> individuals) {
        return areTheSameWithPercentage(individuals, ALL_THE_SAME_PERCENTAGE);
    }

    public static boolean areTheSameWithPercentage(List<Individual> individuals, double samePercentage) {
        long maxQuantity = individuals.stream()
                .collect(groupingBy(Individual::getBinaryCode, counting()))
                .values()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);
        return (double) maxQuantity / individuals.size() >= samePercentage;
    }

    private boolean isHomogenous(List<Individual> individuals, double minPercentage) {
        Map<Integer, List<Boolean>> indexToBits = new HashMap<>();
        individuals.stream()
                .map(Individual::getBinaryCode)
                .forEach(binaryCode -> putBits(binaryCode, indexToBits));
        return isHomogenous(indexToBits, minPercentage);
    }

    private boolean isHomogenous(Map<Integer, List<Boolean>> indexToBits, double minPercentage) {
        for (Map.Entry<Integer, List<Boolean>> entry : indexToBits.entrySet()) {
            if (isNotHomogenous(entry.getValue(), minPercentage)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNotHomogenous(List<Boolean> bits, double minPercentage) {
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

    private void putBits(String binaryCode, Map<Integer, List<Boolean>> indexToBits) {
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
