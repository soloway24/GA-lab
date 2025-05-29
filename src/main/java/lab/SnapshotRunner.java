package lab;

import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import lab.convertor.FitnessToProbabilityConvertor;
import lab.export.SnapshotProbabilityPlotter;
import lab.population.PopulationTimingType;
import lab.selection.*;
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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableMap;
import static lab.selection.linear.DynamicLinearScaler.*;
import static lab.util.CalculationUtils.getAverage;
import static lab.util.CalculationUtils.getMedian;

@Component
@RequiredArgsConstructor
public class SnapshotRunner {

    private final SnapshotParser snapshotParser;
    private final SnapshotProbabilityPlotter snapshotProbabilityPlotter;
    private final RwsSelector rwsSelector;
    private final SusSelector susSelector;
    private final ScalingSelector scalingSelector;
    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor;

    public void run() throws InterruptedException, PythonExecutionException, IOException {
        Path basePath = Paths.get("stats_none/plots/FH/100/NONE/ONE_OPTIMAL/STANDARD/RWS/success (1)/population_snapshots");
        int populationSize = 100;
        List<Selector> selectors = getSelectors();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Map<PopulationTimingType, Map<Individual, Double>> timingToIndividualToFitness = snapshotParser.getTimingToIndividualToFitness(basePath, populationSize);
        Map<Selector, Map<PopulationTimingType, Pair<String, Map<Individual, Double>>>> selectorToProbabilities = selectors.stream()
                .collect(toUnmodifiableMap(Function.identity(), selector -> getSelectorScaledProbabilities(selector, timingToIndividualToFitness)));

        printSelectorProbabilities(selectorToProbabilities);
        snapshotProbabilityPlotter.plotSelectorProbabilities(selectorToProbabilities, basePath);

        stopWatch.stop();
        System.out.println("Time Elapsed: " + stopWatch.getTime() / 1000.0);
    }

    private Map<PopulationTimingType, Pair<String, Map<Individual, Double>>> getSelectorScaledProbabilities(Selector selector,
                                                                                                            Map<PopulationTimingType, Map<Individual, Double>> timingToIndividualToFitness) {
        return timingToIndividualToFitness.entrySet().stream()
                .collect(toUnmodifiableMap(Map.Entry::getKey, entry -> convertToProbabilities(entry.getValue(), selector)));
    }

    private Pair<String, Map<Individual, Double>> convertToProbabilities(Map<Individual, Double> individualToFitness,
                                                                         Selector selector) {
        Map<Individual, Double> scaledIndividualToFitness = selector.scale(new SelectionContext(individualToFitness));
        Map<Individual, Double> individualToProbability = fitnessToProbabilityConvertor.convertToSelectionProbabilities(scaledIndividualToFitness);
        Map<Individual, Double> sortedIndividualToProbability = individualToProbability.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> b, LinkedHashMap::new));

        String selectorName = getSelectorName(selector, individualToFitness);
        return Pair.of(selectorName, sortedIndividualToProbability);
    }

    private String getSelectorName(Selector selector, Map<Individual, Double> individualToFitness) {

        if (selector.getParam1().isPresent() && selector.getParam2().isPresent()) {
            double medianFitness = getMedian(individualToFitness.values());
            double averageFitness = getAverage(individualToFitness.values());

            String selectorFullName;
            if (medianFitness >= averageFitness) {
                selectorFullName = selector.getName() + " " + selector.getParam2().get();
            } else {
                selectorFullName = selector.getName() + " " + selector.getParam1().get();
            }
            return selectorFullName;
        }
        return selector.getFullName();
    }

    private void printSelectorProbabilities(Map<Selector, Map<PopulationTimingType, Pair<String, Map<Individual, Double>>>> selectorToProbabilities) {
        System.out.println("======= PROBABILITY DISTRIBUTIONS BY SELECTOR =======\n");

        for (Map.Entry<Selector, Map<PopulationTimingType, Pair<String, Map<Individual, Double>>>> selectorEntry : selectorToProbabilities.entrySet()) {


            Map<PopulationTimingType, Pair<String, Map<Individual, Double>>> timingMap = selectorEntry.getValue();
            for (Map.Entry<PopulationTimingType, Pair<String, Map<Individual, Double>>> timingEntry : timingMap.entrySet()) {
                System.out.println("▶ Selector: " + timingEntry.getValue().getKey());
                System.out.println("--------------------------------------------------");
                PopulationTimingType timing = timingEntry.getKey();
                System.out.println("  📂 Timing/File: " + timing.name());

                Map<Individual, Double> individualMap = timingEntry.getValue().getValue();

                // Optional: align columns nicely
                System.out.printf("    %-30s | %s%n", "Individual", "Probability");
                System.out.println("    " + "-".repeat(45));

                individualMap.forEach((individual, probability) ->
                        System.out.printf("    %-30s | %.6f%n", individual, probability)
                );

                System.out.println(); // spacing between timings
            }

            System.out.println("\n==================================================\n");
        }
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
                rwsSelector
                ,
//                powerScalingRws11,
//                powerScalingRws12,
//                powerScalingRws15,
//                powerScalingRws2,
//                powerScalingRws3
//                ,
//                dynamicPowerScalingRws0p9to1p1,
//                dynamicPowerScalingRws0p8to1p2,
//                dynamicPowerScalingRws0p5to1p5,
//                dynamicPowerScalingRws0p5to2
//                ,

                linearScalingRws12
                ,
                linearScalingRws14
                ,
                linearScalingRws15
                ,
                linearScalingRws16
                ,
                linearScalingRws18,
                linearScalingRws2
//                ,
//                averageLinearRws
//                ,
//                medianLinearRws
//                ,
//                maxAvgWorstLinearRws
//                ,

//                sigmaTruncationRws1,
//                sigmaTruncationRws2,
//                sigmaTruncationRws3,
//                sigmaTruncationRws4
//                ,

//                spanMethodRws194
//                ,
//                spanMethodRws300
//                ,
//                spanMethodRws500
//                ,

//                adaptivePowerLawScalingRws194
//                ,
//                adaptivePowerLawScalingRws300
//                ,
//                adaptivePowerLawScalingRws500
//                ,

//                windowScalingRws0
//                ,
//                windowScalingRws1,
//                windowScalingRws2,
//                windowScalingRws10,
//
//                susSelector
//                ,
//                powerScalingSus11
//                ,
//                powerScalingSus12
//                ,
//                powerScalingSus15
//                ,
//                powerScalingSus2,
//                powerScalingSus3
//                ,
//
//                dynamicPowerScalingSus0p9to1p1,
//                dynamicPowerScalingSus0p8to1p2
//                ,
//                dynamicPowerScalingSus0p5to1p5,
//                dynamicPowerScalingSus0p5to2
//                ,
//
//                linearScalingSus12
//                ,
//                linearScalingSus14,
//                linearScalingSus15,
//                linearScalingSus16,
//                linearScalingSus18,
//                linearScalingSus2
//                ,
//
//                maxAvgWorstLinearSus
//                ,
//                averageLinearSus
//                ,
//                medianLinearSus
//                ,
//
//                sigmaTruncationSus1,
//                sigmaTruncationSus2,
//                sigmaTruncationSus3,
//                sigmaTruncationSus4,
////
//                spanMethodSus5000
//                ,
//                adaptivePowerLawScalingSus5000
//                ,
//
//                windowScalingSus0,
//                windowScalingSus1,
//                windowScalingSus2,
//                windowScalingSus10
        );
    }

}
