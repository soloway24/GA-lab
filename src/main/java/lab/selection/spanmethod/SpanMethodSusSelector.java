package lab.selection.spanmethod;

import lab.Individual;
import lab.selection.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.valueOf;
import static lab.selection.AdditionalSelectorProperty.NI;

@RequiredArgsConstructor
public class SpanMethodSusSelector implements Selector {

    private final SpanMethodSelector spanMethodSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.SUS;
    }

    @Override
    public String getName() {
        return "Span SUS";
    }

    @Override
    public String getFullName() {
        return "Span SUS " + spanMethodSelector.getG();
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.of(valueOf(spanMethodSelector.getG()));
    }

    @Override
    public Optional<String> getParam2() {
        return Optional.empty();
    }

    @Override
    public List<Individual> select(SelectionContext selectionContext) {
        return spanMethodSelector.select(selectionContext, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return spanMethodSelector.scale(selectionContext);
    }

    @Override
    public List<AdditionalSelectorProperty> getAdditionalSelectorProperties() {
        return List.of(NI);
    }
}