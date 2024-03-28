package lab.v2.selection;

import lab.v2.Individual;
import lab.v2.convertor.FitnessToProbabilityConvertor;
import lab.v2.convertor.ProbabilityToExpectedQuantityConvertor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import static lab.v2.selection.SelectorType.SUS;

@RequiredArgsConstructor
public class SusSelector implements Selector {

    private static final int SPIN_STEP = 1;
    private final FitnessToProbabilityConvertor fitnessToProbabilityConvertor;
    private final ProbabilityToExpectedQuantityConvertor probabilityToExpectedQuantityConvertor;
    private final Random random = new Random();

    @Override
    public SelectorType getSelectorType() {
        return SUS;
    }

    @Override
    public String getName() {
        return "SUS";
    }

    @Override
    public List<Individual> select(Map<Individual, ? extends Number> individualToFitness) {
        Map<Individual, Double> individualToProbability = fitnessToProbabilityConvertor.convertToSelectionProbabilities(individualToFitness);
        return selectSUS(individualToProbability);
    }

    private List<Individual> selectSUS(Map<Individual, Double> individualToProbability) {
        Map<Individual, Double> individualToExpectedQuantity =
                probabilityToExpectedQuantityConvertor.convertToExpectedQuantities(individualToProbability);

        return selectAll(individualToExpectedQuantity);
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
                selected.add(new Individual(entry.getKey()));
                selectedQuantity++;
                spin += SPIN_STEP;
            }
        }
        return selected;
    }

}
