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
public class SpanMethodRwsSelector implements Selector {

    private final SpanMethodSelector spanMethodSelector;
    private final RwsSelector rwsSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.RWS;
    }

    @Override
    public String getName() {
        return "Span RWS";
    }

    @Override
    public String getFullName() {
        return "Span RWS " + spanMethodSelector.getG();
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
        return spanMethodSelector.select(selectionContext, rwsSelector::select);
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