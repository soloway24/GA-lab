package lab.v2.selection;

import lab.v2.Individual;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lab.v2.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
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
        when(convertor.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_INT)).thenReturn(INDIVIDUAL_TO_PROBABILITY);

        List<Individual> selected = rwsSelector.select(INDIVIDUAL_TO_FITNESS_INT);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_INT.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_INT.keySet(), hasItem(individual)));
    }

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        when(convertor.convertToSelectionProbabilities(INDIVIDUAL_TO_FITNESS_DOUBLE)).thenReturn(INDIVIDUAL_TO_PROBABILITY);

        List<Individual> selected = rwsSelector.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet(), hasItem(individual)));
    }

}