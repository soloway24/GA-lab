package lab.run;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lab.population.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class RunPoolCreator {

    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    private static final Type CONFIG_TYPE = new TypeToken<Map<PopulationConfigurationKey, PopulationPool>>() {
    }.getType();
    private static final String CONFIG_PATH = "src/main/resources/populations_config.json";
    private static final int MAX_RUN_POOL_SIZE = 100;

    private final PopulationPoolInitializer populationPoolInitializer;
    private final Map<PopulationConfigurationKey, PopulationPool> populationConfigToPool = readOrCreateConfig();

    public List<RunPool> createAll(List<RunPoolConfiguration> runPoolConfigurations) {
        List<RunPool> runPools = runPoolConfigurations.stream()
                .map(this::create)
                .toList();

        saveConfigToFile();
        return runPools;
    }

    private RunPool create(RunPoolConfiguration runPoolConfiguration) {
        PopulationPool populationPool = getPopulationPool(runPoolConfiguration);
        List<Run> runs = createRuns(runPoolConfiguration, populationPool);
        return new RunPool(runPoolConfiguration.runConfiguration(), runs);
    }

    private PopulationPool getPopulationPool(RunPoolConfiguration runPoolConfiguration) {
        PopulationConfiguration populationConfiguration = convert(runPoolConfiguration.runConfiguration());
        PopulationConfigurationKey key = new PopulationConfigurationKey(populationConfiguration);
        PopulationPool maxPopulationPool = ofNullable(populationConfigToPool.get(key))
                .orElseGet(() -> addAndGetPopulationPool(populationConfiguration));
        return getLimitedPopulationPool(runPoolConfiguration, maxPopulationPool);
    }

    private PopulationPool getLimitedPopulationPool(RunPoolConfiguration runPoolConfiguration, PopulationPool populationPool) {
        List<Population> populations = populationPool.populations()
                .stream()
                .limit(runPoolConfiguration.runPoolSize())
                .toList();
        return new PopulationPool(populations);
    }

    private PopulationConfiguration convert(RunConfiguration runConfiguration) {
        return new PopulationConfiguration(
                runConfiguration.function(),
                runConfiguration.populationType(),
                runConfiguration.encoding(),
                runConfiguration.populationSize()
        );
    }

    private PopulationPool addAndGetPopulationPool(PopulationConfiguration populationConfiguration) {
        PopulationPool populationPool = populationPoolInitializer.initializePopulationPool(populationConfiguration, MAX_RUN_POOL_SIZE);
        PopulationConfigurationKey key = new PopulationConfigurationKey(populationConfiguration);
        populationConfigToPool.put(key, populationPool);
        return populationPool;
    }

    private List<Run> createRuns(RunPoolConfiguration runPoolConfiguration, PopulationPool populationPool) {
        return populationPool.populations()
                .stream()
                .map(population -> new Run(runPoolConfiguration.runConfiguration(), population))
                .toList();
    }

    private Map<PopulationConfigurationKey, PopulationPool> readOrCreateConfig() {
        File configFile = new File(CONFIG_PATH);
        if (configFile.exists()) {
            try (FileReader fileReader = new FileReader(configFile)) {
                return GSON.fromJson(fileReader, CONFIG_TYPE);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read population config from file", e);
            }
        }
        return new HashMap<>();
    }

    private void saveConfigToFile() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            GSON.toJson(populationConfigToPool, CONFIG_TYPE, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write population config to file", e);
        }
    }
}
