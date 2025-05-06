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
    public List<Individual> select(SelectionContext selectionContext) {
        return dynamicLinearScalingSelector.select(selectionContext, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return dynamicLinearScalingSelector.scale(selectionContext);
    }

}