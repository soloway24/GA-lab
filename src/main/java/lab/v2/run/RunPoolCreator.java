package lab.v2.run;

import lab.v2.population.PopulationConfiguration;
import lab.v2.population.PopulationPool;
import lab.v2.population.PopulationPoolInitializer;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class RunPoolCreator {

    private final PopulationPoolInitializer populationPoolInitializer;
    private final Map<PopulationConfiguration, PopulationPool> populationConfigToPool = new HashMap<>();

    public List<RunPool> createAll(List<RunPoolConfiguration> runPoolConfigurations) {
        return runPoolConfigurations.stream()
                .map(this::create)
                .toList();
    }

    private RunPool create(RunPoolConfiguration runPoolConfiguration) {
        PopulationPool populationPool = getPopulationPool(runPoolConfiguration);
        List<Run> runs = createRuns(runPoolConfiguration, populationPool);
        return new RunPool(runPoolConfiguration.runConfiguration(), runs);
    }

    private PopulationPool getPopulationPool(RunPoolConfiguration runPoolConfiguration) {
        PopulationConfiguration populationConfiguration = convert(runPoolConfiguration.runConfiguration());
        return ofNullable(populationConfigToPool.get(populationConfiguration))
                .orElseGet(() -> addAndGetPopulationPool(populationConfiguration, runPoolConfiguration.runPoolSize()));
    }

    private PopulationConfiguration convert(RunConfiguration runConfiguration) {
        return new PopulationConfiguration(runConfiguration.function(), runConfiguration.populationType(),
                runConfiguration.encoding(), runConfiguration.populationSize());
    }

    private PopulationPool addAndGetPopulationPool(PopulationConfiguration populationConfiguration, int runPoolSize) {
        PopulationPool populationPool = populationPoolInitializer.initializePopulationPool(populationConfiguration, runPoolSize);
        populationConfigToPool.put(populationConfiguration, populationPool);
        return populationPool;
    }

    private List<Run> createRuns(RunPoolConfiguration runPoolConfiguration, PopulationPool populationPool) {
        return populationPool.populations()
                .stream()
                .map(population -> new Run(runPoolConfiguration.runConfiguration(), population))
                .toList();
    }
}