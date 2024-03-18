import lab.genetic_operators.Mutation;
import lab.model.Individual;
import lab.model.RunPool;
import lab.parameters.ContestType;
import lab.parameters.Encoding;
import lab.function.FitnessFunction;
import lab.parameters.GeneticOperatorsApplication;
import lab.stats.RunPoolStatsData;
import lab.utils.Export;
import lab.utils.GeneticUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static lab.utils.GeneticUtils.*;

public class Main {

    public static void main(String[] args) {
       /* N = 100;
        FITNESS_FUNCTION = FitnessFunction.F2;
        ENCODING = Encoding.STANDARD;
        CONTEST_TYPE = ContestType.MULTIPLE_ENTRY ;
        P_SWAP = 0.8f;
        GENETIC_OPERATORS = GeneticOperatorsApplication.CROSSOVER_MUTATION;

        List<Individual> initialPopulation = initPopulation(FITNESS_FUNCTION, ENCODING);
        List<RunPoolStatsData> all_pools = new ArrayList<>();

        run(initialPopulation, all_pools);*/

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));
        List<RunPoolStatsData> all_pools = new ArrayList<>();

        for (Integer populationSize : POPULATION_SIZES) {
            N = populationSize;
            List<RunPoolStatsData> n_pools = new ArrayList<>();
            for (FitnessFunction function : FitnessFunction.values()) {
                GeneticUtils.FITNESS_FUNCTION = function;
                Encoding[] values;
                if (function == FitnessFunction.QUAD || function == FitnessFunction.QUAD_SYM)
                    values = Encoding.values();
                else
                    values = new Encoding[]{Encoding.STANDARD};

                for (Encoding encoding : values) {
                    GeneticUtils.ENCODING = encoding;
                    List<Individual> initialPopulation = initPopulation(function, encoding);
                    for (ContestType contestType : ContestType.values()) {
                        GeneticUtils.CONTEST_TYPE = contestType;
                        for (float sel_prob : SELECTION_PROBABILITY) {
                            GeneticUtils.P_SWAP = sel_prob;
                            for (GeneticOperatorsApplication operators : GeneticOperatorsApplication.values()) {
                                GENETIC_OPERATORS = operators;
                                run(initialPopulation, all_pools, n_pools);
                            }
                        }
                    }
                }
            }
            Export.exportRunPools(n_pools, N + "_");
        }
        Export.exportRunPools(all_pools, "");
    }

    public static void run(List<Individual> initialPopulation, List<RunPoolStatsData> all_pools, List<RunPoolStatsData> n_pools) {
        Mutation.MUTATION_PROBABILITY = getMutationProbability();

        long startTime = System.currentTimeMillis();
        Date start = new Date();

        System.err.println(start + " ------ Configuration started: " + (all_pools.size() + 1));

        RunPool pool = new RunPool(N_RUNS, ENCODING, CONTEST_TYPE, FITNESS_FUNCTION, GENETIC_OPERATORS, initialPopulation);
        pool.runProcesses();
        pool.calculateIterationsStatistics();
        System.err.println("Exporting results, calculation time : " + (System.currentTimeMillis() - startTime) / 1000 + " sec");
        Export.exportStandaloneRuns(pool);

        all_pools.add(new RunPoolStatsData(pool));
        n_pools.add(new RunPoolStatsData(pool));
        System.err.println("Configuration Succeed: " + all_pools.size() + ", total time : " +
                (System.currentTimeMillis() - startTime) / 1000 + " sec");

    }

    public static List<Individual> initPopulation(FitnessFunction function, Encoding encoding) {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < N - 1; i++) {
            Individual individual = new Individual();
            individual.fillRandomly(function.getLength());
            population.add(individual);
        }
        population.add(function.getBest());
        return population;
    }

    private static float getMutationProbability() {
        float res;
        if (GeneticUtils.FITNESS_FUNCTION.getLength() == 10) {
            res = 0.0001f / (GeneticUtils.N / 100.f);
        } else {
            res = 0.00001f / (GeneticUtils.N / 100.f);
        }
        return res;
    }
}
