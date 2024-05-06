package lab.selection;

import lab.Individual;
import lab.convertor.FitnessToProbabilityConvertor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

@ExtendWith(MockitoExtension.class)
class DynamicPowerScalingRwsSelectorIntegrationTest {

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();
    private final RwsSelector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
    private final ScalingSelector scalingSelector = new ScalingSelector();
    private final DynamicPowerScalingSelector dynamicPowerScalingSelector =
            new DynamicPowerScalingSelector(scalingSelector, SelectionTestEntities.POWER_SCALING_POWER_START, SelectionTestEntities.POWER_SCALING_POWER_END);
    private final DynamicPowerScalingRwsSelector dynamicPowerScalingRwsSelector =
            new DynamicPowerScalingRwsSelector(dynamicPowerScalingSelector, rwsSelector);

    @Test
    public void whenSelectWithMedianGreaterThanAverageThenSuccess() {
        List<Individual> selected = dynamicPowerScalingRwsSelector.select(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_MED_G_AVG);
        MatcherAssert.assertThat(selected, Matchers.hasSize(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_MED_G_AVG.size()));

        List<String> binaryCodes = SelectionTestEntities.getBinaryCodes(selected);
        List<String> initialBinaryCodes = SelectionTestEntities.getBinaryCodes(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_MED_G_AVG.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }

    @Test
    public void whenSelectWithMedianLessThanAverageThenSuccess() {
        List<Individual> selected = dynamicPowerScalingRwsSelector.select(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE);
        MatcherAssert.assertThat(selected, Matchers.hasSize(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.size()));

        List<String> binaryCodes = SelectionTestEntities.getBinaryCodes(selected);
        List<String> initialBinaryCodes = SelectionTestEntities.getBinaryCodes(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }

    @Test
    public void whenSelectWithZeroFitnessSumThenSuccess() {
        List<Individual> selected = dynamicPowerScalingRwsSelector.select(Map.of(SelectionTestEntities.INDIVIDUAL_1, 0.0));
        List<String> binaryCodes = SelectionTestEntities.getBinaryCodes(selected);
        MatcherAssert.assertThat(binaryCodes, Matchers.contains(SelectionTestEntities.INDIVIDUAL_1.getBinaryCode()));
    }
}