package lab.run;

import lab.encoding.Encoding;
import lab.function.FitnessFunction;
import lab.operator.Operator;
import lab.population.PopulationType;
import lab.selection.Selector;

public record RunConfiguration(FitnessFunction<?, ? extends Number> function,
                               Selector selector,
                               Operator operator,
                               PopulationType populationType,
                               Encoding encoding,
                               int populationSize) {
}