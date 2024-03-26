package lab.v2.population;

import lab.v2.Individual;

import java.util.List;

public record Population(PopulationConfiguration populationConfiguration,
                         List<Individual> individuals) {

    public int getSize() {
        return individuals.size();
    }

}