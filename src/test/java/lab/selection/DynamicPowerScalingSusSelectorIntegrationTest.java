package lab.selection;

import lab.Individual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static lab.selection.SelectionTestEntities.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles(value = "test")
@ExtendWith(MockitoExtension.class)
class DynamicPowerScalingSusSelectorIntegrationTest {

    @Autowired
    private SusSelector susSelector;
    @Autowired
    private ScalingSelector scalingSelector;

    private DynamicPowerScalingSusSelector dynamicPowerScalingSusSelector;

    @BeforeEach
    void setUp() {
        DynamicPowerScalingSelector dynamicPowerScalingSelector = new DynamicPowerScalingSelector(scalingSelector, POWER_SCALING_POWER_START, POWER_SCALING_POWER_END);
        dynamicPowerScalingSusSelector =
                new DynamicPowerScalingSusSelector(dynamicPowerScalingSelector, susSelector);
    }

    @Test
    public void whenSelectWithMedianGreaterThanAverageThenSuccess() {
        List<Individual> selected = dynamicPowerScalingSusSelector.select(INDIVIDUAL_TO_FITNESS_MED_G_AVG);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_MED_G_AVG.size()));

        List<String> binaryCodes = getBinaryCodes(selected);
        List<String> initialBinaryCodes = getBinaryCodes(INDIVIDUAL_TO_FITNESS_MED_G_AVG.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }

    @Test
    public void whenSelectWithMedianLessThanAverageThenSuccess() {
        List<Individual> selected = dynamicPowerScalingSusSelector.select(INDIVIDUAL_TO_FITNESS_DOUBLE);
        assertThat(selected, hasSize(INDIVIDUAL_TO_FITNESS_DOUBLE.size()));

        List<String> binaryCodes = getBinaryCodes(selected);
        List<String> initialBinaryCodes = getBinaryCodes(INDIVIDUAL_TO_FITNESS_DOUBLE.keySet());

        binaryCodes.forEach(individual -> assertThat(initialBinaryCodes, hasItem(individual)));
    }

    @Test
    public void whenSelectWithZeroFitnessSumThenSuccess() {
        List<Individual> selected = dynamicPowerScalingSusSelector.select(Map.of(INDIVIDUAL_1, 0.0));
        List<String> binaryCodes = getBinaryCodes(selected);
        assertThat(binaryCodes, contains(INDIVIDUAL_1.getBinaryCode()));
    }
}