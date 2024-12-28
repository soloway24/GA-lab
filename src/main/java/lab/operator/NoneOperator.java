package lab.operator;

import lab.Individual;
import lab.util.CalculationUtils;

import java.util.List;

import static lab.operator.OperatorType.NONE;

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
        return CalculationUtils.getIndexedIndividuals(individuals);
    }

}
