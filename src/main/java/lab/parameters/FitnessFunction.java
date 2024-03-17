package lab.parameters;

import lab.model.Individual;
import lab.utils.GeneticUtils;
import lombok.Getter;

@Getter
public enum FitnessFunction {

    F2(10, '0', "5.12^2 - x^2", 0, 26.2144, -5.12, 5.12, new Individual("1000000000"), new Individual("1100000000"), true),
    F1(10, '1', "x^2", 0, 104.6529, 0, 10.23, new Individual("1111111111"), new Individual("1000000000"), true),
    FHD(100, '0', "FHD", 0, 10000, 0, 100, new Individual("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"), null, false),
    F_ALL_CONST(100, '0', "FConst", 0, 100, 0, 100, new Individual("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"), null, true);

    private final int length;
    private final char optimal;
    private final String outPath;
    private final double min;
    private final double max;
    private final double minX;
    private final double maxX;
    private final Individual optimalStandard;
    private final Individual optimalGray;
    private final boolean applyOperators;

    FitnessFunction(int length, char optimal, String out, double min, double max, double minX, double maxX, Individual optimalStandard, Individual optimalGray, boolean applyOperators) {
        this.length = length;
        this.optimal = optimal;
        this.outPath = out;
        this.min = min;
        this.max = max;

        this.minX = minX;
        this.maxX = maxX;

        this.optimalStandard = optimalStandard;
        this.optimalGray = optimalGray;

        this.applyOperators = applyOperators;
    }

    public Individual getBest() {
        if (optimalGray == null)
            return optimalStandard;
        return GeneticUtils.ENCODING == Encoding.STANDARD ? optimalStandard : optimalGray;
    }
}
