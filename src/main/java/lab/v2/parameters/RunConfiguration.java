package lab.v2.parameters;

import lab.parameters.Encoding;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.population.PopulationConfiguration;

public record RunConfiguration(int populationSize,
                               FitnessFunctionV2<?, ?> function,
                               OperatorsApplicationType operatorsApplicationType,
                               PopulationConfiguration populationConfiguration,
                               Encoding encoding) {
}