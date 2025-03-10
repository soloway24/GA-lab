package lab.run;

import lab.encoding.Encoding;
import lab.function.FConstAllFunction;
import lab.function.FitnessFunction;
import lab.function.PowerFunction;
import lab.operator.NoneOperator;
import lab.operator.Operator;
import lab.population.Population;
import lab.population.PopulationConfiguration;
import lab.population.PopulationInitializer;
import lab.population.PopulationType;
import lab.selection.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static lab.encoding.Encoding.STANDARD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@ActiveProfiles(value = "test")
class RunPoolExecutorIntegrationTest {

    private static final int POPULATION_SIZE = 100;
    @Autowired
    private RunPoolStatsCreator runPoolStatsCreator;
    @Autowired
    private RunPoolExecutor runPoolExecutor;
    @Autowired
    private PopulationInitializer populationInitializer;

    // selectors
    @Autowired
    private RwsSelector rwsSelector;
    @Autowired
    private SusSelector susSelector;
    @Autowired
    private ScalingSelector scalingSelector;

    // functions
    private final FitnessFunction<Double, Double> quadraticFunction = new PowerFunction(
            10, 0.0, 10.23, 2, 2
    );
    private final FitnessFunction<Number, Integer> fConstAllFunction = FConstAllFunction.getInstance();
    private final Operator noneOperator = new NoneOperator();

    private DynamicPowerScalingRwsSelector dynamicPowerScalingRwsSelector0p9to1p1;
    private DynamicPowerScalingRwsSelector dynamicPowerScalingRwsSelector0p8to1p2;

    private PowerScalingRwsSelector powerScalingRwsSelector;
    private PowerScalingSusSelector powerScalingSusSelector;


    @BeforeEach
    void setUp() {
        PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, 1.1);
        DynamicPowerScalingSelector dynamicPowerScalingSelector0p9to1p1 = new DynamicPowerScalingSelector(scalingSelector, 0.9, 1.1);
        DynamicPowerScalingSelector dynamicPowerScalingSelector0p8to1p2 = new DynamicPowerScalingSelector(scalingSelector, 0.8, 1.2);
        dynamicPowerScalingRwsSelector0p9to1p1 =
                new DynamicPowerScalingRwsSelector(dynamicPowerScalingSelector0p9to1p1, rwsSelector);
        dynamicPowerScalingRwsSelector0p8to1p2 =
                new DynamicPowerScalingRwsSelector(dynamicPowerScalingSelector0p8to1p2, rwsSelector);
        DynamicPowerScalingSusSelector dynamicPowerScalingSusSelector0p9to1p1 = new DynamicPowerScalingSusSelector(dynamicPowerScalingSelector0p9to1p1, susSelector);
        DynamicPowerScalingSusSelector dynamicPowerScalingSusSelector0p8to1p2 = new DynamicPowerScalingSusSelector(dynamicPowerScalingSelector0p8to1p2, susSelector);
        powerScalingRwsSelector = new PowerScalingRwsSelector(powerScalingSelector, rwsSelector);
        powerScalingSusSelector = new PowerScalingSusSelector(powerScalingSelector, susSelector);
    }

    @Test
    public void whenRunQuadratic1TheSuccess() {
        testFunction(quadraticFunction, rwsSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD);
    }

    @Test
    public void whenRunFconst1AllTheSuccess() {
        testFunction(fConstAllFunction, rwsSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD);
    }

    @Test
    public void whenRunFconstAllTheSuccess() {
        testFunction(fConstAllFunction, powerScalingRwsSelector, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD);
    }

    @Test
    public void whenRunFconstAll2TheSuccess() {
        testFunction(fConstAllFunction, dynamicPowerScalingRwsSelector0p9to1p1, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD);
    }

    @Test
    public void whenRunFconstAll3TheSuccess() {
        testFunction(fConstAllFunction, dynamicPowerScalingRwsSelector0p8to1p2, noneOperator, PopulationType.TEN_PERCENT_OPTIMAL, STANDARD);
    }

//    @Test()
//    public void whenRunFconstAllWithSusTheSuccess() {
//        testFunction(fConstAllFunction, susSelector, noneOperator, TEN_PERCENT_OPTIMAL, STANDARD);
//    }

    private void testFunction(FitnessFunction<?, ?> function,
                              Selector selector,
                              Operator operator,
                              PopulationType populationType,
                              Encoding encoding) {
        PopulationConfiguration populationConfiguration = new PopulationConfiguration(function,
                populationType, encoding, POPULATION_SIZE);

        RunConfiguration runConfiguration = new RunConfiguration(function, selector,
                operator, populationType, encoding, POPULATION_SIZE);

        Population population = populationInitializer.initializePopulation(populationConfiguration);

        Run run = new Run(runConfiguration, population);

        System.out.println("Population = " + population);
        RunStats runStats = runPoolExecutor.executeRun(run, 1, 1, 1, 1);
        System.out.println("Result = " + runStats);

        assertThat(runStats.isSuc(), equalTo(true));

        RunPoolStats runPoolStats = runPoolStatsCreator.create(List.of(runStats), runConfiguration);
        System.out.println("runPoolStats = " + runPoolStats);
    }
}