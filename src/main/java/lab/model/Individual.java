package lab.model;

import lab.function.FitnessFunction;
import lab.parameters.Encoding;
import lab.utils.GeneticUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Random;

@Getter
@Setter
public class Individual {

    public static final Individual ALL_100_ZEROS_INDIVIDUAL = new Individual("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

    private static final Random RANDOM = new Random();

    private int index;
    private String binaryCode;
    private Encoding encoding;

    public Individual() {
    }

    public Individual(String binaryCode) {
        this.binaryCode = binaryCode;
    }

    public Individual(String binaryCode, Encoding encoding) {
        this.binaryCode = binaryCode;
        this.encoding = encoding;
    }

    public static Individual createRandomIndividual(int length, Encoding encoding) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextBoolean() ? "1" : "0");
        }
        return new Individual(sb.toString(), encoding);
    }

    public void fillRandomly(int L) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < L; i++) {
            sb.append(random.nextDouble() < 0.5 ? 1 : 0);
        }
        binaryCode = sb.toString();
    }

    public void fillWithChar(int L, char ch) {
        binaryCode = String.valueOf(ch).repeat(Math.max(0, L));
    }

    public double getHealth(FitnessFunction function) {
        double x = 0;
        switch (function) {
            case F_ALL_CONST -> x = GeneticUtils.FconstALL(this);
            case FHD -> x = GeneticUtils.FHD(this);
            case QUAD -> x = GeneticUtils.F1(this);
            case QUAD_SYM -> x = GeneticUtils.F2(this);
        }
        return Math.round(x * 10000.0) / 10000.0;
    }

    @Override
    public String toString() {
        return binaryCode;
    }

    @Override
    public Individual clone() {
        Individual clone = new Individual();
        clone.setBinaryCode(binaryCode);
        clone.setIndex(index);
        return clone;
    }

    public int getOnes() {

        return Arrays.stream(binaryCode.split("")).mapToInt(Integer::parseInt).sum();
    }
}