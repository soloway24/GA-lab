package lab.v2.population;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class PopulationPoolInitializer {

    private final PopulationInitializer populationInitializer;

    public PopulationPool initializePopulationPool(PopulationPoolConfiguration populationPoolConfiguration) {
        List<Population> populations = IntStream.range(0, populationPoolConfiguration.size())
                .mapToObj(i -> populationInitializer.initializePopulation(populationPoolConfiguration.populationConfiguration()))
                .toList();
        return new PopulationPool(populationPoolConfiguration, populations);
    }
}