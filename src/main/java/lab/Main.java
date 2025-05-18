package lab;

import lab.function.*;
import lab.function.f.*;
import lab.operator.NoneOperator;
import lab.operator.OnePointCrossoverOperator;
import lab.operator.Operator;
import lab.run.RunConfigurationFactory;
import lab.run.RunPool;
import lab.run.RunPoolConfiguration;
import lab.run.RunPoolCreator;
import lab.selection.RwsSelector;
import lab.selection.ScalingSelector;
import lab.selection.Selector;
import lab.selection.SusSelector;
import lab.selection.adaptivepowerlaw.AdaptivePowerLawScalingRwsSelector;
import lab.selection.adaptivepowerlaw.AdaptivePowerLawScalingSelector;
import lab.selection.adaptivepowerlaw.AdaptivePowerLawScalingSusSelector;
import lab.selection.linear.*;
import lab.selection.power.*;
import lab.selection.sigmatruncation.SigmaTruncationRwsSelector;
import lab.selection.sigmatruncation.SigmaTruncationSelector;
import lab.selection.sigmatruncation.SigmaTruncationSusSelector;
import lab.selection.spanmethod.SpanMethodRwsSelector;
import lab.selection.spanmethod.SpanMethodSelector;
import lab.selection.spanmethod.SpanMethodSusSelector;
import lab.selection.window.WindowScalingRwsSelector;
import lab.selection.window.WindowScalingSelector;
import lab.selection.window.WindowScalingSusSelector;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lab.selection.linear.DynamicLinearScaler.*;

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


        Map<FitnessFunction<?, ?>, List<RunPool>> functionToRunPools = runPools.stream()
                .collect(Collectors.groupingBy(runPool -> runPool.runConfiguration().function(), Collectors.toList()));

