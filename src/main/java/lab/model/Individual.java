package lab.model;

import lab.parameters.FitnessFunction;
import lab.utils.GeneticUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Random;

@Getter
@Setter
public class Individual {

    private int index;
    private String data;

    public Individual() {
    }
    public Individual(String data) {
        this.data = data;
    }

    public void fillRandomly(int L) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < L; i++) {
            sb.append(random.nextDouble() < 0.5 ? 1 : 0);
        }
        data = sb.toString();
    }

    public void fillWithChar(int L, char ch) {
        data = String.valueOf(ch).repeat(Math.max(0, L));
    }

    public double getHealth(FitnessFunction function) {
        double x = 0;
        switch (function) {
            case F_ALL_CONST -> x = GeneticUtils.FconstALL(this);
            case FHD -> x = GeneticUtils.FHD(this);
            case F1 -> x = GeneticUtils.F1(this);
            case F2 -> x = GeneticUtils.F2(this);
        }
        return Math.round(x * 10000.0) / 10000.0;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public Individual clone() {
        Individual clone = new Individual();
        clone.setData(data);
        clone.setIndex(index);
        return clone;
    }

    public int getOnes() {

        return Arrays.stream(data.split("")).mapToInt(Integer::parseInt).sum();
    }
}