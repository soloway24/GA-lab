package lab.v2.selection;

import lab.model.Individual;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.convertor.ProbabilityToExpectedQuantityConvertor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map.Entry;

import static lab.v2.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SusSelectorTest {

    @Mock
    private FitnessToProbabilityConvertor<Integer> fitnessToProbabilityConvertorInt;
    @Mock
    private FitnessToProbabilityConvertor<Double> fitnessToProbabilityConvertorDouble;
    @Mock
    private ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor;

    private SusSelector<Integer> susSelectorInt;
    private SusSelector<Double> susSelectorDouble;

    @BeforeEach
    public void init() {
        susSelectorInt = new SusSelector<>(fitnessToProbabilityConvertorInt, probabilityToExpectedQuantityConvertor);
        susSelectorDouble = new SusSelector<>(fitnessToProbabilityConvertorDouble, probabilityToExpectedQuantityConvertor);
    }

    @Test
    public void whenSelectWithIntFitnessThenSuccess() {
        when(fitnessToProbabilityConvertorInt.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_INT)).thenReturn(INDIVIDUAL_TO_PROBABILITY);
        when(probabilityToExpectedQuantityConvertor.convertToExpectedQuantities(INDIVIDUAL_TO_PROBABILITY)).thenReturn(INDIVIDUAL_TO_EXPECTED_QUANTITY);

        List<Individual> selected = susSelectorInt.select(INDIVIDUAL_TO_FITNESS_INT);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_INT.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_INT.keySet(), hasItem(individual)));

        INDIVIDUAL_TO_EXPECTED_QUANTITY.entrySet().forEach(entry -> assertContainsIndividualIfShouldBeSelected(selected, entry));
    }

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        when(fitnessToProbabilityConvertorDouble.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_DOUBLE)).thenReturn(INDIVIDUAL_TO_PROBABILITY);
        when(probabilityToExpectedQuantityConvertor.convertToExpectedQuantities(INDIVIDUAL_TO_PROBABILITY)).thenReturn(INDIVIDUAL_TO_EXPECTED_QUANTITY);

        List<Individual> selected = susSelectorDouble.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet(), hasItem(individual)));

        INDIVIDUAL_TO_EXPECTED_QUANTITY.entrySet().forEach(entry -> assertContainsIndividualIfShouldBeSelected(selected, entry));
    }

    private void assertContainsIndividualIfShouldBeSelected(List<Individual> selected, Entry<Individual, Double> entry) {
        int flooredExpectedQuantity = entry.getValue().intValue();
        if (flooredExpectedQuantity >= 1) {
            int actualQuantity = getSelectedQuantity(selected, entry.getKey());
            assertThat(actualQuantity, equalTo(flooredExpectedQuantity));
        }
    }

    private int getSelectedQuantity(List<Individual> selected, Individual expected) {
        return selected.stream()
                .filter(individual -> individual.equals(expected))
                .toList()
                .size();
    }

}