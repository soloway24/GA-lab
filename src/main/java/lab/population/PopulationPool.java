package lab.population;

import java.util.List;

public record PopulationPool(List<Population> populations) {

    public int getSize() {
        return populations.size();
    }

}