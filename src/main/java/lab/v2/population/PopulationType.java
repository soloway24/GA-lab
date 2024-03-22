package lab.v2.population;

import java.util.Optional;

public enum PopulationType {

    RANDOM(PopulationInitializationType.RANDOM, null, null),
    ZERO_OPTIMAL(PopulationInitializationType.OPTIMAL_QUANTITY, 0, null),
    ONE_OPTIMAL(PopulationInitializationType.OPTIMAL_QUANTITY, 1, null),
    FIVE_PERCENT_OPTIMAL(PopulationInitializationType.OPTIMAL_PERCENTAGE, null, 0.05),
    TEN_PERCENT_OPTIMAL(PopulationInitializationType.OPTIMAL_PERCENTAGE, null, 0.1),
    ;

    private final PopulationInitializationType populationInitializationType;
    private final Integer optimalQuantity;
    private final Double optimalPercentage;

    PopulationType(PopulationInitializationType populationInitializationType, Integer optimalQuantity, Double optimalPercentage) {
        this.populationInitializationType = populationInitializationType;
        this.optimalQuantity = optimalQuantity;
        this.optimalPercentage = optimalPercentage;
    }

    public PopulationInitializationType getInitializationType() {
        return populationInitializationType;
    }

    public Optional<Integer> getOptimalQuantity() {
        return Optional.ofNullable(optimalQuantity);
    }

    public Optional<Double> getOptimalPercentage() {
        return Optional.ofNullable(optimalPercentage);
    }
}