package lab.v2.operator;

import lab.model.Individual;

import java.util.List;

public class NoneOperator implements Operator {

    @Override
    public String getName() {
        return "NONE";
    }

    @Override
    public List<Individual> apply(List<Individual> individuals) {
        return individuals;
    }

}
