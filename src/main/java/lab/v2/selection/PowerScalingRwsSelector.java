package lab.v2.selection;

import lab.model.Individual;

import java.util.List;
import java.util.Map;

public class PowerScalingRwsSelector<T extends Number> extends PowerScalingSelector<T> {

    private final RwsSelector<Double> rwsSelector;

    public PowerScalingRwsSelector(RwsSelector<Double> rwsSelector, double power) {
        super(power);
        this.rwsSelector = rwsSelector;
    }

    @Override
    public String getName() {
        return "PS RWS";
    }

    @Override
    protected List<Individual> selectFromBaseSelector(Map<Individual, Double> individualToFitness) {
        return rwsSelector.select(individualToFitness);
    }
}
