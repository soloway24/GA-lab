package lab.v2.run;

import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.convertor.ProbabilityToExpectedQuantityConvertor;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.population.Population;
import lab.v2.population.PopulationConfiguration;
import lab.v2.population.PopulationPool;
import lab.v2.population.PopulationPoolInitializer;
import lab.v2.selection.RwsSelector;
import lab.v2.selection.Selector;
import lab.v2.selection.SusSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lab.parameters.Encoding.STANDARD;
import static lab.v2.operator.OperatorType.CROSSOVER;
import static lab.v2.operator.OperatorType.MUTATION;
import static lab.v2.population.PopulationType.ONE_OPTIMAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RunPoolCreatorTest {

    private static final int RUN_POOL_SIZE = 2;
    private static final int POPULATION_SIZE = 10;

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();
    private final ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor = new ProbabilityToExpectedQuantityConvertor();
    private final Selector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
    private final Selector susSelector = new SusSelector(fitnessToProbabilityConvertor, probabilityToExpectedQuantityConvertor);

    @Mock
    private FitnessFunctionV2<Integer, Integer> function1;
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
        PopulationConfiguration populationConfiguration = new PopulationConfiguration(function1, ONE_OPTIMAL,
                STANDARD, POPULATION_SIZE);
        RunPoolConfiguration runPoolConfiguration1 = new RunPoolConfiguration(
                new RunConfiguration(function1, rwsSelector, CROSSOVER, ONE_OPTIMAL, STANDARD, POPULATION_SIZE), RUN_POOL_SIZE
        );
        RunPoolConfiguration runPoolConfiguration2 = new RunPoolConfiguration(
                new RunConfiguration(function1, susSelector, MUTATION, ONE_OPTIMAL, STANDARD, POPULATION_SIZE), RUN_POOL_SIZE
        );
        runPoolConfigurations = List.of(runPoolConfiguration1, runPoolConfiguration2);
        List<Population> populations = List.of(population1, population2);
        PopulationPool populationPool = new PopulationPool(populationConfiguration, populations);

        when(populationPoolInitializer.initializePopulationPool(populationConfiguration, RUN_POOL_SIZE)).thenReturn(populationPool);
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