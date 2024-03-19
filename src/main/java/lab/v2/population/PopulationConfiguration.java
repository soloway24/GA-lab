package lab.v2.population;

import java.util.Optional;

public enum PopulationConfiguration {


    RANDOM(PopulationType.RANDOM, null, null),
    ZERO_OPTIMAL(PopulationType.OPTIMAL_QUANTITY, 0, null),
    ONE_OPTIMAL(PopulationType.OPTIMAL_QUANTITY, 1, null),
    FIVE_PERCENT_OPTIMAL(PopulationType.OPTIMAL_PERCENTAGE, null, 0.05),
    TEN_PERCENT_OPTIMAL(PopulationType.OPTIMAL_PERCENTAGE, null, 0.1),
    ;

    private final PopulationType populationType;
    private final Integer optimalQuantity;
    private final Double optimalPercentage;

    PopulationConfiguration(PopulationType populationType, Integer optimalQuantity, Double optimalPercentage) {
        this.populationType = populationType;
        this.optimalQuantity = optimalQuantity;
        this.optimalPercentage = optimalPercentage;
    }

    public PopulationType getPopulationType() {
        return populationType;
    }

    public Optional<Integer> getOptimalQuantity() {
        return Optional.ofNullable(optimalQuantity);

    }

    public Optional<Double> getOptimalPercentage() {
        return Optional.ofNullable(optimalPercentage);
    }
}