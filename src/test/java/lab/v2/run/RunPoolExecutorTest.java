package lab.v2.run;

import lab.parameters.Encoding;
import lab.v2.identifier.ConvergenceIdentifier;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.convertor.ProbabilityToExpectedQuantityConvertor;
import lab.v2.function.FConstAllFunction;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.function.PowerFunction;
import lab.v2.operator.NoneOperator;
import lab.v2.operator.Operator;
import lab.v2.population.*;
import lab.v2.selection.*;
import org.junit.jupiter.api.Test;

import static lab.parameters.Encoding.STANDARD;
import static lab.v2.population.PopulationType.TEN_PERCENT_OPTIMAL;

class RunPoolExecutorTest {

    private static final int POPULATION_SIZE = 100;
    private final ConvergenceIdentifier convergenceIdentifier = new ConvergenceIdentifier();
    private final RunPoolExecutor runPoolExecutor = new RunPoolExecutor(convergenceIdentifier);

    // functions
    private final FitnessFunctionV2<Double, Double> quadraticFunction = new PowerFunction(
            10, 0.0, 10.23, 2, 2
    );
    private final FitnessFunctionV2<Number, Integer> fConstAllFunction = FConstAllFunction.getInstance();

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
        testFunction(quadraticFunction, rwsSelector, noneOperator, TEN_PERCENT_OPTIMAL, STANDARD);
    }

    @Test
    public void whenRunFconst1AllTheSuccess() {
        testFunction(fConstAllFunction, rwsSelector, noneOperator, TEN_PERCENT_OPTIMAL, STANDARD);
    }

    @Test
    public void whenRunFconstAllTheSuccess() {
        testFunction(fConstAllFunction, powerScalingRwsSelector, noneOperator, TEN_PERCENT_OPTIMAL, STANDARD);
    }

    @Test
    public void whenRunFconstAll2TheSuccess() {
        testFunction(fConstAllFunction, dynamicPowerScalingRwsSelector0p9to1p1, noneOperator, TEN_PERCENT_OPTIMAL, STANDARD);
    }

    @Test
    public void whenRunFconstAll3TheSuccess() {
        testFunction(fConstAllFunction, dynamicPowerScalingRwsSelector0p8to1p2, noneOperator, TEN_PERCENT_OPTIMAL, STANDARD);
    }

//    @Test()
//    public void whenRunFconstAllWithSusTheSuccess() {
//        testFunction(fConstAllFunction, susSelector, noneOperator, TEN_PERCENT_OPTIMAL, STANDARD);
//    }

    private void testFunction(FitnessFunctionV2<?, ?> function,
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
        System.out.println("Result = " + runPoolExecutor.executeRun(run));
    }
}