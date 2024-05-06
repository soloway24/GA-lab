package lab.selection;

import lab.convertor.FitnessToProbabilityConvertor;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static lab.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FitnessToProbabilityConvertorTest {

    private final FitnessToProbabilityConvertor convertor = new FitnessToProbabilityConvertor();

    @Test
    public void whenConvertIntFitnessThenSuccess() {
        assertThat(convertor.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_INT).entrySet(),
                containsInAnyOrder(INDIVIDUAL_TO_PROBABILITY.entrySet().toArray()));
    }

    @Test
    public void whenConvertDoubleFitnessThenSuccess() {
        assertThat(convertor.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_DOUBLE).entrySet(),
                containsInAnyOrder(INDIVIDUAL_TO_PROBABILITY.entrySet().toArray()));
    }

    @Test
    public void whenConvertEmptyIndividualsThenFailure() {
        assertThrows(IllegalStateException.class,
                () -> convertor.convertToSelectionProbabilities(Map.of()));
    }

    @Test
    public void whenConvertZeroFitnessSumThenFailure() {
        assertThrows(IllegalStateException.class,
                () -> convertor.convertToSelectionProbabilities(Map.of(INDIVIDUAL_1, 0.0)));
    }
}