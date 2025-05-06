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

import static lab.selection.DynamicLinearScaler.*;

@Component
@RequiredArgsConstructor
public class Main {

    private final RunConfigurationFactory runConfigurationFactory;
    private final RunPoolCreator runPoolCreator;
    private final Executor executor;
    private final RwsSelector rwsSelector;
    private final SusSelector susSelector;
    private final ScalingSelector scalingSelector;

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
                quadraticFunction
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
//                testSin500
        );
    }

    private List<Selector> getSelectors() {
        // power scaling
        PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, 1.1);
        PowerScalingSelector powerScalingSelector2 = new PowerScalingSelector(scalingSelector, 2);

        PowerScalingRwsSelector powerScalingRwsSelector = new PowerScalingRwsSelector(powerScalingSelector, rwsSelector);
        PowerScalingRwsSelector powerScalingRwsSelector2 = new PowerScalingRwsSelector(powerScalingSelector2, rwsSelector);
        PowerScalingSusSelector powerScalingSusSelector = new PowerScalingSusSelector(powerScalingSelector, susSelector);
        PowerScalingSusSelector powerScalingSusSelector2 = new PowerScalingSusSelector(powerScalingSelector2, susSelector);

        // linear scaling
        LinearScalingSelector linearScalingSelector12 = new LinearScalingSelector(scalingSelector, 1.2);
        LinearScalingSelector linearScalingSelector14 = new LinearScalingSelector(scalingSelector, 1.4);
        LinearScalingSelector linearScalingSelector15 = new LinearScalingSelector(scalingSelector, 1.5);
        LinearScalingSelector linearScalingSelector16 = new LinearScalingSelector(scalingSelector, 1.6);
        LinearScalingSelector linearScalingSelector18 = new LinearScalingSelector(scalingSelector, 1.8);
        LinearScalingSelector linearScalingSelector2 = new LinearScalingSelector(scalingSelector, 2);

        LinearScalingRwsSelector linearScalingRws12 = new LinearScalingRwsSelector(linearScalingSelector12, rwsSelector);
        LinearScalingRwsSelector linearScalingRws14 = new LinearScalingRwsSelector(linearScalingSelector14, rwsSelector);
        LinearScalingRwsSelector linearScalingRws15 = new LinearScalingRwsSelector(linearScalingSelector15, rwsSelector);
        LinearScalingRwsSelector linearScalingRws16 = new LinearScalingRwsSelector(linearScalingSelector16, rwsSelector);
        LinearScalingRwsSelector linearScalingRws18 = new LinearScalingRwsSelector(linearScalingSelector18, rwsSelector);
        LinearScalingRwsSelector linearScalingRws2 = new LinearScalingRwsSelector(linearScalingSelector2, rwsSelector);

        LinearScalingSusSelector linearScalingSus12 = new LinearScalingSusSelector(linearScalingSelector12, susSelector);
        LinearScalingSusSelector linearScalingSus14 = new LinearScalingSusSelector(linearScalingSelector14, susSelector);
        LinearScalingSusSelector linearScalingSus15 = new LinearScalingSusSelector(linearScalingSelector15, susSelector);
        LinearScalingSusSelector linearScalingSus16 = new LinearScalingSusSelector(linearScalingSelector16, susSelector);
        LinearScalingSusSelector linearScalingSus18 = new LinearScalingSusSelector(linearScalingSelector18, susSelector);
        LinearScalingSusSelector linearScalingSus2 = new LinearScalingSusSelector(linearScalingSelector2, susSelector);

        // dynamic linear scaling
        DynamicLinearScalingSelector averageLinearScaling = new DynamicLinearScalingSelector(scalingSelector, AVERAGE);
        DynamicLinearScalingSelector medianLinearScaling = new DynamicLinearScalingSelector(scalingSelector, MEDIAN);
        DynamicLinearScalingSelector maxAvgWorstLinearScaling = new DynamicLinearScalingSelector(scalingSelector, MAX_AVG_WORST);

        DynamicLinearScalingRwsSelector averageLinearRws = new DynamicLinearScalingRwsSelector(averageLinearScaling, rwsSelector);
        DynamicLinearScalingRwsSelector medianLinearRws = new DynamicLinearScalingRwsSelector(medianLinearScaling, rwsSelector);
        DynamicLinearScalingRwsSelector maxAvgWorstLinearRws = new DynamicLinearScalingRwsSelector(maxAvgWorstLinearScaling, rwsSelector);
        DynamicLinearScalingSusSelector averageLinearSus = new DynamicLinearScalingSusSelector(averageLinearScaling, susSelector);
        DynamicLinearScalingSusSelector medianLinearSus = new DynamicLinearScalingSusSelector(medianLinearScaling, susSelector);
        DynamicLinearScalingSusSelector maxAvgWorstLinearSus = new DynamicLinearScalingSusSelector(maxAvgWorstLinearScaling, susSelector);

        // dynamic power scaling
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
//                susSelector
//                , powerScalingRwsSelector
//                , powerScalingRwsSelector2
//                , powerScalingSusSelector
//                , powerScalingSusSelector2
//                , dynamicPowerScalingRwsSelector0p9to1p1
//                , dynamicPowerScalingRwsSelector0p8to1p2
//                , dynamicPowerScalingSusSelector0p9to1p1
//                , dynamicPowerScalingSusSelector0p8to1p2,
//
//                linearScalingRws12,
//                linearScalingRws14,
//                linearScalingRws15,
//                linearScalingRws16,
//                linearScalingRws18,
//                linearScalingRws2,
//                linearScalingSus12,
//                linearScalingSus14,
//                linearScalingSus15,
//                linearScalingSus16,
//                linearScalingSus18,
//                linearScalingSus2,

                averageLinearRws,
                medianLinearRws,
                maxAvgWorstLinearRws,
                averageLinearSus,
                medianLinearSus,
                maxAvgWorstLinearSus
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
