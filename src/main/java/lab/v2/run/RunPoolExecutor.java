package lab.v2.run;

import lab.v2.Individual;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.identifier.ConvergenceIdentifier;
import lab.v2.operator.Operator;
import lab.v2.population.Population;
import lab.v2.selection.Selector;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

@RequiredArgsConstructor
public class RunPoolExecutor {

    private static final int MAX_ITERATIONS = 10000000;

    private final ConvergenceIdentifier convergenceIdentifier;

    public List<RunPoolStats> executeAllRunPools(List<RunPool> runPools) {
        return runPools.stream()
                .map(this::executeRunPool)
                .toList();
    }

    public RunPoolStats executeRunPool(RunPool runPool) {
        System.out.println("Executing RunPool: " + runPool.runConfiguration());
        List<RunStats> runPoolStats = IntStream.range(0, runPool.getSize())
                .mapToObj(i -> {
                    System.out.println("Run " + i);
                    return executeRun(runPool.runs().get(i));
                })
                .toList();
        return new RunPoolStats(runPool.runConfiguration(), runPoolStats);
    }

    public RunStats executeRun(Run run) {
        RunConfiguration runConfiguration = run.runConfiguration();
        FitnessFunctionV2<?, ? extends Number> function = runConfiguration.function();
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
        individualToFitness = getIndividualToFitness(currentIndividuals, function);
        boolean hasConverged = convergenceIdentifier.hasConverged(currentIndividuals, operator.getOperatorType());
        boolean isSuccessful = function.isSuccessful(individualToFitness, operator.getOperatorType(), hasConverged);

        return new RunStats(individualToFitness, isSuccessful);
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
