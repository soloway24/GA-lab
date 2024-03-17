package lab.parameters;

import lombok.Getter;

@Getter
public enum GeneticOperatorsApplication {
    NONE("NONE"),
    CROSSOVER("CROSSOVER"),
    MUTATION("MUTATION"),
    CROSSOVER_MUTATION("CROSSOVER_MUTATION");

    private final String outPath;

    GeneticOperatorsApplication(String s) {
        outPath = s;
    }
}
