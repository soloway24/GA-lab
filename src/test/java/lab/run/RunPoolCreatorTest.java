package lab.run;

import lab.function.FitnessFunction;
import lab.operator.NoneOperator;
import lab.operator.OnePointCrossoverOperator;
import lab.operator.Operator;
import lab.population.*;
import lab.selection.Selector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lab.encoding.Encoding.STANDARD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RunPoolCreatorTest {

    private static final int MAX_POOL_SIZE = 100;
    private static final int RUN_POOL_SIZE = 2;
    private static final int POPULATION_SIZE = 10;

    private final Operator noneOperator = new NoneOperator();
    private final Operator onePointCrossover = new OnePointCrossoverOperator(1.0);

    @Mock
    private Selector rwsSelector;
    @Mock
    private Selector susSelector;
    @Mock
    private FitnessFunction<Integer, Integer> function1;
    @Mock
    private Population population1;
    @Mock
    private Population population2;
    @Mock
    private PopulationPoolInitializer populationPoolInitializer;
    @InjectMocks
    private RunPoolCreator runPoolCreator;

    private List<RunPoolConfiguration> runPoolConfigurations;

    @BeforeEach
    public void init() {
        PopulationConfiguration populationConfiguration = new PopulationConfiguration(function1, PopulationType.ONE_OPTIMAL,
                STANDARD, POPULATION_SIZE);
        RunPoolConfiguration runPoolConfiguration1 = new RunPoolConfiguration(
                new RunConfiguration(function1, rwsSelector, noneOperator, PopulationType.ONE_OPTIMAL, STANDARD, POPULATION_SIZE), RUN_POOL_SIZE
        );
        RunPoolConfiguration runPoolConfiguration2 = new RunPoolConfiguration(
                new RunConfiguration(function1, susSelector, onePointCrossover, PopulationType.ONE_OPTIMAL, STANDARD, POPULATION_SIZE), RUN_POOL_SIZE
        );
        runPoolConfigurations = List.of(runPoolConfiguration1, runPoolConfiguration2);
        List<Population> populations = List.of(population1, population2);
        PopulationPool populationPool = new PopulationPool(populationConfiguration, populations);

        when(populationPoolInitializer.initializePopulationPool(populationConfiguration, MAX_POOL_SIZE)).thenReturn(populationPool);
    }

    @Test
    public void whenCreateThenSuccess() {
        List<RunPool> runPools = runPoolCreator.createAll(runPoolConfigurations);
        assertThat(runPools, hasSize(RUN_POOL_SIZE));

        Population population11 = runPools.get(0).runs().get(0).population();
        Population population12 = runPools.get(0).runs().get(1).population();
        Population population21 = runPools.get(1).runs().get(0).population();
        Population population22 = runPools.get(1).runs().get(1).population();

        assertThat(population11, equalTo(population21));
        assertThat(population12, equalTo(population22));
    }

}