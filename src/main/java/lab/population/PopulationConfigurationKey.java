package lab.population;

import lab.encoding.Encoding;

public record PopulationConfigurationKey(String functionName,
                                         PopulationType populationType,
                                         Encoding encoding,
                                         int populationSize) {

    public PopulationConfigurationKey(PopulationConfiguration populationConfiguration) {
        this(
                populationConfiguration.function().getName(),
                populationConfiguration.populationType(),
                populationConfiguration.encoding(),
                populationConfiguration.populationSize()
        );
    }
}