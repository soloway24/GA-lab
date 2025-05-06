package lab.selection;

import lab.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class DynamicLinearScalingSusSelector implements Selector {

    private final DynamicLinearScalingSelector dynamicLinearScalingSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.SUS;
    }

    @Override
    public String getName() {
        return "DLS SUS";
    }

    @Override
    public String getFullName() {
        return "DLS SUS " + dynamicLinearScalingSelector.getDynamicLinearScaler().name();
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.of(dynamicLinearScalingSelector.getDynamicLinearScaler().name());
    }

    @Override
    public Optional<String> getParam2() {
        return Optional.empty();
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return dynamicLinearScalingSelector.select(individualToFitness, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return dynamicLinearScalingSelector.scale(individualToFitness);
    }

}