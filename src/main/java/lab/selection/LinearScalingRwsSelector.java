package lab.selection;

import lab.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;

@RequiredArgsConstructor
public class LinearScalingRwsSelector implements Selector {

    private final LinearScalingSelector linearScalingSelector;
    private final RwsSelector rwsSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.RWS;
    }

    @Override
    public String getName() {
        return "LS RWS";
    }

    @Override
    public String getFullName() {
        return "LS RWS " + linearScalingSelector.getPs();
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.of(valueOf(linearScalingSelector.getPs()));
    }

    @Override
    public Optional<String> getParam2() {
        return Optional.empty();
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return linearScalingSelector.select(individualToFitness, rwsSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return linearScalingSelector.scale(individualToFitness);
    }
}