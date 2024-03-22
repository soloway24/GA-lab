package lab.v2.population;

import java.util.List;

public record PopulationPool(PopulationConfiguration populationConfiguration,
                             List<Population> populations) {

    public int getSize() {
        return populations.size();
    }

}