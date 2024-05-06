package lab.population;

import lab.Individual;
import lab.function.FitnessFunction;
import lab.util.CalculationUtils;
import lab.encoding.Encoding;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.shuffle;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static lab.population.PopulationInitializationType.*;

@RequiredArgsConstructor
public class PopulationInitializer {

    private final PopulationTypeValidator populationTypeValidator;

    private final Map<PopulationInitializationType, Function<PopulationConfiguration, Population>> initializationTypeToInitializer =
            Map.of(
                    RANDOM, this::initializeRandomPopulation,
                    OPTIMAL_QUANTITY, this::initializeRandomPopulationWithOptimalQuantity,
                    OPTIMAL_PERCENTAGE, this::initializeRandomPopulationWithOptimalPercentage
            );

    public Population initializePopulation(PopulationConfiguration populationConfig) {
        PopulationInitializationType initializationType = populationConfig.populationType().getInitializationType();

        return getInitializer(initializationType).apply(populationConfig);
    }

    private Function<PopulationConfiguration, Population> getInitializer(PopulationInitializationType populationInitializationType) {
        return ofNullable(initializationTypeToInitializer.get(populationInitializationType))
                .orElseThrow(() -> new IllegalArgumentException("Provided population type " + populationInitializationType
                        + " does not have corresponding initializer!"));
    }

    private Population initializeRandomPopulation(PopulationConfiguration populationConfig) {
        List<Individual> individuals = createRandomIndividuals(populationConfig.function().getChromosomeLength(),
                populationConfig.encoding(), populationConfig.populationSize())
                .toList();
        List<Individual> indexedIndividuals = CalculationUtils.getIndexedIndividuals(individuals);
        return new Population(populationConfig, indexedIndividuals);
    }

    private Population initializeRandomPopulationWithOptimalQuantity(PopulationConfiguration populationConfig) {
        int optimalQuantity = getOptimalQuantity(populationConfig.populationType());

        return initializeRandomPopulationWithOptimalQuantity(populationConfig, optimalQuantity);
    }

    private Population initializeRandomPopulationWithOptimalPercentage(PopulationConfiguration populationConfig) {
        double optimalPercentage = getOptimalPercentage(populationConfig.populationType());
        populationTypeValidator.verifyOptimalPercentage(optimalPercentage);
        int optimalQuantity = (int) (populationConfig.populationSize() * optimalPercentage);

        return initializeRandomPopulationWithOptimalQuantity(populationConfig, optimalQuantity);
    }

    private Population initializeRandomPopulationWithOptimalQuantity(PopulationConfiguration populationConfig,
                                                                     int optimalQuantity) {
        if (optimalQuantity == 0) {
            return initializeRandomPopulationWithoutOptimal(populationConfig);
        }

        int populationSize = populationConfig.populationSize();
        populationTypeValidator.verifyOptimalQuantity(optimalQuantity, populationSize);

        FitnessFunction<?, ?> function = populationConfig.function();
        Encoding encoding = populationConfig.encoding();
        int restSize = populationSize - optimalQuantity;

        Stream<Individual> optimalIndividuals = createOptimalIndividuals(function, encoding, optimalQuantity);
        Stream<Individual> restPopulation = createRandomNotOptimalIndividuals(function, encoding, restSize);
        List<Individual> wholePopulation = concat(optimalIndividuals, restPopulation)
                .collect(toList());
        shuffle(wholePopulation);
        List<Individual> individuals = CalculationUtils.getIndexedIndividuals(wholePopulation);
        return new Population(populationConfig, individuals);
    }

    private Population initializeRandomPopulationWithoutOptimal(PopulationConfiguration populationConfiguration) {
        List<Individual> individuals = createRandomNotOptimalIndividuals(populationConfiguration.function(),
                populationConfiguration.encoding(), populationConfiguration.populationSize())
                .toList();
        return new Population(populationConfiguration, individuals);
    }

    private Stream<Individual> createRandomIndividuals(int chromosomeLength, Encoding encoding, int quantity) {
        return IntStream.range(0, quantity)
                .mapToObj(i -> Individual.createRandomIndividual(chromosomeLength, encoding));
    }

    private Stream<Individual> createRandomNotOptimalIndividuals(FitnessFunction<?, ?> function, Encoding encoding, int quantity) {
        Individual optimal = getOptimalIndividual(function, encoding);
        return IntStream.range(0, quantity)
                .mapToObj(i -> createNotOptimalIndividual(function.getChromosomeLength(), encoding, optimal));
    }

    private Stream<Individual> createOptimalIndividuals(FitnessFunction<?, ?> function, Encoding encoding, int quantity) {
        return IntStream.range(0, quantity)
                .mapToObj(i -> getOptimalIndividual(function, encoding));
    }

    private Individual createNotOptimalIndividual(int chromosomeLength, Encoding encoding, Individual optimal) {
        Individual individual;
        do {
            individual = Individual.createRandomIndividual(chromosomeLength, encoding);
        } while (isOptimal(individual, optimal));
        return individual;
    }

    private Individual getOptimalIndividual(FitnessFunction<?, ?> function, Encoding encoding) {
        return function.getOptimalIndividual(encoding)
                .orElseThrow(() -> new IllegalStateException("Function + " + function + " does not provide and optimal " +
                        "individual for the encoding: " + encoding + " !"));
    }

    private boolean isOptimal(Individual individual, Individual optimal) {
        return individual.getBinaryCode().equals(optimal.getBinaryCode());
    }

    private int getOptimalQuantity(PopulationType populationType) {
        return populationType.getOptimalQuantity()
                .orElseThrow(() -> new IllegalArgumentException("Provided population configuration " + populationType
                        + "does not contain required optimal quantity value for population type " + populationType.getInitializationType() + " !"));
    }

    private double getOptimalPercentage(PopulationType populationType) {
        return populationType.getOptimalPercentage()
                .orElseThrow(() -> new IllegalArgumentException("Provided population configuration " + populationType
                        + "does not contain required optimal percentage value for population type " + populationType.getInitializationType() + " !"));
    }
}
