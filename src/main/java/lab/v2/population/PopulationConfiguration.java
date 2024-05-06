package lab.v2.population;

import lab.v2.encoding.Encoding;
import lab.v2.function.FitnessFunctionV2;

public record PopulationConfiguration(FitnessFunctionV2<?, ?> function,
                                      PopulationType populationType,
                                      Encoding encoding,
                                      int populationSize) {
}
