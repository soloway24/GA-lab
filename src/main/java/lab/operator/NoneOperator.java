package lab.operator;

import lab.util.CalculationUtils;
import lab.Individual;

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
