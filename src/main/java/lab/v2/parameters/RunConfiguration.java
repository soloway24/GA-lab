package lab.v2.parameters;

import lab.parameters.Encoding;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.population.PopulationType;

public record RunConfiguration(FitnessFunctionV2<?, ?> function,
                               OperatorsApplicationType operatorsApplicationType,
                               PopulationType populationType,
                               Encoding encoding,
                               int populationSize) {
}