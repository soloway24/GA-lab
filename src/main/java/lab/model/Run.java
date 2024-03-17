package lab.model;

import lab.encoding.Decoder;
import lab.genetic_operators.Crossover;
import lab.genetic_operators.Mutation;
import lab.parameters.ContestType;
import lab.parameters.Encoding;
import lab.parameters.FitnessFunction;
import lab.parameters.GeneticOperatorsApplication;
import lab.selection.Arena;
import lab.stats.RunStatsData;
import lab.stop_conditions.StopCondition;
import lab.utils.*;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static lab.utils.GeneticUtils.N;

@Getter
public class Run {

    private static final int MAX_ITERATIONS = 10000000;

    private List<Individual> population;
    private final Encoding encoding;
    private final ContestType contestType;
    private final FitnessFunction fitnessFunction;
    private final GeneticOperatorsApplication geneticOperatorsApplication;
    public int nIterationBeforeStop;

    public double F_found, F_avg;
    public double I_min = Float.MAX_VALUE, I_max = Float.MIN_VALUE, I_avg;
    public double Sigma_I_min = Float.MAX_VALUE, Sigma_I_max = Float.MIN_VALUE;
    public int NI_I_min, NI_I_max;

    public double GR_early, GR_avg, GR_late;
    public int NI_GR_late;

    public double RR_min = Float.MAX_VALUE, RR_max = Float.MIN_VALUE, RR_avg;
    public long NI_RR_min, NI_RR_max;

    public double Teta_min = Float.MAX_VALUE, Teta_max = Float.MIN_VALUE, Teta_avg;
    public long NI_Teta_min, NI_Teta_max;

    public double s_min = Float.MAX_VALUE, s_max = Float.MIN_VALUE, s_avg;
    public long NI_s_min, NI_s_max;

    public List<RunStatsData> stats;

    public Run(Encoding encoding, ContestType contestType, FitnessFunction function, GeneticOperatorsApplication geneticOperatorsApplication, List<Individual> initialPopulation) {
        this.encoding = encoding;
        this.contestType = contestType;
        this.fitnessFunction = function;
        this.geneticOperatorsApplication = geneticOperatorsApplication;
        this.population = initialPopulation;
        stats = new ArrayList<>();
    }

    public void run() {

        double totalIntensity = 0, totalGR = 0, totalRR = 0, totalTeta = 0, totalS = 0;
        int iteration;
        /*{
            double RR = countUniqueIndices(population) / (double) population.size();
            double Teta = countDuplicatedIndices(population) / (double) population.size();
            double growsRate = growsRate(population, population, 0);
            RunStatsData runStatsData = new RunStatsData(0, F_avg(population), F_found(population), selectionIntensity(population, population), F_avg(population) - F_avg(population), countSigma(population), countBestIndividualEntries(population) / (double) N * 100, growsRate, RR, Teta);
            runStatsData.ones = countGenes(population);
            if (fitnessFunction == FitnessFunction.F1 || fitnessFunction == FitnessFunction.F2) {
                runStatsData.phenotypes = countPhenotypes(population);
                runStatsData.fitness = countFitnessFunction(population);
            }
            stats.add(runStatsData);
        }*/
        for (iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            if (StopCondition.shouldStop(population, geneticOperatorsApplication)) {
                break;
            }

            Collections.shuffle(population);
            for (int i = 0; i < population.size(); i++) {
                population.get(i).setIndex(i);
            }

            List<Individual> parentsPool = new ArrayList<>();
            Arena.generateParents(population, parentsPool, contestType, fitnessFunction);

            if (fitnessFunction.isApplyOperators()) {
                applyOperators(parentsPool);
            }

            totalIntensity += intensity(population, parentsPool, iteration);
            double growsRate = growsRate(population, parentsPool, iteration);
            totalGR += growsRate;

            double RR = countUniqueIndices(parentsPool) / (float) parentsPool.size();
            double Teta = countDuplicatedIndices(parentsPool) / (float) parentsPool.size();
            totalRR += RR;
            totalTeta += Teta;

            if (RR_min > RR) {
                RR_min = RR;
                NI_RR_min = iteration + 1;

                Teta_max = Teta;
                NI_Teta_max = iteration + 1;
            }

            if (RR_max < RR) {
                RR_max = RR;
                NI_RR_max = iteration + 1;

                Teta_min = Teta;
                NI_Teta_min = iteration + 1;
            }

            double s = F_avg(parentsPool) - F_avg(population);
            totalS += s;
            if (s < s_min) {
                s_min = s;
                NI_s_min = iteration + 1;
            }
            if (s > s_max) {
                s_max = s;
                NI_s_max = iteration + 1;
            }

            RunStatsData runStatsData = new RunStatsData(iteration + 1, F_avg(parentsPool), F_found(parentsPool), selectionIntensity(population, parentsPool), s, countSigma(parentsPool), countBestIndividualEntries(parentsPool) / (double) N * 100, growsRate, RR, Teta);
            if (iteration < 5) {
                runStatsData.ones = countGenes(population);
                if (fitnessFunction == FitnessFunction.F1 || fitnessFunction == FitnessFunction.F2) {
                    runStatsData.phenotypes = countPhenotypes(population);
                    runStatsData.fitness = countFitnessFunction(population);
                }
            }
            stats.add(runStatsData);
            population = parentsPool;
        }

        double RR = countUniqueIndices(population) / (double) population.size();
        double Teta = countDuplicatedIndices(population) / (double) population.size();
        double growsRate = growsRate(population, population, iteration);
        RunStatsData runStatsData = new RunStatsData(iteration + 1, F_avg(population), F_found(population), selectionIntensity(population, population), F_avg(population) - F_avg(population), countSigma(population), countBestIndividualEntries(population)/(double)N * 100 , growsRate, RR, Teta);
        runStatsData.ones = countGenes(population);
        if (fitnessFunction == FitnessFunction.F1 || fitnessFunction == FitnessFunction.F2) {
            runStatsData.phenotypes = countPhenotypes(population);
            runStatsData.fitness = countFitnessFunction(population);
        }
        stats.add(runStatsData);

        F_found = F_found(population);
        F_avg = F_avg(population);
        I_avg = totalIntensity / (iteration + 1);
        GR_avg = totalGR / (iteration + 1);
        nIterationBeforeStop = iteration + 1;
        RR_avg = totalRR / (iteration + 1);
        Teta_avg = totalTeta / (iteration + 1);
        s_avg = totalS / (iteration + 1);
    }

