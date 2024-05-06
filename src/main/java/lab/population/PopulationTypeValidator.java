package lab.population;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;

public class PopulationTypeValidator {

    private static PopulationTypeValidator instance;

    private PopulationTypeValidator() {
    }

    public static PopulationTypeValidator getInstance() {
        return ofNullable(instance)
                .orElse(new PopulationTypeValidator());
    }

    public void verifyOptimalQuantity(int optimalQuantity, int populationSize) {
        if (optimalQuantity < 0) {
            throw new IllegalArgumentException("Provided quantity of optimal individuals in the population " + optimalQuantity
                    + " is less than 0 !");
        }
        if (optimalQuantity > populationSize) {
            throw new IllegalArgumentException("Provided quantity of optimal individuals in the population " + optimalQuantity
                    + " is greater than population size " + populationSize + " !");
        }
    }

    public void verifyOptimalPercentage(double optimalPercentage) {
        if (optimalPercentage < 0 || optimalPercentage > 1) {
            throw new IllegalArgumentException("Provided percentage of optimal individuals in the population " + optimalPercentage
                    + " is not in the range of [0.0, 1.0]!");
        }

        int scale = BigDecimal.valueOf(optimalPercentage).scale();
        if (scale > 2) {
            throw new IllegalArgumentException("Provided percentage of optimal individuals in the population " + optimalPercentage
                    + " should have max scale of 2 in order to represent a whole number of individuals, but has scale of " + scale + " !");
        }
    }
}