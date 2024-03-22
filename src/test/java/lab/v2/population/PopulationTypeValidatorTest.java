package lab.v2.population;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PopulationTypeValidatorTest {

    private static final int POPULATION_SIZE = 100;

    private final PopulationTypeValidator populationTypeValidator = PopulationTypeValidator.getInstance();

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    public void whenValidOptimalQuantityThenSuccess(int optimalQuantity) {
        populationTypeValidator.verifyOptimalQuantity(optimalQuantity, POPULATION_SIZE);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    public void whenInvalidOptimalQuantityThenFailure(int optimalQuantity) {
        assertThrows(IllegalArgumentException.class,
                () -> populationTypeValidator.verifyOptimalQuantity(optimalQuantity, POPULATION_SIZE));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.5, 1})
    public void whenValidOptimalPercentageThenSuccess(double optimalPercentage) {
        populationTypeValidator.verifyOptimalPercentage(optimalPercentage);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.5, 0.001, 1.1})
    public void whenInvalidOptimalPercentageThenFailure(double optimalPercentage) {
        assertThrows(IllegalArgumentException.class,
                () -> populationTypeValidator.verifyOptimalPercentage(optimalPercentage));
    }
}