package lab.selection.window;

import lab.Individual;
import lab.selection.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;
import static lab.selection.AdditionalSelectorProperty.WINDOW_WORST;

@RequiredArgsConstructor
public class WindowScalingSusSelector implements Selector {

    private final WindowScalingSelector windowScalingSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.SUS;
    }

    @Override
    public String getName() {
        return "Window SUS";
    }

    @Override
    public String getFullName() {
        return "Window SUS " + windowScalingSelector.getWindowSize();
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.of(valueOf(windowScalingSelector.getWindowSize()));
    }

    @Override
    public Optional<String> getParam2() {
        return Optional.empty();
    }

    @Override
    public List<Individual> select(SelectionContext selectionContext) {
        return windowScalingSelector.select(selectionContext, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return windowScalingSelector.scale(selectionContext);
    }

    @Override
    public Map<AdditionalSelectorProperty, Object> getAdditionalSelectorProperties() {
        return Map.of(WINDOW_WORST, windowScalingSelector.getWindowSize());
    }
}