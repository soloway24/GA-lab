package lab.v2.population;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PopulationConfigurationValidatorTest {

    private static final int POPULATION_SIZE = 100;

    private final PopulationConfigurationValidator populationConfigurationValidator = PopulationConfigurationValidator.getInstance();

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    public void whenValidOptimalQuantityThenSuccess(int optimalQuantity) {
        populationConfigurationValidator.verifyOptimalQuantity(optimalQuantity, POPULATION_SIZE);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    public void whenInvalidOptimalQuantityThenFailure(int optimalQuantity) {
        assertThrows(IllegalArgumentException.class,
                () -> populationConfigurationValidator.verifyOptimalQuantity(optimalQuantity, POPULATION_SIZE));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.5, 1})
    public void whenValidOptimalPercentageThenSuccess(double optimalPercentage) {
        populationConfigurationValidator.verifyOptimalPercentage(optimalPercentage);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.5, 0.001, 1.1})
    public void whenInvalidOptimalPercentageThenFailure(double optimalPercentage) {
        assertThrows(IllegalArgumentException.class,
                () -> populationConfigurationValidator.verifyOptimalPercentage(optimalPercentage));
    }
}