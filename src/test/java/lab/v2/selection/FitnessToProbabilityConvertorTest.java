package lab.v2.selection;

import lab.v2.convertor.FitnessToProbabilityConvertor;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static lab.v2.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FitnessToProbabilityConvertorTest {

    private final FitnessToProbabilityConvertor<Integer> convertorInt = new FitnessToProbabilityConvertor<>();
    private final FitnessToProbabilityConvertor<Double> convertorDouble = new FitnessToProbabilityConvertor<>();

    @Test
    public void whenConvertIntFitnessThenSuccess() {
        assertThat(convertorInt.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_INT).entrySet(),
                containsInAnyOrder(INDIVIDUAL_TO_PROBABILITY.entrySet().toArray()));
    }

    @Test
    public void whenConvertDoubleFitnessThenSuccess() {
        assertThat(convertorDouble.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_DOUBLE).entrySet(),
                containsInAnyOrder(INDIVIDUAL_TO_PROBABILITY.entrySet().toArray()));
    }

    @Test
    public void whenConvertEmptyIndividualsThenFailure() {
        assertThrows(IllegalStateException.class,
                () -> convertorDouble.convertToSelectionProbabilities(Map.of()));
    }

    @Test
    public void whenConvertZeroFitnessSumThenFailure() {
        assertThrows(IllegalStateException.class,
                () -> convertorDouble.convertToSelectionProbabilities(Map.of(INDIVIDUAL_1, 0.0)));
    }
}