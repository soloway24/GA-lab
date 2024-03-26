package lab.v2.operator;

import lab.model.Individual;

import java.util.List;
import java.util.Random;

import static lab.v2.operator.OperatorType.CROSSOVER;

public class OnePointCrossoverOperator implements Operator {

    private final double probability;

    public OnePointCrossoverOperator(double probability) {
        this.probability = probability;
    }

    @Override
    public String getName() {
        return "OP Crossover";
    }

    @Override
    public OperatorType getOperatorType() {
        return CROSSOVER;
    }

    @Override
    public List<Individual> apply(List<Individual> individuals) {
        return individuals;
    }

}
