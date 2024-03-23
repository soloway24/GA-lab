package lab.v2.selection;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static lab.v2.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FitnessToPercentageConvertorTest {

    private final FitnessToPercentageConvertor<Integer> convertorInt = new FitnessToPercentageConvertor<>();
    private final FitnessToPercentageConvertor<Double> convertorDouble = new FitnessToPercentageConvertor<>();

    @Test
    public void whenConvertIntFitnessThenSuccess() {
        assertThat(convertorInt.convertToSelectionPercentages(INDIVIDUAL_TO_FITNESS_INT).entrySet(),
                containsInAnyOrder(INDIVIDUAL_TO_PERCENTAGE.entrySet().toArray()));
    }

    @Test
    public void whenConvertDoubleFitnessThenSuccess() {
        assertThat(convertorDouble.convertToSelectionPercentages(INDIVIDUAL_TO_FITNESS_DOUBLE).entrySet(),
                containsInAnyOrder(INDIVIDUAL_TO_PERCENTAGE.entrySet().toArray()));
    }

    @Test
    public void whenConvertEmptyIndividualsThenFailure() {
        assertThrows(IllegalStateException.class,
                () -> convertorDouble.convertToSelectionPercentages(Map.of()));
    }

    @Test
    public void whenConvertZeroFitnessSumThenFailure() {
        assertThrows(IllegalStateException.class,
                () -> convertorDouble.convertToSelectionPercentages(Map.of(INDIVIDUAL_1, 0.0)));
    }
}