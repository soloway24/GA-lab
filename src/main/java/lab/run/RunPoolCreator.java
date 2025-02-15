package lab.run;

import lab.population.Population;
import lab.population.PopulationConfiguration;
import lab.population.PopulationPool;
import lab.population.PopulationPoolInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class RunPoolCreator {

    private static final int MAX_RUN_POOL_SIZE = 100;
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
        PopulationPool maxPopulationPool = ofNullable(populationConfigToPool.get(populationConfiguration))
                .orElseGet(() -> addAndGetPopulationPool(populationConfiguration));
        return getLimitedPopulationPool(runPoolConfiguration, maxPopulationPool);
    }

    private PopulationPool getLimitedPopulationPool(RunPoolConfiguration runPoolConfiguration, PopulationPool populationPool) {
        List<Population> populations = populationPool.populations()
                .stream()
                .limit(runPoolConfiguration.runPoolSize())
                .toList();
        return new PopulationPool(populationPool.populationConfiguration(), populations);
    }

    private PopulationConfiguration convert(RunConfiguration runConfiguration) {
        return new PopulationConfiguration(runConfiguration.function(), runConfiguration.populationType(),
                runConfiguration.encoding(), runConfiguration.populationSize());
    }

    private PopulationPool addAndGetPopulationPool(PopulationConfiguration populationConfiguration) {
        PopulationPool populationPool = populationPoolInitializer.initializePopulationPool(populationConfiguration, MAX_RUN_POOL_SIZE);
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