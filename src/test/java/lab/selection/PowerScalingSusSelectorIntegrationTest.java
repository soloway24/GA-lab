package lab.selection;

import lab.Individual;
import lab.convertor.FitnessToProbabilityConvertor;
import lab.convertor.ProbabilityToExpectedQuantityConvertor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

@ExtendWith(MockitoExtension.class)
class PowerScalingSusSelectorIntegrationTest {

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor();
    private final ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor = new ProbabilityToExpectedQuantityConvertor();

    private final SusSelector susSelector = new SusSelector(fitnessToProbabilityConvertor, probabilityToExpectedQuantityConvertor);
    private final ScalingSelector scalingSelector = new ScalingSelector();
    private final PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, SelectionTestEntities.POWER_SCALING_POWER);
    private final PowerScalingSusSelector powerScalingSusSelector = new PowerScalingSusSelector(powerScalingSelector, susSelector);

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        List<Individual> selected = powerScalingSusSelector.select(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE);
        MatcherAssert.assertThat(selected, Matchers.hasSize(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.size()));

        List<String> binaryCodes = SelectionTestEntities.getBinaryCodes(selected);
        List<String> initialBinaryCodes = SelectionTestEntities.getBinaryCodes(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }
}