package lab.selection;

import lab.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;

@RequiredArgsConstructor
public class PowerScalingRwsSelector implements Selector {

    private final PowerScalingSelector powerScalingSelector;
    private final RwsSelector rwsSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.RWS;
    }

    @Override
    public String getName() {
        return "PS RWS";
    }

    @Override
    public String getFullName() {
        return "PS RWS " + powerScalingSelector.getPower();
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
        return powerScalingSelector.select(individualToFitness, rwsSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return powerScalingSelector.scale(individualToFitness);
    }
}
