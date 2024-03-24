package lab.v2.selection;

import lab.model.Individual;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lab.v2.selection.SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE;
import static lab.v2.selection.SelectionTestEntities.POWER_SCALING_POWER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
class PowerScalingRwsSelectorIntegrationTest {

    private final FitnessToProbabilityConvertor<Double> fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor<>();

    private final RwsSelector<Double> rwsSelector = new RwsSelector<>(fitnessToProbabilityConvertor);
    private final ScalingSelector<Double> scalingSelector = new ScalingSelector<>();
    private final PowerScalingSelector<Double> powerScalingSelector = new PowerScalingSelector<>(scalingSelector, POWER_SCALING_POWER);
    private final PowerScalingRwsSelector<Double> powerScalingRwsSelector = new PowerScalingRwsSelector<>(powerScalingSelector, rwsSelector);

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        List<Individual> selected = powerScalingRwsSelector.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet(), hasItem(individual)));
    }
}