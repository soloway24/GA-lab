package lab.v2.selection;

import lab.model.Individual;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import static java.util.stream.Collectors.toUnmodifiableMap;

@RequiredArgsConstructor
public class SusSelector<T extends Number> implements Selector<T> {

    private static final int SPIN_STEP = 1;
    private final FitnessToPercentageConvertor<T> fitnessToPercentageConvertor;
    private final Random random = new Random();

    @Override
    public String getName() {
        return "SUS";
    }

    @Override
    public List<Individual> select(Map<Individual, T> individualToFitness) {
        Map<Individual, Double> individualToPercentage = fitnessToPercentageConvertor.convertToSelectionPercentages(individualToFitness);
        return selectSUS(individualToPercentage);
    }

    private List<Individual> selectSUS(Map<Individual, Double> individualToPercentage) {
        Map<Individual, Double> individualToExpectedQuantity = getIndividualToExpectedQuantity(individualToPercentage);

        return selectAll(individualToExpectedQuantity);
    }

    private Map<Individual, Double> getIndividualToExpectedQuantity(Map<Individual, Double> individualToPercentage) {
        return individualToPercentage.entrySet().stream()
                .collect(toUnmodifiableMap(Entry::getKey, entry -> entry.getValue() * individualToPercentage.size()));
    }

    private List<Individual> selectAll(Map<Individual, Double> individualToExpectedQuantity) {
        int populationSize = individualToExpectedQuantity.size();
        double spin = random.nextDouble();
        double currentPercentage = 0;
        double selectedQuantity = 0;
        List<Individual> selected = new ArrayList<>();

        for (Entry<Individual, Double> entry : individualToExpectedQuantity.entrySet()) {
            currentPercentage += entry.getValue();
            while (currentPercentage >= spin && selectedQuantity < populationSize) {
                selected.add(entry.getKey());
                selectedQuantity++;
                spin += SPIN_STEP;
            }
        }
        return selected;
    }

}
