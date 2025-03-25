package lab;

import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import lab.convertor.FitnessToProbabilityConvertor;
import lab.export.SnapshotProbabilityPlotter;
import lab.population.PopulationTimingType;
import lab.selection.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
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

@Component
@RequiredArgsConstructor
public class SnapshotRunner {

    private final SnapshotParser snapshotParser;
    private final SnapshotProbabilityPlotter snapshotProbabilityPlotter;
    private final RwsSelector rwsSelector;
    private final SusSelector susSelector;
    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor;

    public void run() throws InterruptedException, PythonExecutionException, IOException {
        Path basePath = Paths.get("stats_v2/plots/FH/100/RWS/NONE/ONE_OPTIMAL/STANDARD/4/population_snapshots");
        int populationSize = 100;
        List<Selector> selectors = getSelectors();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Map<PopulationTimingType, Map<Individual, Double>> timingToIndividualToFitness = snapshotParser.getTimingToIndividualToFitness(basePath, populationSize);
        Map<Selector, Map<PopulationTimingType, Map<Individual, Double>>> selectorToProbabilities = selectors.stream()
                .collect(toUnmodifiableMap(Function.identity(), selector -> getSelectorScaledProbabilities(selector, timingToIndividualToFitness)));

        printSelectorProbabilities(selectorToProbabilities);
        snapshotProbabilityPlotter.plotSelectorProbabilities(selectorToProbabilities, basePath);

        stopWatch.stop();
        System.out.println("Time Elapsed: " + stopWatch.getTime() / 1000.0);
    }

    private Map<PopulationTimingType, Map<Individual, Double>> getSelectorScaledProbabilities(Selector selector,
                                                                                              Map<PopulationTimingType, Map<Individual, Double>> timingToIndividualToFitness) {
        return timingToIndividualToFitness.entrySet().stream()
                .collect(toUnmodifiableMap(Map.Entry::getKey, entry -> convertToProbabilities(entry.getValue(), selector)));
    }

    private Map<Individual, Double> convertToProbabilities(Map<Individual, Double> individualToFitness,
                                                           Selector selector) {
        Map<Individual, Double> scaledIndividualToFitness = selector.scale(individualToFitness);
        Map<Individual, Double> IndividualToProbability = fitnessToProbabilityConvertor.convertToSelectionProbabilities(scaledIndividualToFitness);
        return IndividualToProbability.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> b, LinkedHashMap::new));
    }

    private void printSelectorProbabilities(Map<Selector, Map<PopulationTimingType, Map<Individual, Double>>> selectorToProbabilities) {
        System.out.println("======= PROBABILITY DISTRIBUTIONS BY SELECTOR =======\n");

        for (Map.Entry<Selector, Map<PopulationTimingType, Map<Individual, Double>>> selectorEntry : selectorToProbabilities.entrySet()) {
            Selector selector = selectorEntry.getKey();
            System.out.println("▶ Selector: " + selector.getFullName());
            System.out.println("--------------------------------------------------");

            Map<PopulationTimingType, Map<Individual, Double>> timingMap = selectorEntry.getValue();
            for (Map.Entry<PopulationTimingType, Map<Individual, Double>> timingEntry : timingMap.entrySet()) {
                PopulationTimingType timing = timingEntry.getKey();
                System.out.println("  📂 Timing/File: " + timing.name());

                Map<Individual, Double> individualMap = timingEntry.getValue();

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
                rwsSelector
//                , susSelector
                , powerScalingRwsSelector
                , powerScalingRwsSelector2
//                , powerScalingSusSelector
//                , powerScalingSusSelector2
                , dynamicPowerScalingRwsSelector0p9to1p1
                , dynamicPowerScalingRwsSelector0p8to1p2
//                , dynamicPowerScalingSusSelector0p9to1p1
//                , dynamicPowerScalingSusSelector0p8to1p2
        );
    }
}
