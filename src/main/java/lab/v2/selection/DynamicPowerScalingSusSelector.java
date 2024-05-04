package lab.v2.selection;

import lab.v2.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;
import static lab.v2.selection.SelectorType.SUS;

@RequiredArgsConstructor
public class DynamicPowerScalingSusSelector implements Selector {

    private final DynamicPowerScalingSelector dynamicPowerScalingSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SUS;
    }

    @Override
    public String getName() {
        return "DPS SUS";
    }

    @Override
    public String getFullName() {
        return "DPS SUS "
                + dynamicPowerScalingSelector.getStartPower()
                + " - "
                + dynamicPowerScalingSelector.getEndPower();
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.of(valueOf(dynamicPowerScalingSelector.getStartPower()));

    }

    @Override
    public Optional<String> getParam2() {
        return Optional.of(valueOf(dynamicPowerScalingSelector.getEndPower()));
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return dynamicPowerScalingSelector.select(individualToFitness, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return dynamicPowerScalingSelector.scale(individualToFitness);
    }

}
