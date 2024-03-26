package lab.v2.run;

import lab.model.Individual;
import lab.v2.encoding.DecoderV2;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.population.Population;
import lab.v2.selection.Selector;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

public class RunPoolExecutor {

    private static final int MAX_ITERATIONS = 10000000;

    private final DecoderV2 decoder = DecoderV2.getInstance();

    public void executeRunPool(RunPool runPool) {
        runPool.runs().forEach(this::executeRun);
    }

    public Map<Individual, ? extends Number> executeRun(Run run) {
        RunConfiguration runConfiguration = run.runConfiguration();
        FitnessFunctionV2<?, ?> function = runConfiguration.function();
        Population population = run.population();
        Selector selector = runConfiguration.selector();

        List<Individual> currentIndividuals = population.individuals();
        Map<Individual, ? extends Number> individualToFitness = null;
        int i = 0;

        while (hasNotConverged(currentIndividuals) && i < MAX_ITERATIONS) {
            individualToFitness = getIndividualToFitness(currentIndividuals, function);
            currentIndividuals = selector.select(individualToFitness);
            i++;
        }
        return individualToFitness;
    }

    private boolean hasNotConverged(List<Individual> individuals) {
        return true;
    }

    private <ARG_T extends Number, RES_T extends Number> Map<Individual, RES_T> getIndividualToFitness(List<Individual> individuals,
                                                                                                       FitnessFunctionV2<ARG_T, RES_T> function) {
        return individuals
                .stream()
                .collect(toUnmodifiableMap(identity(), individual -> evaluateFitness(individual, function)));
    }

    private <ARG_T extends Number, RES_T extends Number> RES_T evaluateFitness(Individual individual,
                                                                               FitnessFunctionV2<ARG_T, RES_T> function) {
        ARG_T phenotype = decoder.decodeV2(individual, function);
        return function.evaluate(phenotype);
    }
}
