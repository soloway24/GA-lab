package lab.v2.selection;

import lab.v2.Individual;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lab.v2.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
class PowerScalingRwsSelectorIntegrationTest {

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();

    private final RwsSelector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
    private final ScalingSelector scalingSelector = new ScalingSelector();
    private final PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, POWER_SCALING_POWER);
    private final PowerScalingRwsSelector powerScalingRwsSelector = new PowerScalingRwsSelector(powerScalingSelector, rwsSelector);

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        List<Individual> selected = powerScalingRwsSelector.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));

        List<String> binaryCodes = getBinaryCodes(selected);
        List<String> initialBinaryCodes = getBinaryCodes(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }
}