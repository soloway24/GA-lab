package lab.v2.run;

import lab.v2.ConvergenceIdentifier;
import lab.v2.Individual;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.operator.Operator;
import lab.v2.population.Population;
import lab.v2.selection.Selector;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

@RequiredArgsConstructor
public class RunPoolExecutor {

    private static final int MAX_ITERATIONS = 10000000;

    private final ConvergenceIdentifier convergenceIdentifier;

    public void executeRunPool(RunPool runPool) {
        runPool.runs().forEach(run -> System.out.println(executeRun(run)));
    }

    public Map<Individual, ? extends Number> executeRun(Run run) {
        RunConfiguration runConfiguration = run.runConfiguration();
        FitnessFunctionV2<?, ?> function = runConfiguration.function();
        Population population = run.population();
        Selector selector = runConfiguration.selector();
        Operator operator = runConfiguration.operator();

        List<Individual> currentIndividuals = population.individuals();
        Map<Individual, ? extends Number> individualToFitness;
        int i = 0;

        while (hasNotConverged(currentIndividuals, operator) && i < MAX_ITERATIONS) {
            individualToFitness = getIndividualToFitness(currentIndividuals, function);
            currentIndividuals = selector.select(individualToFitness);
            currentIndividuals = operator.apply(currentIndividuals);
            i++;
        }
        return getIndividualToFitness(currentIndividuals, function);
    }

    private boolean hasNotConverged(List<Individual> individuals, Operator operator) {
        return !convergenceIdentifier.hasConverged(individuals, operator.getOperatorType());
    }

    private <ARG_T extends Number, RES_T extends Number> Map<Individual, RES_T> getIndividualToFitness(List<Individual> individuals,
                                                                                                       FitnessFunctionV2<ARG_T, RES_T> function) {
        return individuals
                .stream()
                .collect(toUnmodifiableMap(identity(), function::evaluate));
    }
}
