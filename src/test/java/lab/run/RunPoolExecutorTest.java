package lab.run;

import lab.convertor.FitnessToProbabilityConvertor;
import lab.convertor.ProbabilityToExpectedQuantityConvertor;
import lab.encoding.Encoding;
import lab.function.FConstAllFunction;
import lab.function.FitnessFunction;
import lab.function.PowerFunction;
import lab.identifier.ConvergenceIdentifier;
import lab.operator.NoneOperator;
import lab.operator.Operator;
import lab.population.*;
import lab.selection.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static lab.encoding.Encoding.STANDARD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class RunPoolExecutorTest {

    private static final int POPULATION_SIZE = 100;
    private final ConvergenceIdentifier convergenceIdentifier = new ConvergenceIdentifier();
    private final RunPoolStatsCreator runPoolStatsCreator = new RunPoolStatsCreator();
    private final RunPoolExecutor runPoolExecutor = new RunPoolExecutor(convergenceIdentifier, runPoolStatsCreator);

    // functions
    private final FitnessFunction<Double, Double> quadraticFunction = new PowerFunction(
            10, 0.0, 10.23, 2, 2
    );
    private final FitnessFunction<Number, Integer> fConstAllFunction = FConstAllFunction.getInstance();

    // convertors
    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();
    private final ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor = new ProbabilityToExpectedQuantityConvertor();

    // selectors
    private final RwsSelector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
    private final SusSelector susSelector = new SusSelector(fitnessToProbabilityConvertor, probabilityToExpectedQuantityConvertor);

    private final ScalingSelector scalingSelector = new ScalingSelector();
    private final PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, 1.1);
    DynamicPowerScalingSelector dynamicPowerScalingSelector0p9to1p1 = new DynamicPowerScalingSelector(scalingSelector, 0.9, 1.1);
    DynamicPowerScalingSelector dynamicPowerScalingSelector0p8to1p2 = new DynamicPowerScalingSelector(scalingSelector, 0.8, 1.2);

    DynamicPowerScalingRwsSelector dynamicPowerScalingRwsSelector0p9to1p1 =
            new DynamicPowerScalingRwsSelector(dynamicPowerScalingSelector0p9to1p1, rwsSelector);
    DynamicPowerScalingRwsSelector dynamicPowerScalingRwsSelector0p8to1p2 =
            new DynamicPowerScalingRwsSelector(dynamicPowerScalingSelector0p8to1p2, rwsSelector);

    DynamicPowerScalingSusSelector dynamicPowerScalingSusSelector0p9to1p1 =
            new DynamicPowerScalingSusSelector(dynamicPowerScalingSelector0p9to1p1, susSelector);
    DynamicPowerScalingSusSelector dynamicPowerScalingSusSelector0p8to1p2 =
            new DynamicPowerScalingSusSelector(dynamicPowerScalingSelector0p8to1p2, susSelector);
    private final PowerScalingRwsSelector powerScalingRwsSelector = new PowerScalingRwsSelector(powerScalingSelector, rwsSelector);
    private final PowerScalingSusSelector powerScalingSusSelector = new PowerScalingSusSelector(powerScalingSelector, susSelector);


    private final Operator noneOperator = new NoneOperator();
    private final PopulationTypeValidator populationTypeValidator = PopulationTypeValidator.getInstance();
    private final PopulationInitializer populationInitializer = new PopulationInitializer(populationTypeValidator);

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
        RunStats runStats = runPoolExecutor.executeRun(run);
        System.out.println("Result = " + runStats);

        assertThat(runStats.isSuc(), equalTo(true));

        RunPoolStats runPoolStats = runPoolStatsCreator.create(List.of(runStats), runConfiguration);
        System.out.println("runPoolStats = " + runPoolStats);
    }
}