//        executor.executeAllSingleThread(runPools);
//        executor.executeAll(runPools);

        functionToRunPools.forEach((function, fRunPools) -> {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            System.out.println("Running function " + function.getName());

            executor.executeAllParallel(fRunPools);

            stopWatch.stop();
            System.out.println("Time Elapsed: " + stopWatch.getTime() / 1000.0);
        });
    }

    private List<Integer> getPopulationSizes() {
        return List.of(100);
    }

    private List<FitnessFunction<?, ?>> getFunctions() {
//        FitnessFunction<?, ?> constAllFunction = FConstAllFunction.getInstance();
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

        FitnessFunction<?, ?> f1 = new F1Function(5, 10, -5.12, 5.11, 2);
        FitnessFunction<?, ?> f1_1 = new F1_1Function(5, 10, -5.12, 5.11, 2);
        FitnessFunction<?, ?> f1_2 = new F1_2Function(5, 10, 0, 10.23, 2);
        FitnessFunction<?, ?> f2 = new F2Function(5, 10, -5.12, 5.11, 2, 7);
        FitnessFunction<?, ?> f4 = new F4Function(5, 16, -327.68, 327.67, 2);
        FitnessFunction<?, ?> f5 = new F5Function(5, 10, -5.12, 5.11, 2);
        FitnessFunction<?, ?> f6_1 = new F6Function(5, 10, 0, 10.23, 2, 0.1, 1);
        FitnessFunction<?, ?> f6_250 = new F6Function(5, 10, 0, 10.23, 2, 0.1, 250);

//        return List.of(constAllFunction);
//        return List.of(fhFunction);
//        return List.of(quadraticFunction);
//        return List.of(quadratic512Function);
//        return List.of(exponent025);
//        return List.of(exponent1);
//        return List.of(exponent2);
        return List.of(
//                fhFunction
//                ,
//                fhd2Function
//                fhd10Function
//                ,
//                fhd100Function
//                ,
//                constAllFunction
//                ,
//                quadraticFunction
//                ,
                quadratic512Function
//                quadraticSqrt512Function
//                ,
//                exponent025
//                ,
//                exponent1
//                ,
//                exponent2

//                 rastriginFunction
//                , deb2Function
//                , deb4Function
//                testSin2
//                ,
//                testSin500
//                ,
//                f1
//                ,
//                f1_1
//                ,
//                f1_2
//                ,
//                f2
//                ,
//                f4 // TODO not working - contains negative values
//                ,
//                f5
//                ,
//                f6_1 // TODO not working - contains negative values
//                ,
//                f6_250 // TODO not working - contains negative values
        );
    }

    private List<Selector> getSelectors() {
        // power scaling
        PowerScalingSelector powerScaling11 = new PowerScalingSelector(scalingSelector, 1.1);
        PowerScalingSelector powerScaling12 = new PowerScalingSelector(scalingSelector, 1.2);
        PowerScalingSelector powerScaling15 = new PowerScalingSelector(scalingSelector, 1.5);
        PowerScalingSelector powerScaling2 = new PowerScalingSelector(scalingSelector, 2);
        PowerScalingSelector powerScaling3 = new PowerScalingSelector(scalingSelector, 3);


        PowerScalingRwsSelector powerScalingRws11 = new PowerScalingRwsSelector(powerScaling11, rwsSelector);
        PowerScalingRwsSelector powerScalingRws12 = new PowerScalingRwsSelector(powerScaling12, rwsSelector);
        PowerScalingRwsSelector powerScalingRws15 = new PowerScalingRwsSelector(powerScaling15, rwsSelector);
        PowerScalingRwsSelector powerScalingRws2 = new PowerScalingRwsSelector(powerScaling2, rwsSelector);
        PowerScalingRwsSelector powerScalingRws3 = new PowerScalingRwsSelector(powerScaling3, rwsSelector);

        PowerScalingSusSelector powerScalingSus11 = new PowerScalingSusSelector(powerScaling11, susSelector);
        PowerScalingSusSelector powerScalingSus12 = new PowerScalingSusSelector(powerScaling12, susSelector);
        PowerScalingSusSelector powerScalingSus15 = new PowerScalingSusSelector(powerScaling15, susSelector);
        PowerScalingSusSelector powerScalingSus2 = new PowerScalingSusSelector(powerScaling2, susSelector);
        PowerScalingSusSelector powerScalingSus3 = new PowerScalingSusSelector(powerScaling3, susSelector);

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
        DynamicPowerScalingSelector dynamicPowerScaling0p9to1p1 = new DynamicPowerScalingSelector(scalingSelector, 0.9, 1.1);
        DynamicPowerScalingSelector dynamicPowerScaling0p8to1p2 = new DynamicPowerScalingSelector(scalingSelector, 0.8, 1.2);
        DynamicPowerScalingSelector dynamicPowerScaling0p5to1p5 = new DynamicPowerScalingSelector(scalingSelector, 0.5, 1.5);
        DynamicPowerScalingSelector dynamicPowerScaling0p5to2 = new DynamicPowerScalingSelector(scalingSelector, 0.5, 2);

        DynamicPowerScalingRwsSelector dynamicPowerScalingRws0p9to1p1 = new DynamicPowerScalingRwsSelector(dynamicPowerScaling0p9to1p1, rwsSelector);
        DynamicPowerScalingRwsSelector dynamicPowerScalingRws0p8to1p2 = new DynamicPowerScalingRwsSelector(dynamicPowerScaling0p8to1p2, rwsSelector);
        DynamicPowerScalingRwsSelector dynamicPowerScalingRws0p5to1p5 = new DynamicPowerScalingRwsSelector(dynamicPowerScaling0p5to1p5, rwsSelector);
        DynamicPowerScalingRwsSelector dynamicPowerScalingRws0p5to2 = new DynamicPowerScalingRwsSelector(dynamicPowerScaling0p5to2, rwsSelector);

        DynamicPowerScalingSusSelector dynamicPowerScalingSus0p9to1p1 = new DynamicPowerScalingSusSelector(dynamicPowerScaling0p9to1p1, susSelector);
        DynamicPowerScalingSusSelector dynamicPowerScalingSus0p8to1p2 = new DynamicPowerScalingSusSelector(dynamicPowerScaling0p8to1p2, susSelector);
        DynamicPowerScalingSusSelector dynamicPowerScalingSus0p5to1p5 = new DynamicPowerScalingSusSelector(dynamicPowerScaling0p5to1p5, susSelector);
        DynamicPowerScalingSusSelector dynamicPowerScalingSus0p5to2 = new DynamicPowerScalingSusSelector(dynamicPowerScaling0p5to2, susSelector);

        // sigma truncation
        SigmaTruncationSelector sigmaTruncation1 = new SigmaTruncationSelector(scalingSelector, 1);
        SigmaTruncationSelector sigmaTruncation2 = new SigmaTruncationSelector(scalingSelector, 2);
        SigmaTruncationSelector sigmaTruncation3 = new SigmaTruncationSelector(scalingSelector, 3);
        SigmaTruncationSelector sigmaTruncation4 = new SigmaTruncationSelector(scalingSelector, 4);

        SigmaTruncationRwsSelector sigmaTruncationRws1 = new SigmaTruncationRwsSelector(sigmaTruncation1, rwsSelector);
        SigmaTruncationRwsSelector sigmaTruncationRws2 = new SigmaTruncationRwsSelector(sigmaTruncation2, rwsSelector);
        SigmaTruncationRwsSelector sigmaTruncationRws3 = new SigmaTruncationRwsSelector(sigmaTruncation3, rwsSelector);
        SigmaTruncationRwsSelector sigmaTruncationRws4 = new SigmaTruncationRwsSelector(sigmaTruncation4, rwsSelector);

        SigmaTruncationSusSelector sigmaTruncationSus1 = new SigmaTruncationSusSelector(sigmaTruncation1, susSelector);
        SigmaTruncationSusSelector sigmaTruncationSus2 = new SigmaTruncationSusSelector(sigmaTruncation2, susSelector);
        SigmaTruncationSusSelector sigmaTruncationSus3 = new SigmaTruncationSusSelector(sigmaTruncation3, susSelector);
        SigmaTruncationSusSelector sigmaTruncationSus4 = new SigmaTruncationSusSelector(sigmaTruncation4, susSelector);

        // span method
        SpanMethodSelector spanMethod194 = new SpanMethodSelector(scalingSelector, 194);
        SpanMethodSelector spanMethod300 = new SpanMethodSelector(scalingSelector, 300);
        SpanMethodSelector spanMethod500 = new SpanMethodSelector(scalingSelector, 500);
        SpanMethodSelector spanMethod5000 = new SpanMethodSelector(scalingSelector, 5000);
        SpanMethodSelector spanMethod10000 = new SpanMethodSelector(scalingSelector, 10000);

        SpanMethodRwsSelector spanMethodRws194 = new SpanMethodRwsSelector(spanMethod194, rwsSelector);
        SpanMethodRwsSelector spanMethodRws300 = new SpanMethodRwsSelector(spanMethod300, rwsSelector);
        SpanMethodRwsSelector spanMethodRws500 = new SpanMethodRwsSelector(spanMethod500, rwsSelector);
        SpanMethodSusSelector spanMethodSus5000 = new SpanMethodSusSelector(spanMethod5000, susSelector);
        SpanMethodSusSelector spanMethodSus10000 = new SpanMethodSusSelector(spanMethod10000, susSelector);

        // window scaling
        WindowScalingSelector windowScaling0 = new WindowScalingSelector(scalingSelector, 0);
        WindowScalingSelector windowScaling1 = new WindowScalingSelector(scalingSelector, 1);
        WindowScalingSelector windowScaling2 = new WindowScalingSelector(scalingSelector, 2);
        WindowScalingSelector windowScaling10 = new WindowScalingSelector(scalingSelector, 10);

        WindowScalingRwsSelector windowScalingRws0 = new WindowScalingRwsSelector(windowScaling0, rwsSelector);
        WindowScalingRwsSelector windowScalingRws1 = new WindowScalingRwsSelector(windowScaling1, rwsSelector);
        WindowScalingRwsSelector windowScalingRws2 = new WindowScalingRwsSelector(windowScaling2, rwsSelector);
        WindowScalingRwsSelector windowScalingRws10 = new WindowScalingRwsSelector(windowScaling10, rwsSelector);

        WindowScalingSusSelector windowScalingSus0 = new WindowScalingSusSelector(windowScaling0, susSelector);
        WindowScalingSusSelector windowScalingSus1 = new WindowScalingSusSelector(windowScaling1, susSelector);
        WindowScalingSusSelector windowScalingSus2 = new WindowScalingSusSelector(windowScaling2, susSelector);
        WindowScalingSusSelector windowScalingSus10 = new WindowScalingSusSelector(windowScaling10, susSelector);

        AdaptivePowerLawScalingSelector adaptivePowerLawScaling194 = new AdaptivePowerLawScalingSelector(scalingSelector, 194);
        AdaptivePowerLawScalingSelector adaptivePowerLawScaling300 = new AdaptivePowerLawScalingSelector(scalingSelector, 300);
        AdaptivePowerLawScalingSelector adaptivePowerLawScaling500 = new AdaptivePowerLawScalingSelector(scalingSelector, 500);
        AdaptivePowerLawScalingSelector adaptivePowerLawScaling5000 = new AdaptivePowerLawScalingSelector(scalingSelector, 5000);
        AdaptivePowerLawScalingSelector adaptivePowerLawScaling10000 = new AdaptivePowerLawScalingSelector(scalingSelector, 10000);

        AdaptivePowerLawScalingRwsSelector adaptivePowerLawScalingRws194 = new AdaptivePowerLawScalingRwsSelector(adaptivePowerLawScaling194, rwsSelector);
        AdaptivePowerLawScalingRwsSelector adaptivePowerLawScalingRws300 = new AdaptivePowerLawScalingRwsSelector(adaptivePowerLawScaling300, rwsSelector);
        AdaptivePowerLawScalingRwsSelector adaptivePowerLawScalingRws500 = new AdaptivePowerLawScalingRwsSelector(adaptivePowerLawScaling500, rwsSelector);
        AdaptivePowerLawScalingSusSelector adaptivePowerLawScalingSus5000 = new AdaptivePowerLawScalingSusSelector(adaptivePowerLawScaling5000, susSelector);
        AdaptivePowerLawScalingSusSelector AdaptivePowerLawScalingSus10000 = new AdaptivePowerLawScalingSusSelector(adaptivePowerLawScaling10000, susSelector);

        return List.of(
//                rwsSelector
//                ,
//                powerScalingRws11,
//                powerScalingRws12,
//                powerScalingRws15,
//                powerScalingRws2,
//                powerScalingRws3,
//                dynamicPowerScalingRws0p9to1p1,
//                dynamicPowerScalingRws0p8to1p2,
//                dynamicPowerScalingRws0p5to1p5,
//                dynamicPowerScalingRws0p5to2,
//                linearScalingRws12,
//                linearScalingRws14,
//                linearScalingRws15,
//                linearScalingRws16,
//                linearScalingRws18,
//                linearScalingRws2,
//                averageLinearRws,
//                medianLinearRws,
//                maxAvgWorstLinearRws,
//                sigmaTruncationRws1,
//                sigmaTruncationRws2,
//                sigmaTruncationRws3,
//                sigmaTruncationRws4,
//                spanMethodRws194,
//                spanMethodRws300,
//                spanMethodRws500,
//                adaptivePowerLawScalingRws194,
//                adaptivePowerLawScalingRws300,
//                adaptivePowerLawScalingRws500,
//                windowScalingRws0,
//                windowScalingRws1,
//                windowScalingRws2,
//                windowScalingRws10

//                susSelector
//                ,
//                powerScalingSus11
//                ,
//                powerScalingSus12,
//                powerScalingSus15,
//                powerScalingSus2,
//                powerScalingSus3
//                ,

//                dynamicPowerScalingSus0p9to1p1,
//                dynamicPowerScalingSus0p8to1p2,
//                dynamicPowerScalingSus0p5to1p5,
//                dynamicPowerScalingSus0p5to2,
//
//                linearScalingSus12,
//                linearScalingSus14,
//                linearScalingSus15,
//                linearScalingSus16,
//                linearScalingSus18,
//                linearScalingSus2
//                ,
//
//                averageLinearSus
//                ,
//                medianLinearSus,
                maxAvgWorstLinearSus
//                ,
//
//                sigmaTruncationSus1,
//                sigmaTruncationSus2,
//                sigmaTruncationSus3,
//                sigmaTruncationSus4,
//
//                spanMethodSus5000,
//                adaptivePowerLawScalingSus5000
//                ,
//
//                windowScalingSus0,
//                windowScalingSus1,
//                windowScalingSus2,
//                windowScalingSus10
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
