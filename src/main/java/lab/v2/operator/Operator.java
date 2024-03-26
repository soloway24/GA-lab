package lab.v2.operator;

import lab.v2.Individual;

import java.util.List;

public interface Operator {

    String getName();

    OperatorType getOperatorType();

    List<Individual> apply(List<Individual> individuals);

}
