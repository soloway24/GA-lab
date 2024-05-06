package lab.selection;

import lab.convertor.ProbabilityToExpectedQuantityConvertor;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static lab.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProbabilityToExpectedQuantityConvertorTest {

    private final ProbabilityToExpectedQuantityConvertor convertor = new ProbabilityToExpectedQuantityConvertor();

    @Test
    public void whenConvertDoubleFitnessThenSuccess() {
        assertThat(convertor.convertToExpectedQuantities(INDIVIDUAL_TO_PROBABILITY).entrySet(),
                containsInAnyOrder(INDIVIDUAL_TO_EXPECTED_QUANTITY.entrySet().toArray()));
    }

    @Test
    public void whenConvertZeroFitnessSumThenFailure() {
        assertThrows(IllegalStateException.class,
                () -> convertor.convertToExpectedQuantities(Map.of(INDIVIDUAL_1, 0.0)));
    }
}