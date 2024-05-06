package lab.selection;

import lab.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;

@RequiredArgsConstructor
public class PowerScalingSusSelector implements Selector {

    private final PowerScalingSelector powerScalingSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.SUS;
    }

    @Override
    public String getName() {
        return "PS SUS";
    }

    @Override
    public String getFullName() {
        return "PS SUS " + powerScalingSelector.getPower();
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.of(valueOf(powerScalingSelector.getPower()));
    }

    @Override
    public Optional<String> getParam2() {
        return Optional.empty();
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return powerScalingSelector.select(individualToFitness, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return powerScalingSelector.scale(individualToFitness);
    }

}
