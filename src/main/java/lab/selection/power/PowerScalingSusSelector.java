package lab.selection.power;

import lab.Individual;
import lab.selection.SelectionContext;
import lab.selection.Selector;
import lab.selection.SelectorType;
import lab.selection.SusSelector;
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
    public List<Individual> select(SelectionContext selectionContext) {
        return powerScalingSelector.select(selectionContext, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return powerScalingSelector.scale(selectionContext);
    }

}
