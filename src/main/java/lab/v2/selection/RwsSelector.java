package lab.v2.selection;

import lab.model.Individual;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class RwsSelector<T extends Number> implements Selector<T> {

    private final FitnessToProbabilityConvertor<T> fitnessToProbabilityConvertor;
    private final Random random = new Random();

    @Override
    public String getName() {
        return "RWS";
    }

    @Override
    public List<Individual> select(Map<Individual, T> individualToFitness) {
        Map<Individual, Double> individualToProbability = fitnessToProbabilityConvertor.convertToSelectionProbabilities(individualToFitness);
        return selectRWS(individualToProbability);
    }

    private List<Individual> selectRWS(Map<Individual, Double> individualToPercentage) {
        int selectionSize = individualToPercentage.size();

        return IntStream.range(0, selectionSize)
                .mapToObj(i -> selectOne(individualToPercentage))
                .toList();
    }

    private Individual selectOne(Map<Individual, Double> individualToPercentage) {
        double spin = random.nextDouble();
        double currentPercentage = 0;

        for (Entry<Individual, Double> entry : individualToPercentage.entrySet()) {
            currentPercentage += entry.getValue();
            if (currentPercentage >= spin) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Selection " + getName() + " did not find an individual for selection!");
    }

}
