package lab.selection.adaptivepowerlaw;

import lab.Individual;
import lab.selection.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;
import static lab.selection.AdditionalSelectorProperty.NI;
import static lab.selection.AdditionalSelectorProperty.S0;

@RequiredArgsConstructor
public class AdaptivePowerLawScalingSusSelector implements Selector {

    private static final int NOT_USED_VALUE = -1;

    private final AdaptivePowerLawScalingSelector adaptivePowerLawScalingSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.SUS;
    }

    @Override
    public String getName() {
        return "APL SUS";
    }

    @Override
    public String getFullName() {
        return "APL SUS " + adaptivePowerLawScalingSelector.getG();
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
        return adaptivePowerLawScalingSelector.select(selectionContext, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return adaptivePowerLawScalingSelector.scale(selectionContext);
    }

    @Override
    public Map<AdditionalSelectorProperty, Object> getAdditionalSelectorProperties() {
        return Map.of(
                NI, NOT_USED_VALUE,
                S0, NOT_USED_VALUE
        );
    }
}