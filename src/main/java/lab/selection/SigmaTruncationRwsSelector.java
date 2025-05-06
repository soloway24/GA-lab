package lab.selection;

import lab.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;

@RequiredArgsConstructor
public class SigmaTruncationRwsSelector implements Selector {

    private final SigmaTruncationSelector sigmaTruncationSelector;
    private final RwsSelector rwsSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.RWS;
    }

    @Override
    public String getName() {
        return "ST RWS";
    }

    @Override
    public String getFullName() {
        return "ST RWS " + sigmaTruncationSelector.getC();
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.of(valueOf(sigmaTruncationSelector.getC()));
    }

    @Override
    public Optional<String> getParam2() {
        return Optional.empty();
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        return sigmaTruncationSelector.select(individualToFitness, rwsSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(Map<Individual, ? extends Number> individualToFitness) {
        return sigmaTruncationSelector.scale(individualToFitness);
    }
}