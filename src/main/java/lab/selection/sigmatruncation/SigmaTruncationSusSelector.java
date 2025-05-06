package lab.selection.sigmatruncation;

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
public class SigmaTruncationSusSelector implements Selector {

    private final SigmaTruncationSelector sigmaTruncationSelector;
    private final SusSelector susSelector;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.SUS;
    }

    @Override
    public String getName() {
        return "ST SUS";
    }

    @Override
    public String getFullName() {
        return "ST SUS " + sigmaTruncationSelector.getC();
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.of(valueOf(sigmaTruncationSelector.getC()));
    }

    @Override
    public Optional<String> getParam2() {
        return Optional.empty();
    }

    @Override
    public List<Individual> select(SelectionContext selectionContext) {
        return sigmaTruncationSelector.select(selectionContext, susSelector::select);
    }

    @Override
    public Map<Individual, Double> scale(SelectionContext selectionContext) {
        return sigmaTruncationSelector.scale(selectionContext);
    }
}