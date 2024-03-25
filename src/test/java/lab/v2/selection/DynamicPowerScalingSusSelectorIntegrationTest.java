package lab.v2.selection;

import lab.model.Individual;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.convertor.ProbabilityToExpectedQuantityConvertor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static lab.v2.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class DynamicPowerScalingSusSelectorIntegrationTest {

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();
    private final ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor = new ProbabilityToExpectedQuantityConvertor();

    private final SusSelector susSelector = new SusSelector(fitnessToProbabilityConvertor, probabilityToExpectedQuantityConvertor);
    private final ScalingSelector scalingSelector = new ScalingSelector();
    private final DynamicPowerScalingSelector dynamicPowerScalingSelector =
            new DynamicPowerScalingSelector(scalingSelector, POWER_SCALING_POWER_START, POWER_SCALING_POWER_END);
    private final DynamicPowerScalingSusSelector dynamicPowerScalingSusSelector =
            new DynamicPowerScalingSusSelector(dynamicPowerScalingSelector, susSelector);

    @Test
    public void whenSelectWithMedianGreaterThanAverageThenSuccess() {
        List<Individual> selected = dynamicPowerScalingSusSelector.select(INDIVIDUAL_TO_FITNESS_MED_G_AVG);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_MED_G_AVG.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_MED_G_AVG.keySet(), hasItem(individual)));
    }

    @Test
    public void whenSelectWithMedianLessThanAverageThenSuccess() {
        List<Individual> selected = dynamicPowerScalingSusSelector.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet(), hasItem(individual)));
    }

    @Test
    public void whenSelectWithZeroFitnessSumThenSuccess() {
        List<Individual> selected = dynamicPowerScalingSusSelector.select(Map.of(INDIVIDUAL_1, 0.0));
        assertThat(selected, contains(INDIVIDUAL_1));
    }
}