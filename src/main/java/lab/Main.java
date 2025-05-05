package lab;

import lab.function.*;
import lab.operator.NoneOperator;
import lab.operator.OnePointCrossoverOperator;
import lab.operator.Operator;
import lab.run.RunConfigurationFactory;
import lab.run.RunPool;
import lab.run.RunPoolConfiguration;
import lab.run.RunPoolCreator;
import lab.selection.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Main {

    private final RunConfigurationFactory runConfigurationFactory;
    private final RunPoolCreator runPoolCreator;
    private final Executor executor;
    private final RwsSelector rwsSelector;
    private final SusSelector susSelector;

    public void run() throws InterruptedException {
        List<Integer> populationSizes = getPopulationSizes();
        List<FitnessFunction<?, ?>> functions = getFunctions();
        List<Selector> selectors = getSelectors();
        List<Operator> operators = getOperators();
        List<RunPoolConfiguration> runPoolConfigurations = getRunPoolConfigurations(functions, selectors, operators,
                populationSizes);
        List<RunPool> runPools = getRunPools(runPoolConfigurations);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

//        executor.executeAllSingleThread(runPools);
//        executor.executeAll(runPools);
        executor.executeAllParallel(runPools);

        stopWatch.stop();
        System.out.println("Time Elapsed: " + stopWatch.getTime() / 1000.0);
    }

    private List<Integer> getPopulationSizes() {
        return List.of(100);
    }

    private List<FitnessFunction<?, ?>> getFunctions() {
        FitnessFunction<?, ?> constAllFunction = FConstAllFunction.getInstance();
        FitnessFunction<?, ?> fhFunction = FhFunction.getInstance();
        FitnessFunction<?, ?> fhd2Function = new FhdFunction(2);
        FitnessFunction<?, ?> fhd10Function = new FhdFunction(10);
        FitnessFunction<?, ?> fhd100Function = new FhdFunction(100);
        FitnessFunction<?, ?> quadraticFunction = new PowerFunction(10, 0, 10.23, 2, 2);
        FitnessFunction<?, ?> quadratic512Function = new PowerFunction5_12(10, -5.12, 5.11, 2,
                5.12, 2, 2);
        FitnessFunction<?, ?> quadraticSqrt512Function = new PowerFunctionSqrt5_12(10, -5.12, 5.11, 2, 5.12);
        FitnessFunction<?, ?> exponent025 = new ExponentialFunction(10, 0, 10.23, 2, 0.25);
        FitnessFunction<?, ?> exponent1 = new ExponentialFunction(10, 0, 10.23, 2, 1);
        FitnessFunction<?, ?> exponent2 = new ExponentialFunction(10, 0, 10.23, 2, 2);
        FitnessFunction<?, ?> rastriginFunction = new RastriginFunction(10, -5.12, 5.11, 2, 7);
        FitnessFunction<?, ?> deb2Function = new Deb2Function(10, 0, 1.023, 3);
        FitnessFunction<?, ?> deb4Function = new Deb4Function(10, 0, 1.023, 3);
        FitnessFunction<?, ?> testSin2 = new TestSin2Function(10, 0, 10.23, 2);
        FitnessFunction<?, ?> testSin500 = new TestSin500Function(10, 0, 10.23, 2);

//        return List.of(constAllFunction);
//        return List.of(fhFunction);
//        return List.of(quadraticFunction);
//        return List.of(quadratic512Function);
//        return List.of(exponent025);
//        return List.of(exponent1);
//        return List.of(exponent2);
        return List.of(
//                fhFunction
//                fhd2Function
//                fhd10Function,
//                fhd100Function
//                .constAllFunction
//                ,
//                quadraticFunction
//                , quadratic512Function
//                quadraticSqrt512Function
//                , exponent025
//                , exponent1
//                , exponent2
//                 rastriginFunction
//                , deb2Function
//                , deb4Function
//                testSin2
//                ,
                testSin500
        );
    }

    private List<Selector> getSelectors() {
        ScalingSelector scalingSelector = new ScalingSelector();
        PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, 1.1);
        PowerScalingSelector powerScalingSelector2 = new PowerScalingSelector(scalingSelector, 2);

        PowerScalingRwsSelector powerScalingRwsSelector = new PowerScalingRwsSelector(powerScalingSelector, rwsSelector);
        PowerScalingRwsSelector powerScalingRwsSelector2 = new PowerScalingRwsSelector(powerScalingSelector2, rwsSelector);
        PowerScalingSusSelector powerScalingSusSelector = new PowerScalingSusSelector(powerScalingSelector, susSelector);
        PowerScalingSusSelector powerScalingSusSelector2 = new PowerScalingSusSelector(powerScalingSelector2, susSelector);

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

        return List.of(
//                rwsSelector
//                ,
                susSelector
//                , powerScalingRwsSelector
//                , powerScalingRwsSelector2
//                , powerScalingSusSelector
//                , powerScalingSusSelector2
//                , dynamicPowerScalingRwsSelector0p9to1p1
//                , dynamicPowerScalingRwsSelector0p8to1p2
//                , dynamicPowerScalingSusSelector0p9to1p1
//                , dynamicPowerScalingSusSelector0p8to1p2
        );
    }

    private List<Operator> getOperators() {
        Operator noneOperator = new NoneOperator();
        Operator crossoverOperator = new OnePointCrossoverOperator(1);

        return List.of(noneOperator);
//        return List.of(crossoverOperator);
    }

    private List<RunPoolConfiguration> getRunPoolConfigurations(List<FitnessFunction<?, ?>> functions,
                                                                List<Selector> selectors,
                                                                List<Operator> operators,
                                                                List<Integer> populationSizes) {
        return runConfigurationFactory.createPoolConfigurations(functions, selectors, operators, populationSizes);
    }

    private List<RunPool> getRunPools(List<RunPoolConfiguration> runPoolConfigurations) {
        return runPoolCreator.createAll(runPoolConfigurations);
    }
}
