package lab.population;

import lab.Individual;

import java.util.List;

public record Population(List<Individual> individuals) {

    public int getSize() {
        return individuals.size();
    }

}