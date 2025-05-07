package lab.selection.adaptivepowerlaw;

import lab.Individual;
import lab.selection.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;
import static lab.selection.AdditionalSelectorProperty.NI;

@RequiredArgsConstructor
public class AdaptivePowerLawScalingRwsSelector implements Selector {

    private static final int NOT_USED_VALUE = -1;

    private final AdaptivePowerLawScalingSelector adaptivePowerLawScalingSelector;
    private final RwsSelector rwsSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.RWS;
    }

    @Override
    public String getName() {
        return "APL RWS";
    }

    @Override
    public String getFullName() {
        return "APL RWS " + adaptivePowerLawScalingSelector.getG();
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.of(valueOf(adaptivePowerLawScalingSelector.getG()));
    }

    @Override
    public Optional<String> getParam2() {
        return Optional.empty();
    }

    @Override
    public List<Individual> select(SelectionContext selectionContext) {
        return adaptivePowerLawScalingSelector.select(selectionContext, rwsSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return adaptivePowerLawScalingSelector.scale(selectionContext);
    }

    @Override
    public Map<AdditionalSelectorProperty, Object> getAdditionalSelectorProperties() {
        return Map.of(NI, NOT_USED_VALUE);
    }
}