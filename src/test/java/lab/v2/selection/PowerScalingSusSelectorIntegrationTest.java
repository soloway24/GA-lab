package lab.v2.selection;

import lab.model.Individual;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.convertor.ProbabilityToExpectedQuantityConvertor;
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
class PowerScalingSusSelectorIntegrationTest {

    private final FitnessToProbabilityConvertor<Double> fitnessToProbabilityConvertor = new FitnessToProbabilityConvertor<>();
    private final ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor = new ProbabilityToExpectedQuantityConvertor();

    private final SusSelector<Double> susSelector = new SusSelector<>(fitnessToProbabilityConvertor, probabilityToExpectedQuantityConvertor);
    private final ScalingSelector<Double> scalingSelector = new ScalingSelector<>();
    private final PowerScalingSelector<Double> powerScalingSelector = new PowerScalingSelector<>(scalingSelector, POWER_SCALING_POWER);
    private final PowerScalingSusSelector<Double> powerScalingSusSelector = new PowerScalingSusSelector<>(powerScalingSelector, susSelector);

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        List<Individual> selected = powerScalingSusSelector.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));
        selected.forEach(individual -> assertThat(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet(), hasItem(individual)));
    }
}