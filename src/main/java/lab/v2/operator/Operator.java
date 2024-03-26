package lab.v2.operator;

import lab.model.Individual;

import java.util.List;

public interface Operator {

    String getName();

    List<Individual> apply(List<Individual> individuals);

}
