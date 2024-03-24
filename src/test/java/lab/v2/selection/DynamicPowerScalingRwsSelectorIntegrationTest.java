package lab.v2.selection;

import lab.model.Individual;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static lab.v2.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class DynamicPowerScalingRwsSelectorIntegrationTest {

    private final FitnessToProbabilityConvertor<Double> fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor<>();
    private final RwsSelector<Double> rwsSelector = new RwsSelector<>(fitnessToProbabilityConvertor);
    private final ScalingSelector<Double> scalingSelector = new ScalingSelector<>();
    private final DynamicPowerScalingSelector<Double> dynamicPowerScalingSelector =
            new DynamicPowerScalingSelector<>(scalingSelector, POWER_SCALING_POWER_START, POWER_SCALING_POWER_END);
    private final DynamicPowerScalingRwsSelector<Double> dynamicPowerScalingRwsSelector =
            new DynamicPowerScalingRwsSelector<>(dynamicPowerScalingSelector, rwsSelector);

    @Test
    public void whenSelectWithMedianGreaterThanAverageThenSuccess() {
        List<Individual> selected = dynamicPowerScalingRwsSelector.select(INDIVIDUAL_TO_FITNESS_MED_G_AVG);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_MED_G_AVG.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_MED_G_AVG.keySet(), hasItem(individual)));
    }

    @Test
    public void whenSelectWithMedianLessThanAverageThenSuccess() {
        List<Individual> selected = dynamicPowerScalingRwsSelector.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet(), hasItem(individual)));
    }

    @Test
    public void whenSelectWithZeroFitnessSumThenSuccess() {
        List<Individual> selected = dynamicPowerScalingRwsSelector.select(Map.of(INDIVIDUAL_1, 0.0));
        assertThat(selected, contains(INDIVIDUAL_1));
    }
}