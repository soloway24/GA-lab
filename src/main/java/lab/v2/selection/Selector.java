package lab.v2.selection;

import lab.v2.Individual;

import java.util.List;
import java.util.Map;

public interface Selector {

    String getName();

    List<Individual> select(Map<Individual, ? extends Number> individualToFitness);

}
