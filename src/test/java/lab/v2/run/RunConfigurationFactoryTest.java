package lab.v2.run;

import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.convertor.ProbabilityToExpectedQuantityConvertor;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.parameters.OperatorsApplicationType;
import lab.v2.selection.RwsSelector;
import lab.v2.selection.Selector;
import lab.v2.selection.SusSelector;
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
import static lab.v2.population.PopulationType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RunConfigurationFactoryTest {

    private static final int POPULATION_SIZE_1 = 100;
    private static final int POPULATION_SIZE_2 = 200;
    private static final List<Integer> POPULATION_SIZES = List.of(POPULATION_SIZE_1, POPULATION_SIZE_2);
    private static final List<OperatorsApplicationType> OPERATORS_APPLICATION_TYPES = List.of(CROSSOVER, MUTATION);

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();
    private final ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor = new ProbabilityToExpectedQuantityConvertor();
    private final Selector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
    private final Selector susSelector = new SusSelector(fitnessToProbabilityConvertor, probabilityToExpectedQuantityConvertor);
    private final List<Selector> selectors = List.of(rwsSelector, susSelector);

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
        assertThat(runConfigurationFactory.createAll(functions, selectors, OPERATORS_APPLICATION_TYPES, POPULATION_SIZES),
                containsInAnyOrder(buildExpectedRunConfigurations().toArray()));
    }

    private List<RunConfiguration> buildExpectedRunConfigurations() {
        return List.of(
                new RunConfiguration(function1, rwsSelector, CROSSOVER, ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, CROSSOVER, ZERO_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, CROSSOVER, ONE_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, CROSSOVER, ONE_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function1, rwsSelector, MUTATION, ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, MUTATION, ZERO_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, MUTATION, ONE_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, MUTATION, ONE_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function2, rwsSelector, CROSSOVER, FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, CROSSOVER, FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, CROSSOVER, TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, CROSSOVER, TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function2, rwsSelector, MUTATION, FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, MUTATION, FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, MUTATION, TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, MUTATION, TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),


                new RunConfiguration(function1, rwsSelector, CROSSOVER, ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, CROSSOVER, ZERO_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, CROSSOVER, ONE_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, CROSSOVER, ONE_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function1, rwsSelector, MUTATION, ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, MUTATION, ZERO_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, MUTATION, ONE_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, MUTATION, ONE_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function2, rwsSelector, CROSSOVER, FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, CROSSOVER, FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, CROSSOVER, TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, CROSSOVER, TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function2, rwsSelector, MUTATION, FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, MUTATION, FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, MUTATION, TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, MUTATION, TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),


                new RunConfiguration(function1, susSelector, CROSSOVER, ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, susSelector, CROSSOVER, ZERO_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function1, susSelector, CROSSOVER, ONE_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, susSelector, CROSSOVER, ONE_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function1, susSelector, MUTATION, ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, susSelector, MUTATION, ZERO_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function1, susSelector, MUTATION, ONE_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, susSelector, MUTATION, ONE_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function2, susSelector, CROSSOVER, FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, CROSSOVER, FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, CROSSOVER, TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, CROSSOVER, TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function2, susSelector, MUTATION, FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, MUTATION, FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, MUTATION, TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, MUTATION, TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),


                new RunConfiguration(function1, susSelector, CROSSOVER, ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, susSelector, CROSSOVER, ZERO_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function1, susSelector, CROSSOVER, ONE_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, susSelector, CROSSOVER, ONE_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function1, susSelector, MUTATION, ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, susSelector, MUTATION, ZERO_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function1, susSelector, MUTATION, ONE_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, susSelector, MUTATION, ONE_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function2, susSelector, CROSSOVER, FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, CROSSOVER, FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, CROSSOVER, TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, CROSSOVER, TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function2, susSelector, MUTATION, FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, MUTATION, FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, MUTATION, TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, MUTATION, TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2)
        );
    }
}