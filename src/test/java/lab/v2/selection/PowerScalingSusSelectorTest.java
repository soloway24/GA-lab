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
class PowerScalingSusSelectorTest {

    @Mock
    private SusSelector<Double> susSelector;

    private PowerScalingSusSelector<Double> powerScalingSusSelectorDouble;

    @BeforeEach
    public void init() {
        powerScalingSusSelectorDouble = new PowerScalingSusSelector<>(susSelector, POWER_SCALING_POWER);
    }

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        when(susSelector.select(INDIVIDUAL_TO_SCALED_FITNESS)).thenReturn(PS_EXPECTED_SELECTED_INDIVIDUALS);

        List<Individual> selected = powerScalingSusSelectorDouble.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, equalTo(PS_EXPECTED_SELECTED_INDIVIDUALS));
    }

    @Test
    public void whenSelectWithZeroDoubleFitnessThenSuccess() {
        when(susSelector.select(Map.of(INDIVIDUAL_1, DEFAULT_SCALED_FITNESS))).thenReturn(PS_EXPECTED_SELECTED_INDIVIDUALS_DEFAULT);

        List<Individual> selected = powerScalingSusSelectorDouble.select(Map.of(INDIVIDUAL_1, 0.0));
        assertThat(selected, equalTo(PS_EXPECTED_SELECTED_INDIVIDUALS_DEFAULT));
    }
}