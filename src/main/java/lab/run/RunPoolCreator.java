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
import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class RunPoolCreator {

    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    private static final Type CONFIG_TYPE = new TypeToken<Map<PopulationConfigurationKey, PopulationPool>>() {
    }.getType();
    private static final String CONFIG_PATH = "src/main/resources/populations_config_%s.json";
    private static final int MAX_RUN_POOL_SIZE = 100;

    private final PopulationPoolInitializer populationPoolInitializer;
    private final Map<String, Map<PopulationConfigurationKey, PopulationPool>> functionToConfigs = new HashMap<>();

    public List<RunPool> createAll(List<RunPoolConfiguration> runPoolConfigurations) {
        List<RunPool> runPools = runPoolConfigurations.stream()
                .map(this::create)
                .toList();

        saveConfigsToFiles();
        return runPools;
    }

    private RunPool create(RunPoolConfiguration runPoolConfiguration) {
        PopulationPool populationPool = getPopulationPool(runPoolConfiguration);
        List<Run> runs = createRuns(runPoolConfiguration, populationPool);
        return new RunPool(runPoolConfiguration.runConfiguration(), runs);
    }

    private PopulationPool getPopulationPool(RunPoolConfiguration runPoolConfiguration) {
        PopulationConfiguration populationConfiguration = convert(runPoolConfiguration.runConfiguration());

        String functionName = populationConfiguration.function().getName();
        Map<PopulationConfigurationKey, PopulationPool> configs = ofNullable(functionToConfigs.get(functionName))
                .orElseGet(() -> addAndGetFunctionConfig(functionName));

        PopulationConfigurationKey key = new PopulationConfigurationKey(populationConfiguration);
        PopulationPool maxPopulationPool = ofNullable(configs.get(key))
                .orElseGet(() -> addAndGetPopulationPool(populationConfiguration, functionName));
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

    Map<PopulationConfigurationKey, PopulationPool> addAndGetFunctionConfig(String functionName) {
        var config = readOrCreateConfig(functionName);
        functionToConfigs.put(functionName, config);
        return config;
    }

    private PopulationPool addAndGetPopulationPool(PopulationConfiguration populationConfiguration, String functionName) {
        PopulationPool populationPool = populationPoolInitializer.initializePopulationPool(populationConfiguration, MAX_RUN_POOL_SIZE);
        PopulationConfigurationKey key = new PopulationConfigurationKey(populationConfiguration);
        functionToConfigs.get(functionName).put(key, populationPool);
        return populationPool;
    }

    private List<Run> createRuns(RunPoolConfiguration runPoolConfiguration, PopulationPool populationPool) {
        return populationPool.populations()
                .stream()
                .map(population -> new Run(runPoolConfiguration.runConfiguration(), population))
                .toList();
    }

    private Map<PopulationConfigurationKey, PopulationPool> readOrCreateConfig(String functionName) {
        String configPath = String.format(CONFIG_PATH, functionName);
        File configFile = new File(configPath);
        if (configFile.exists()) {
            try (FileReader fileReader = new FileReader(configFile)) {
                return GSON.fromJson(fileReader, CONFIG_TYPE);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read population config from file", e);
            }
        }
        return new HashMap<>();
    }

    private void saveConfigsToFiles() {
        functionToConfigs.forEach((functionName, config) -> {
            String configPath = String.format(CONFIG_PATH, functionName);
            try (FileWriter writer = new FileWriter(configPath)) {
                GSON.toJson(config, CONFIG_TYPE, writer);
            } catch (IOException e) {
                throw new RuntimeException("Failed to write population config for function " + functionName + " to file", e);
            }
        });
    }

    public static void main(String[] args) {
        String configPath = "src/main/resources/populations_config_4.json";
        String functionName = "x^2.0";
        File configFile = new File(configPath);
        if (configFile.exists()) {
            try (FileReader fileReader = new FileReader(configFile)) {
                Map<PopulationConfigurationKey, PopulationPool> config = GSON.fromJson(fileReader, CONFIG_TYPE);
                var selectedConfig = config.entrySet().stream()
                        .filter(entry -> entry.getKey().functionName().equals(functionName))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

                String newConfigPath = String.format(CONFIG_PATH, functionName);
                try (FileWriter writer = new FileWriter(newConfigPath)) {
                    GSON.toJson(selectedConfig, CONFIG_TYPE, writer);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to write population config for function " + functionName + " to file", e);
                }

                File newConfigFile = new File(newConfigPath);
                if (configFile.exists()) {
                    try (FileReader newFileReader = new FileReader(newConfigFile)) {
                        Map<PopulationConfigurationKey, PopulationPool> newConfig = GSON.fromJson(newFileReader, CONFIG_TYPE);
                        System.out.println(newConfig);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read population config from file", e);
            }
        }
    }
}
