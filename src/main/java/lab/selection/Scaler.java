package lab.selection;

import lab.Individual;

import java.util.Map;

public interface Scaler {

    <T extends Number> double scale(T fitness, Map<Individual, T> individualToFitness);

}