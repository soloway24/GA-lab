package lab.population;

import lab.Individual;

import java.util.Map;

public record PopulationSnapshot(PopulationTiming populationTiming,
                                 PopulationConfiguration populationConfiguration,
                                 Map<Individual, Double> individualToFitness) {

    public int getSize() {
        return individualToFitness.size();
    }

}