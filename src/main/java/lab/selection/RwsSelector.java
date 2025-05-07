package lab.selection;

import lab.Individual;
import lab.convertor.FitnessToProbabilityConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import static java.util.Collections.shuffle;

@Component
@RequiredArgsConstructor
public class RwsSelector implements Selector {

    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor;
    private final Random random;

    @Override
    public SelectorType getSelectorType() {
        return SelectorType.RWS;
    }

    @Override
    public String getName() {
        return "RWS";
    }

    @Override
    public String getFullName() {
        return "RWS";
    }

    @Override
    public Optional<String> getParam1() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getParam2() {
        return Optional.empty();
    }

    @Override
    public List<Individual> select(SelectionContext selectionContext) {
        Map<Individual, Double> individualToProbability = fitnessToProbabilityConvertor.convertToSelectionProbabilities(selectionContext.getIndividualToFitness());
        return selectRWS(individualToProbability);
    }

    private List<Individual> selectRWS(Map<Individual, Double> individualToPercentage) {
        int selectionSize = individualToPercentage.size();

        return IntStream.range(0, selectionSize)
                .mapToObj(i -> selectOne(individualToPercentage))
                .map(Individual::new)
                .toList();
    }

    private Individual selectOne(Map<Individual, Double> individualToPercentage) {
        double spin = random.nextDouble();
        double currentPercentage = 0;

        List<Entry<Individual, Double>> individualToPercentageShuffled = new ArrayList<>(individualToPercentage.entrySet());
        shuffle(individualToPercentageShuffled);
        for (Entry<Individual, Double> entry : individualToPercentageShuffled) {
            currentPercentage += entry.getValue();
            if (currentPercentage >= spin) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Selector " + getName() + " did not find an individual for selection!");
    }

}
