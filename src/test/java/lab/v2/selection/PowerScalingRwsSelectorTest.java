package lab.v2.selection;

import lab.model.Individual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static lab.v2.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PowerScalingRwsSelectorTest {

    private static final double POWER = 1.1;
    private static final double DEFAULT_FITNESS = 0.0001;
    private static final Map<Individual, Double> INDIVIDUAL_TO_SCALED_FITNESS = Map.of(
            INDIVIDUAL_1, 2.1435469250725863d,
            INDIVIDUAL_2, 12.589254117941675d,
            INDIVIDUAL_3, 26.985656953471274d,
            INDIVIDUAL_4, 9.849155306759332d
    );
    private static final List<Individual> EXPECTED_SELECTED_INDIVIDUALS = List.of(INDIVIDUAL_2, INDIVIDUAL_3, INDIVIDUAL_3, INDIVIDUAL_4);
    private static final List<Individual> EXPECTED_SELECTED_INDIVIDUALS_DEFAULT = List.of(INDIVIDUAL_1);

    @Mock
    private RwsSelector<Double> rwsSelector;

    private PowerScalingRwsSelector<Integer> powerScalingRwsSelectorInt;
    private PowerScalingRwsSelector<Double> powerScalingRwsSelectorDouble;

    @BeforeEach
    public void init() {
        powerScalingRwsSelectorInt = new PowerScalingRwsSelector<>(rwsSelector, POWER);
        powerScalingRwsSelectorDouble = new PowerScalingRwsSelector<>(rwsSelector, POWER);
    }

    @Test
    public void whenSelectWithIntFitnessThenSuccess() {
        when(rwsSelector.select(INDIVIDUAL_TO_SCALED_FITNESS)).thenReturn(EXPECTED_SELECTED_INDIVIDUALS);

        List<Individual> selected = powerScalingRwsSelectorInt.select(INDIVIDUAL_TO_FITNESS_INT);
        assertThat(selected, equalTo(EXPECTED_SELECTED_INDIVIDUALS));
    }

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        when(rwsSelector.select(INDIVIDUAL_TO_SCALED_FITNESS)).thenReturn(EXPECTED_SELECTED_INDIVIDUALS);

        List<Individual> selected = powerScalingRwsSelectorDouble.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, equalTo(EXPECTED_SELECTED_INDIVIDUALS));
    }

    @Test
    public void whenSelectWithZeroIntFitnessThenSuccess() {
        when(rwsSelector.select(Map.of(INDIVIDUAL_1, DEFAULT_FITNESS))).thenReturn(EXPECTED_SELECTED_INDIVIDUALS_DEFAULT);

        List<Individual> selected = powerScalingRwsSelectorInt.select(Map.of(INDIVIDUAL_1, 0));
        assertThat(selected, equalTo(EXPECTED_SELECTED_INDIVIDUALS_DEFAULT));
    }

    @Test
    public void whenSelectWithZeroDoubleFitnessThenSuccess() {
        when(rwsSelector.select(Map.of(INDIVIDUAL_1, DEFAULT_FITNESS))).thenReturn(EXPECTED_SELECTED_INDIVIDUALS_DEFAULT);

        List<Individual> selected = powerScalingRwsSelectorDouble.select(Map.of(INDIVIDUAL_1, 0.0));
        assertThat(selected, equalTo(EXPECTED_SELECTED_INDIVIDUALS_DEFAULT));
    }

}