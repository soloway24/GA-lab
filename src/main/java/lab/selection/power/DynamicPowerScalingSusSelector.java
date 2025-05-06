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
public class DynamicPowerScalingSusSelector implements Selector {

    private final DynamicPowerScalingSelector dynamicPowerScalingSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.SUS;
    }

    @Override
    public String getName() {
        return "DPS SUS";
    }

    @Override
    public String getFullName() {
        return "DPS SUS "
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
    public List<Individual> select(SelectionContext selectionContext) {
        return dynamicPowerScalingSelector.select(selectionContext, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return dynamicPowerScalingSelector.scale(selectionContext);
    }

}
