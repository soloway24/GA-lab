package lab.v2.run;

import lab.v2.encoding.Encoding;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.operator.Operator;
import lab.v2.population.PopulationType;
import lab.v2.selection.Selector;

public record RunConfiguration(FitnessFunctionV2<?, ? extends Number> function,
                               Selector selector,
                               Operator operator,
                               PopulationType populationType,
                               Encoding encoding,
                               int populationSize) {
}