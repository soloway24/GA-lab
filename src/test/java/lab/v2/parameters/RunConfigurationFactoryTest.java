package lab.v2.parameters;

import lab.v2.function.FitnessFunctionV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lab.parameters.Encoding.GRAY;
import static lab.parameters.Encoding.STANDARD;
import static lab.v2.parameters.OperatorsApplicationType.CROSSOVER;
import static lab.v2.parameters.OperatorsApplicationType.MUTATION;
import static lab.v2.population.PopulationConfiguration.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RunConfigurationFactoryTest {

    private static final int POPULATION_SIZE_1 = 100;
    private static final int POPULATION_SIZE_2 = 200;
    private static final List<Integer> POPULATION_SIZES = List.of(POPULATION_SIZE_1, POPULATION_SIZE_2);
    private static final List<OperatorsApplicationType> OPERATORS_APPLICATION_TYPES = List.of(CROSSOVER, MUTATION);

    private final RunConfigurationFactory runConfigurationFactory = RunConfigurationFactory.getInstance();

    @Mock
    private FitnessFunctionV2<Integer, Integer> function1;
    @Mock
    private FitnessFunctionV2<Double, Double> function2;
    private List<FitnessFunctionV2<?, ?>> functions;

    @BeforeEach
    public void init() {
        when(function1.getSupportedEncodings()).thenReturn(List.of(STANDARD, GRAY));
        when(function2.getSupportedEncodings()).thenReturn(List.of(STANDARD, GRAY));

        when(function1.getSupportedPopulationConfigurations(CROSSOVER)).thenReturn(List.of(ZERO_OPTIMAL, ONE_OPTIMAL));
        when(function1.getSupportedPopulationConfigurations(MUTATION)).thenReturn(List.of(ZERO_OPTIMAL, ONE_OPTIMAL));

        when(function2.getSupportedPopulationConfigurations(CROSSOVER)).thenReturn(List.of(FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL));
        when(function2.getSupportedPopulationConfigurations(MUTATION)).thenReturn(List.of(FIVE_PERCENT_OPTIMAL, TEN_PERCENT_OPTIMAL));

        functions = List.of(function1, function2);
    }

    @Test
    public void whenCreateAllThenSuccess() {
        assertThat(runConfigurationFactory.createAll(POPULATION_SIZES, functions, OPERATORS_APPLICATION_TYPES),
                containsInAnyOrder(buildExpectedRunConfigurations().toArray()));
    }

    private List<RunConfiguration> buildExpectedRunConfigurations() {
        return List.of(
                new RunConfiguration(POPULATION_SIZE_1, function1, CROSSOVER, ZERO_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_1, function1, CROSSOVER, ZERO_OPTIMAL, GRAY),
                new RunConfiguration(POPULATION_SIZE_1, function1, CROSSOVER, ONE_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_1, function1, CROSSOVER, ONE_OPTIMAL, GRAY),

                new RunConfiguration(POPULATION_SIZE_1, function1, MUTATION, ZERO_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_1, function1, MUTATION, ZERO_OPTIMAL, GRAY),
                new RunConfiguration(POPULATION_SIZE_1, function1, MUTATION, ONE_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_1, function1, MUTATION, ONE_OPTIMAL, GRAY),

                new RunConfiguration(POPULATION_SIZE_1, function2, CROSSOVER, FIVE_PERCENT_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_1, function2, CROSSOVER, FIVE_PERCENT_OPTIMAL, GRAY),
                new RunConfiguration(POPULATION_SIZE_1, function2, CROSSOVER, TEN_PERCENT_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_1, function2, CROSSOVER, TEN_PERCENT_OPTIMAL, GRAY),

                new RunConfiguration(POPULATION_SIZE_1, function2, MUTATION, FIVE_PERCENT_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_1, function2, MUTATION, FIVE_PERCENT_OPTIMAL, GRAY),
                new RunConfiguration(POPULATION_SIZE_1, function2, MUTATION, TEN_PERCENT_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_1, function2, MUTATION, TEN_PERCENT_OPTIMAL, GRAY),


                new RunConfiguration(POPULATION_SIZE_2, function1, CROSSOVER, ZERO_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_2, function1, CROSSOVER, ZERO_OPTIMAL, GRAY),
                new RunConfiguration(POPULATION_SIZE_2, function1, CROSSOVER, ONE_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_2, function1, CROSSOVER, ONE_OPTIMAL, GRAY),

                new RunConfiguration(POPULATION_SIZE_2, function1, MUTATION, ZERO_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_2, function1, MUTATION, ZERO_OPTIMAL, GRAY),
                new RunConfiguration(POPULATION_SIZE_2, function1, MUTATION, ONE_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_2, function1, MUTATION, ONE_OPTIMAL, GRAY),

                new RunConfiguration(POPULATION_SIZE_2, function2, CROSSOVER, FIVE_PERCENT_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_2, function2, CROSSOVER, FIVE_PERCENT_OPTIMAL, GRAY),
                new RunConfiguration(POPULATION_SIZE_2, function2, CROSSOVER, TEN_PERCENT_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_2, function2, CROSSOVER, TEN_PERCENT_OPTIMAL, GRAY),

                new RunConfiguration(POPULATION_SIZE_2, function2, MUTATION, FIVE_PERCENT_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_2, function2, MUTATION, FIVE_PERCENT_OPTIMAL, GRAY),
                new RunConfiguration(POPULATION_SIZE_2, function2, MUTATION, TEN_PERCENT_OPTIMAL, STANDARD),
                new RunConfiguration(POPULATION_SIZE_2, function2, MUTATION, TEN_PERCENT_OPTIMAL, GRAY)
        );
    }
}