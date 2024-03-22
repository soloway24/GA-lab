package lab.v2.population;

import java.util.List;

public record PopulationPool(PopulationPoolConfiguration poolConfiguration,
                             List<Population> populations) {

    public int getSize() {
        return populations.size();
    }

}