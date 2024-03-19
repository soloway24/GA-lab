package lab.v2.population;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static lab.v2.population.PopulationConfigurationValidator.POPULATION_CONFIGURATION_VALIDATOR;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PopulationConfigurationValidatorTest {

    private static final int POPULATION_SIZE = 100;

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    public void whenValidOptimalQuantityThenSuccess(int optimalQuantity) {
        POPULATION_CONFIGURATION_VALIDATOR.verifyOptimalQuantity(optimalQuantity, POPULATION_SIZE);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    public void whenInvalidOptimalQuantityThenFailure(int optimalQuantity) {
        assertThrows(IllegalArgumentException.class,
                () -> POPULATION_CONFIGURATION_VALIDATOR.verifyOptimalQuantity(optimalQuantity, POPULATION_SIZE));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.5, 1})
    public void whenValidOptimalPercentageThenSuccess(double optimalPercentage) {
        POPULATION_CONFIGURATION_VALIDATOR.verifyOptimalPercentage(optimalPercentage);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.5, 0.001, 1.1})
    public void whenInvalidOptimalPercentageThenFailure(double optimalPercentage) {
        assertThrows(IllegalArgumentException.class,
                () -> POPULATION_CONFIGURATION_VALIDATOR.verifyOptimalPercentage(optimalPercentage));
    }
}