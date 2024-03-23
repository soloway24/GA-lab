package lab.v2.selection;

import lab.model.Individual;

import java.util.List;
import java.util.Map;

public class PowerScalingSusSelector<T extends Number> extends PowerScalingSelector<T> {

    private final SusSelector<Double> susSelector;

    public PowerScalingSusSelector(SusSelector<Double> susSelector, double power) {
        super(power);
        this.susSelector = susSelector;
    }

    @Override
    public String getName() {
        return "PS SUS";
    }

    @Override
    protected List<Individual> selectFromBaseSelector(Map<Individual, Double> individualToFitness) {
        return susSelector.select(individualToFitness);
    }

}