    private long countUniqueIndices(List<Individual> population) {
        return population.stream().mapToInt(Individual::getIndex).distinct().count();
    }

    private long countDuplicatedIndices(List<Individual> population) {
        return population.size() - countUniqueIndices(population);
    }

    private float intensity(List<Individual> population, List<Individual> parents, int iteration) {
        float currentIntensity = selectionIntensity(population, parents);
        if (currentIntensity > I_max) {
            I_max = currentIntensity;
            NI_I_max = iteration + 1;
            Sigma_I_max = countSigma(parents);
        }
        if (currentIntensity < I_min) {
            I_min = currentIntensity;
            NI_I_min = iteration + 1;
            Sigma_I_min = countSigma(parents);
        }
        return currentIntensity;
    }

    private float growsRate(List<Individual> population, List<Individual> parents, int iteration) {
        float GR = 0, gr1, gr2 = 0;
        if (F_found(parents) >= F_found(population)) {
            gr1 = countBestIndividualEntries(population);
            gr2 = countBestIndividualEntries(parents);

            GR = gr2 / (gr1 == 0 ? 1.f : gr1);
        }

        if (iteration == 2) {
            GR_early = GR;
        } else if (iteration >= 2 && gr2 > (float) parents.size() / 2 && NI_GR_late == 0) {
            GR_late = GR;
            NI_GR_late = iteration + 1;
        }
        return GR;
    }

    private void applyOperators(List<Individual> parentsPool) {
        switch (geneticOperatorsApplication) {
            case CROSSOVER -> Crossover.crossover(parentsPool);
            case MUTATION -> Mutation.mutation(parentsPool);
            case CROSSOVER_MUTATION -> {
                Crossover.crossover(parentsPool);
                Mutation.mutation(parentsPool);
            }
        }
    }

    public double F_found(List<Individual> population) {
        double best = 0;
        for (Individual individual : population) {
            double currentHealth = individual.getHealth(fitnessFunction);
            if (currentHealth > best) {
                best = currentHealth;
            }
        }
        return best;
    }

    public float F_avg(List<Individual> population) {
        float total = 0;
        for (Individual individual : population) {
            total += individual.getHealth(fitnessFunction);
        }
        return total / population.size();
    }

    private float countSigma(List<Individual> population) {
        float avg = F_avg(population);
        float total = 0;
        for (Individual individual : population) {
            total += Math.pow(individual.getHealth(fitnessFunction) - avg, 2);
        }
        return (float) Math.sqrt(total / (population.size() - 1));
    }


    private float selectionIntensity(List<Individual> population, List<Individual> parents) {
        float sigma = countSigma(population);
        if (sigma == 0) {
            sigma = 1;
        }
        return (F_avg(parents) - F_avg(population)) / sigma;
    }

    private int countBestIndividualEntries(List<Individual> population) {
        int total = 0;
        Individual optimal = fitnessFunction.getBest();
        String bestValue = optimal.getData();
        for (Individual individual : population) {
            if (individual.getData().equals(bestValue)) {
                total++;
            }
        }
        return total;
    }


    public boolean Suc() {
        if (nIterationBeforeStop == MAX_ITERATIONS) {
            return false;
        }

        if (fitnessFunction == FitnessFunction.F_ALL_CONST) {
            return true;
        }

        if (fitnessFunction == FitnessFunction.FHD) {
            if (geneticOperatorsApplication == GeneticOperatorsApplication.CROSSOVER_MUTATION || (geneticOperatorsApplication == GeneticOperatorsApplication.MUTATION)) {
                return isAlmostOptimalPopulation();
            } else {
                return isOptimalPopulation();
            }
        } else {
            return success();
        }
    }

    private boolean success() {
        Individual optimal = fitnessFunction.getBest();
        double best = optimal.getHealth(fitnessFunction);
        for (Individual individual : population) {
            if (Math.abs(best - individual.getHealth(fitnessFunction)) <= 0.01) {
                return true;
            }
        }
        return false;
    }


    private boolean isOptimalPopulation() {
        Individual optimal = fitnessFunction.getBest();
        for (Individual individual : population) {
            if (!individual.getData().equals(optimal.getData())) {
                return false;
            }
        }
        return true;
    }

    private boolean isAlmostOptimalPopulation() {
        Individual optimal = fitnessFunction.getBest();
        int counter = 0;
        for (Individual individual : population) {
            if (individual.getData().equals(optimal.getData())) {
                counter++;
            }
        }
        return counter >= population.size() * 0.9;
    }

    private List<Integer> countGenes(List<Individual> population) {
        return population.stream().map(Individual::getOnes).collect(Collectors.toList());
    }


    private List<Double> countPhenotypes(List<Individual> population) {
        return population.stream().map(entry -> Decoder.decode(entry.getData(), fitnessFunction)).collect(Collectors.toList());
    }

    private List<Double> countFitnessFunction(List<Individual> population) {
        return population.stream().map(entry -> entry.getHealth(fitnessFunction)).collect(Collectors.toList());
    }

}
