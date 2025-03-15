package lab.population;

import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static lab.population.PopulationTimingType.*;

@Component
public class PopulationTimingTypeIdentifier {

    private static final Map<PopulationTimingType, TriFunction<Integer, Double, Double, Boolean>> TIMING_TYPE_TO_IDENTIFIER =
            Map.of(
                    INITIAL, PopulationTimingTypeIdentifier::initialIdentifier,
                    AVERAGE, PopulationTimingTypeIdentifier::averageIdentifier,
                    CONVERGING, PopulationTimingTypeIdentifier::convergingIdentifier
            );

    public boolean isTiming(PopulationTimingType timingType, int iteration, double avgF, double maxF) {
        return Optional.ofNullable(timingType)
                .map(TIMING_TYPE_TO_IDENTIFIER::get)
                .map(identifier -> identifier.apply(iteration, avgF, maxF))
                .orElse(false);
    }

    private static boolean initialIdentifier(int iteration, double avgF, double maxF) {
        return iteration == 0;
    }

    private static boolean averageIdentifier(int iteration, double avgF, double maxF) {
        double fraction = avgF / maxF;
        return 0.47 <= fraction && fraction <= 0.53;
    }

    private static boolean convergingIdentifier(int iteration, double avgF, double maxF) {
        return avgF / maxF > 0.8;
    }
}
