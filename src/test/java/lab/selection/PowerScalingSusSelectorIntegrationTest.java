package lab.selection;

import lab.Individual;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest
@ActiveProfiles(value = "test")
@ExtendWith(MockitoExtension.class)
class PowerScalingSusSelectorIntegrationTest {

    @Autowired
    private SusSelector susSelector;
    @Autowired
    private ScalingSelector scalingSelector;

    private PowerScalingSusSelector powerScalingSusSelector;

    @BeforeEach
    void setUp() {
        PowerScalingSelector powerScalingSelector = new PowerScalingSelector(scalingSelector, SelectionTestEntities.POWER_SCALING_POWER);
        powerScalingSusSelector = new PowerScalingSusSelector(powerScalingSelector, susSelector);
    }

    @Test
    public void whenSelectWithDoubleFitnessThenSuccess() {
        List<Individual> selected = powerScalingSusSelector.select(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE);
        MatcherAssert.assertThat(selected, Matchers.hasSize(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.size()));

        List<String> binaryCodes = SelectionTestEntities.getBinaryCodes(selected);
        List<String> initialBinaryCodes = SelectionTestEntities.getBinaryCodes(SelectionTestEntities.INDIVIDUAL_TO_FITNESS_DOUBLE.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }
}