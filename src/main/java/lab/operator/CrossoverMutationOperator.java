package lab.operator;

import lab.Individual;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static lab.operator.OperatorType.CROSSOVER_MUTATION;

@RequiredArgsConstructor
public class CrossoverMutationOperator implements Operator {

    private final Operator crossoverOperator;
    private final Operator mutationOperator;

    @Override
    public String getName() {
        return "CROSSOVER_MUTATION";
    }

    @Override
    public OperatorType getOperatorType() {
        return CROSSOVER_MUTATION;
    }

    @Override
    public List<Individual> apply(List<Individual> individuals) {
        List<Individual> crossedIndividuals = crossoverOperator.apply(individuals);
        return mutationOperator.apply(crossedIndividuals);
    }
}