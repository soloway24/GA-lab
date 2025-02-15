package lab.selection;

import lab.Individual;
import lab.convertor.FitnessToProbabilityConvertor;
import lab.convertor.ProbabilityToExpectedQuantityConvertor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import static lab.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SusSelectorTest {

    @Mock
    private FitnessToProbabilityConvertor fitnessToProbabilityConvertor;
    @Mock
    private ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor;
    @Mock
    private Random random;
    @InjectMocks
    private SusSelector susSelector;

    @Test
    public void whenSelectWithIntFitnessThenSuccess() {
        when(fitnessToProbabilityConvertor.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_INT)).thenReturn(INDIVIDUAL_TO_PROBABILITY);
        when(probabilityToExpectedQuantityConvertor.convertToExpectedQuantities(INDIVIDUAL_TO_PROBABILITY)).thenReturn(INDIVIDUAL_TO_EXPECTED_QUANTITY);
        when(random.nextDouble()).thenReturn(0.5);

        List<Individual> selected = susSelector.select(INDIVIDUAL_TO_FITNESS_INT);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_INT.size()));

        List<String> binaryCodes = getBinaryCodes(selected);
        List<String> initialBinaryCodes = getBinaryCodes(INDIVIDUAL_TO_FITNESS_INT.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));

        INDIVIDUAL_TO_EXPECTED_QUANTITY.entrySet().forEach(entry -> assertContainsIndividualIfShouldBeSelected(selected, entry));
    }

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        when(fitnessToProbabilityConvertor.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_DOUBLE)).thenReturn(INDIVIDUAL_TO_PROBABILITY);
        when(probabilityToExpectedQuantityConvertor.convertToExpectedQuantities(INDIVIDUAL_TO_PROBABILITY)).thenReturn(INDIVIDUAL_TO_EXPECTED_QUANTITY);
        when(random.nextDouble()).thenReturn(0.5);

        List<Individual> selected = susSelector.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));

        List<String> binaryCodes = getBinaryCodes(selected);
        List<String> initialBinaryCodes = getBinaryCodes(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));

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
                .filter(individual -> individual.getBinaryCode().equals(expected.getBinaryCode()))
                .toList()
                .size();
    }

}