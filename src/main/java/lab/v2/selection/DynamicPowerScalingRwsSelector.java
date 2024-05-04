package lab.v2.selection;

import lab.v2.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;
import static lab.v2.selection.SelectorType.RWS;

@RequiredArgsConstructor
public class DynamicPowerScalingRwsSelector implements Selector {

    private final DynamicPowerScalingSelector dynamicPowerScalingSelector;
    private final RwsSelector rwsSelector;

    @Override
    public SelectorType getSelectorType() {
        return RWS;
    }

    @Override
    public String getName() {
        return "DPS RWS";
    }

    @Override
    public String getFullName() {
        return "DPS RWS "
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
        return dynamicPowerScalingSelector.select(individualToFitness, rwsSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return dynamicPowerScalingSelector.scale(individualToFitness);
    }

}
