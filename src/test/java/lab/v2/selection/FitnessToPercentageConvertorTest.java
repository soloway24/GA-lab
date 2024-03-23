package lab.v2.selection;

import lab.model.Individual;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FitnessToPercentageConvertorTest {

    private static final Individual INDIVIDUAL_1 = new Individual("0000000001");
    private static final Individual INDIVIDUAL_2 = new Individual("0000000010");
    private static final Individual INDIVIDUAL_3 = new Individual("0000000011");
    private static final Individual INDIVIDUAL_4 = new Individual("0000000100");
    private static final Map<Individual, Integer> INDIVIDUAL_TO_FITNESS_INT = Map.of(
            INDIVIDUAL_1, 2,
            INDIVIDUAL_2, 10,
            INDIVIDUAL_3, 20,
            INDIVIDUAL_4, 8
    );
    private static final Map<Individual, Double> INDIVIDUAL_TO_FITNESS_DOUBLE = Map.of(
            INDIVIDUAL_1, 2.0,
            INDIVIDUAL_2, 10.0,
            INDIVIDUAL_3, 20.0,
            INDIVIDUAL_4, 8.0
    );
    private static final Map<Individual, Double> INDIVIDUAL_TO_PERCENTAGE = Map.of(
            INDIVIDUAL_1, 0.05,
            INDIVIDUAL_2, 0.25,
            INDIVIDUAL_3, 0.5,
            INDIVIDUAL_4, 0.2
    );

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