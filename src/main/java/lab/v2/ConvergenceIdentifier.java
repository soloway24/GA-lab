package lab.v2;

import lab.v2.operator.OperatorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static lab.v2.operator.OperatorType.NONE;

public class ConvergenceIdentifier {

    private static final double HOMOGENOUS_PERCENTAGE = 0.99;

    public boolean hasConverged(List<Individual> individuals, OperatorType operatorType) {
        if (operatorType == NONE) {
            return areAllTheSame(individuals);
        }
        return isHomogenous(individuals, HOMOGENOUS_PERCENTAGE);
    }

    private boolean areAllTheSame(List<Individual> individuals) {
        Individual first = individuals.get(0);
        int sameQuantity = individuals.stream()
                .filter(individual -> individual.getBinaryCode().equals(first.getBinaryCode()))
                .toList()
                .size();
        return sameQuantity == individuals.size();
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
