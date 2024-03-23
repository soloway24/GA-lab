package lab.v2.selection;

import lab.model.Individual;
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
    private FitnessToPercentageConvertor<Integer> convertorInt;
    @Mock
    private FitnessToPercentageConvertor<Double> convertorDouble;

    private RwsSelector<Integer> rwsSelector1;
    private RwsSelector<Double> rwsSelector2;

    @BeforeEach
    public void init() {
        rwsSelector1 = new RwsSelector<>(convertorInt);
        rwsSelector2 = new RwsSelector<>(convertorDouble);
    }

    @Test
    public void whenSelectWithIntFitnessThenSuccess() {
        when(convertorInt.convertToSelectionPercentages(INDIVIDUAL_TO_FITNESS_INT)).thenReturn(INDIVIDUAL_TO_PERCENTAGE);

        List<Individual> selected = rwsSelector1.select(INDIVIDUAL_TO_FITNESS_INT);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_INT.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_INT.keySet(), hasItem(individual)));
    }

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        when(convertorDouble.convertToSelectionPercentages(INDIVIDUAL_TO_FITNESS_DOUBLE)).thenReturn(INDIVIDUAL_TO_PERCENTAGE);

        List<Individual> selected = rwsSelector2.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet(), hasItem(individual)));
    }

}