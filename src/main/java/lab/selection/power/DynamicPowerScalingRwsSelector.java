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
public class DynamicPowerScalingRwsSelector implements Selector {

    private final DynamicPowerScalingSelector dynamicPowerScalingSelector;
    private final RwsSelector rwsSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.RWS;
    }

    @Override
    public String getName() {
        return "DPS RWS";
    }

    @Override
    public String getFullName() {
        return "DPS RWS "
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
        return dynamicPowerScalingSelector.select(selectionContext, rwsSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return dynamicPowerScalingSelector.scale(selectionContext);
    }

}
