package lab.population;

import lab.Individual;

import java.util.Map;

public record PopulationSnapshot(PopulationTiming populationTiming,
                                 Map<Individual, Double> individualToFitness) {

    public int getSize() {
        return individualToFitness.size();
    }

}