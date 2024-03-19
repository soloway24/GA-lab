package lab.v2.population;

import lab.model.Individual;
import lab.parameters.Encoding;
import lab.v2.function.FitnessFunctionV2;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.shuffle;
import static java.util.List.copyOf;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static lab.model.Individual.createRandomIndividual;

public class PopulationInitializer {

    public static final PopulationInitializer POPULATION_INITIALIZER = new PopulationInitializer();

    private PopulationInitializer() {
    }

    public List<Individual> initializeRandomPopulation(int chromosomeLength, Encoding encoding, int populationSize) {
        return createRandomIndividuals(chromosomeLength, encoding, populationSize)
                .toList();
    }

    public List<Individual> initializeRandomPopulationWithoutOptimal(FitnessFunctionV2<?, ?> function, Encoding encoding, int populationSize) {
        return createRandomIndividualsWithoutOptimal(function, encoding, populationSize)
                .toList();
    }

    public List<Individual> initializeRandomPopulationWithOptimal(FitnessFunctionV2<?, ?> function,
                                                                  Encoding encoding,
                                                                  int populationSize,
                                                                  int optimalSize) {
        int restSize = populationSize - optimalSize;
        Stream<Individual> optimalIndividuals = createOptimalIndividuals(function, encoding, optimalSize);
        Stream<Individual> restPopulation = createRandomIndividualsWithoutOptimal(function, encoding, restSize);
        List<Individual> wholePopulation = concat(optimalIndividuals, restPopulation)
                .collect(toList());
        shuffle(wholePopulation);
        return copyOf(wholePopulation);
    }

    public List<Individual> initializeRandomPopulationWithOptimal(FitnessFunctionV2<?, ?> function,
                                                                  Encoding encoding,
                                                                  int populationSize,
                                                                  double optimalPercentage) {
        verifyOptimalPercentage(optimalPercentage);
        int optimalSize = (int) (populationSize * optimalPercentage);
        return initializeRandomPopulationWithOptimal(function, encoding, populationSize, optimalSize);
    }

    private Stream<Individual> createRandomIndividuals(int chromosomeLength, Encoding encoding, int quantity) {
        return IntStream.range(0, quantity)
                .mapToObj(i -> createRandomIndividual(chromosomeLength, encoding));
    }

    private Stream<Individual> createRandomIndividualsWithoutOptimal(FitnessFunctionV2<?, ?> function, Encoding encoding, int quantity) {
        Individual optimal = function.getOptimalIndividual(encoding);
        return IntStream.range(0, quantity)
                .mapToObj(i -> createNotOptimalIndividual(function.getChromosomeLength(), encoding, optimal));
    }

    private Stream<Individual> createOptimalIndividuals(FitnessFunctionV2<?, ?> function, Encoding encoding, int quantity) {
        return IntStream.range(0, quantity)
                .mapToObj(i -> function.getOptimalIndividual(encoding));
    }

    private Individual createNotOptimalIndividual(int chromosomeLength, Encoding encoding, Individual optimal) {
        Individual individual;
        do {
            individual = createRandomIndividual(chromosomeLength, encoding);
        } while (isOptimal(individual, optimal));
        return individual;
    }

    private boolean isOptimal(Individual individual, Individual optimal) {
        return individual.getBinaryCode().equals(optimal.getBinaryCode());
    }

    private void verifyOptimalPercentage(double optimalPercentage) {
        if (optimalPercentage < 0 || optimalPercentage > 1) {
            throw new IllegalArgumentException("Provided percentage of optimal individuals in the population " + optimalPercentage
                    + " is not in the range of [0.0, 1.0]!");
        }

        int scale = BigDecimal.valueOf(optimalPercentage).scale();
        if (scale > 2) {
            throw new IllegalArgumentException("Provided percentage of optimal individuals in the population " + optimalPercentage
                    + " should have max scale of 2 in order to represent a whole number of individuals, but has scale of " + scale + " !");
        }
    }
}
