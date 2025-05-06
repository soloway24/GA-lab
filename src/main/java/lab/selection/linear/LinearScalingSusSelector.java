package lab.selection.linear;

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
public class LinearScalingSusSelector implements Selector {

    private final LinearScalingSelector linearScalingSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.SUS;
    }

    @Override
    public String getName() {
        return "LS SUS";
    }

    @Override
    public String getFullName() {
        return "LS SUS " + linearScalingSelector.getPs();
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
    public List<Individual> select(SelectionContext selectionContext) {
        return linearScalingSelector.select(selectionContext, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return linearScalingSelector.scale(selectionContext);
    }

}