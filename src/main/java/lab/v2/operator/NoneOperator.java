package lab.v2.operator;

import lab.v2.Individual;

import java.util.List;

import static lab.v2.operator.OperatorType.NONE;
import static lab.v2.util.CalculationUtils.getIndexedIndividuals;

public class NoneOperator implements Operator {

    @Override
    public String getName() {
        return "NONE";
    }

    @Override
    public OperatorType getOperatorType() {
        return NONE;
    }

    @Override
    public List<Individual> apply(List<Individual> individuals) {
        return getIndexedIndividuals(individuals);
    }

}
