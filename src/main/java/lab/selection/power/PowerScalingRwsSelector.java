package lab.selection.power;

import lab.Individual;
import lab.selection.RwsSelector;
import lab.selection.SelectionContext;
import lab.selection.Selector;
import lab.selection.SelectorType;
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
    public List<Individual> select(SelectionContext selectionContext) {
        return powerScalingSelector.select(selectionContext, rwsSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return powerScalingSelector.scale(selectionContext);
    }
}
