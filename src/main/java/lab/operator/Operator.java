package lab.operator;

import lab.Individual;

import java.util.List;

public interface Operator {

    String getName();

    OperatorType getOperatorType();

    List<Individual> apply(List<Individual> individuals);

}
