package lab.population;

import lab.encoding.Encoding;
import lab.function.FitnessFunction;

public record PopulationConfiguration(FitnessFunction<?, ?> function,
                                      PopulationType populationType,
                                      Encoding encoding,
                                      int populationSize) {
}
