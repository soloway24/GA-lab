package lab.selection;

import lab.Individual;
import lab.convertor.FitnessToProbabilityConvertor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RwsSelectorTest {

    @Mock
    private FitnessToProbabilityConvertor convertor;
    private RwsSelector rwsSelector;

    @BeforeEach
    public void init() {
        rwsSelector = new RwsSelector(convertor);
    }

    @Test
    public void whenSelectWithIntFitnessThenSuccess() {
        when(convertor.convertToSelectionProbabilities(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_INT)).thenReturn(SelectionTestEntities.INDIVIDUAL_TO_PROBABILITY);

        List<Individual> selected = rwsSelector.select(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_INT);
        MatcherAssert.assertThat(selected, Matchers.hasSize(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_INT.size()));

        List<String> binaryCodes = SelectionTestEntities.getBinaryCodes(selected);
        List<String> initialBinaryCodes = SelectionTestEntities.getBinaryCodes(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_INT.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        when(convertor.convertToSelectionProbabilities(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE)).thenReturn(SelectionTestEntities.INDIVIDUAL_TO_PROBABILITY);

        List<Individual> selected = rwsSelector.select(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE);
        MatcherAssert.assertThat(selected, Matchers.hasSize(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.size()));

        List<String> binaryCodes = SelectionTestEntities.getBinaryCodes(selected);
        List<String> initialBinaryCodes = SelectionTestEntities.getBinaryCodes(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }

}