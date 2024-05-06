package lab.run;

import lab.convertor.FitnessToProbabilityConvertor;
import lab.convertor.ProbabilityToExpectedQuantityConvertor;
import lab.function.FitnessFunction;
import lab.operator.NoneOperator;
import lab.operator.OnePointCrossoverOperator;
import lab.operator.Operator;
import lab.operator.OperatorType;
import lab.population.PopulationType;
import lab.selection.RwsSelector;
import lab.selection.Selector;
import lab.selection.SelectorType;
import lab.selection.SusSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lab.encoding.Encoding.GRAY;
import static lab.encoding.Encoding.STANDARD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RunConfigurationFactoryTest {

    private static final int POPULATION_SIZE_1 = 100;
    private static final int POPULATION_SIZE_2 = 200;
    private static final List<Integer> POPULATION_SIZES = List.of(POPULATION_SIZE_1, POPULATION_SIZE_2);

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();
    private final ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor = new ProbabilityToExpectedQuantityConvertor();
    private final Selector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
    private final Selector susSelector = new SusSelector(fitnessToProbabilityConvertor, probabilityToExpectedQuantityConvertor);
    private final List<Selector> selectors = List.of(rwsSelector, susSelector);
    private final Operator noneOperator = new NoneOperator();
    private final Operator onePointCrossover = new OnePointCrossoverOperator(1.0);
    private final List<Operator> operators = List.of(noneOperator, onePointCrossover);

    private final RunConfigurationFactory runConfigurationFactory = RunConfigurationFactory.getInstance();

    @Mock
    private FitnessFunction<Integer, Integer> function1;
    @Mock
    private FitnessFunction<Double, Double> function2;
    private List<FitnessFunction<?, ?>> functions;

    @BeforeEach
    public void init() {
        when(function1.getSupportedEncodings()).thenReturn(List.of(STANDARD, GRAY));
        when(function2.getSupportedEncodings()).thenReturn(List.of(STANDARD, GRAY));

        when(function1.getUnsupportedSelectorTypes(OperatorType.NONE)).thenReturn(List.of(SelectorType.SUS));

        when(function1.getSupportedPopulationConfigurations(noneOperator.getOperatorType())).thenReturn(List.of(PopulationType.ZERO_OPTIMAL, PopulationType.ONE_OPTIMAL));
        when(function1.getSupportedPopulationConfigurations(onePointCrossover.getOperatorType())).thenReturn(List.of(PopulationType.ZERO_OPTIMAL, PopulationType.ONE_OPTIMAL));

        when(function2.getSupportedPopulationConfigurations(noneOperator.getOperatorType())).thenReturn(List.of(PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL));
        when(function2.getSupportedPopulationConfigurations(onePointCrossover.getOperatorType())).thenReturn(List.of(PopulationType.FIVE_PERCENT_OPTIMAL, PopulationType.TEN_PERCENT_OPTIMAL));

        functions = List.of(function1, function2);
    }

    @Test
    public void whenCreateAllThenSuccess() {
        assertThat(runConfigurationFactory.createAll(functions, selectors, operators, POPULATION_SIZES),
                containsInAnyOrder(buildExpectedRunConfigurations().toArray()));
    }

    private List<RunConfiguration> buildExpectedRunConfigurations() {
        return List.of(
                new RunConfiguration(function1, rwsSelector, noneOperator, PopulationType.ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, noneOperator, PopulationType.ZERO_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, noneOperator, PopulationType.ONE_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, noneOperator, PopulationType.ONE_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function1, rwsSelector, onePointCrossover, PopulationType.ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, onePointCrossover, PopulationType.ZERO_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, onePointCrossover, PopulationType.ONE_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, rwsSelector, onePointCrossover, PopulationType.ONE_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function2, rwsSelector, noneOperator, PopulationType.FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, noneOperator, PopulationType.FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function2, rwsSelector, onePointCrossover, PopulationType.FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, onePointCrossover, PopulationType.FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, onePointCrossover, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, rwsSelector, onePointCrossover, PopulationType.TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),


                new RunConfiguration(function1, rwsSelector, noneOperator, PopulationType.ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, noneOperator, PopulationType.ZERO_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, noneOperator, PopulationType.ONE_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, noneOperator, PopulationType.ONE_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function1, rwsSelector, onePointCrossover, PopulationType.ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, onePointCrossover, PopulationType.ZERO_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, onePointCrossover, PopulationType.ONE_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, rwsSelector, onePointCrossover, PopulationType.ONE_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function2, rwsSelector, noneOperator, PopulationType.FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, noneOperator, PopulationType.FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function2, rwsSelector, onePointCrossover, PopulationType.FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, onePointCrossover, PopulationType.FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, onePointCrossover, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, rwsSelector, onePointCrossover, PopulationType.TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),


                new RunConfiguration(function1, susSelector, onePointCrossover, PopulationType.ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, susSelector, onePointCrossover, PopulationType.ZERO_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function1, susSelector, onePointCrossover, PopulationType.ONE_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function1, susSelector, onePointCrossover, PopulationType.ONE_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function2, susSelector, noneOperator, PopulationType.FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, noneOperator, PopulationType.FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),

                new RunConfiguration(function2, susSelector, onePointCrossover, PopulationType.FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, onePointCrossover, PopulationType.FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, onePointCrossover, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_1),
                new RunConfiguration(function2, susSelector, onePointCrossover, PopulationType.TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_1),


                new RunConfiguration(function1, susSelector, onePointCrossover, PopulationType.ZERO_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, susSelector, onePointCrossover, PopulationType.ZERO_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function1, susSelector, onePointCrossover, PopulationType.ONE_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function1, susSelector, onePointCrossover, PopulationType.ONE_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function2, susSelector, noneOperator, PopulationType.FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, noneOperator, PopulationType.FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),

                new RunConfiguration(function2, susSelector, onePointCrossover, PopulationType.FIVE_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, onePointCrossover, PopulationType.FIVE_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, onePointCrossover, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD, POPULATION_SIZE_2),
                new RunConfiguration(function2, susSelector, onePointCrossover, PopulationType.TEN_PERCENT_OPTIMAL, GRAY, POPULATION_SIZE_2)
        );
    }
}