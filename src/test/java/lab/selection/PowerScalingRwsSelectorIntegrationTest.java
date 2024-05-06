package lab.selection;

import lab.Individual;
import lab.convertor.FitnessToProbabilityConvertor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

@ExtendWith(MockitoExtension.class)
class PowerScalingRwsSelectorIntegrationTest {

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();

    private final RwsSelector rwsSelector = new RwsSelector(fitnessToProbabilityConvertor);
    private final ScalingSelector scalingSelector = new ScalingSelector();
    private final PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, SelectionTestEntities.POWER_SCALING_POWER);
    private final PowerScalingRwsSelector powerScalingRwsSelector = new PowerScalingRwsSelector(powerScalingSelector, rwsSelector);

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        List<Individual> selected = powerScalingRwsSelector.select(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE);
        MatcherAssert.assertThat(selected, Matchers.hasSize(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.size()));

        List<String> binaryCodes = SelectionTestEntities.getBinaryCodes(selected);
        List<String> initialBinaryCodes = SelectionTestEntities.getBinaryCodes(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }
}