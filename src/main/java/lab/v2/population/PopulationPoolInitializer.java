package lab.v2.population;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class PopulationPoolInitializer {

    private final PopulationInitializer populationInitializer;

    public PopulationPool initializePopulationPool(PopulationConfiguration populationConfiguration, int poolSize) {
        List<Population> populations = IntStream.range(0, poolSize)
                .mapToObj(i -> populationInitializer.initializePopulation(populationConfiguration))
                .toList();
        return new PopulationPool(populationConfiguration, populations);
    }
}