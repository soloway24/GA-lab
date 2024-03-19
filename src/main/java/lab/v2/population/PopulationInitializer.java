package lab.v2.population;

import lab.model.Individual;
import lab.parameters.Encoding;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.parameters.RunConfiguration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.shuffle;
import static java.util.List.copyOf;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static lab.model.Individual.createRandomIndividual;
import static lab.v2.population.PopulationConfigurationValidator.POPULATION_CONFIGURATION_VALIDATOR;
import static lab.v2.population.PopulationType.*;

public class PopulationInitializer {

    public static final PopulationInitializer POPULATION_INITIALIZER = new PopulationInitializer();

    private final Map<PopulationType, Function<RunConfiguration, List<Individual>>> populationTypeToInitializer =
            Map.of(
                    RANDOM, this::initializeRandomPopulation,
                    OPTIMAL_QUANTITY, this::initializeRandomPopulationWithOptimalQuantity,
                    OPTIMAL_PERCENTAGE, this::initializeRandomPopulationWithOptimalPercentage
            );

    private PopulationInitializer() {
    }

    public List<Individual> initializePopulation(RunConfiguration runConfiguration) {
        PopulationType populationType = runConfiguration.populationConfiguration().getPopulationType();

        return getInitializer(populationType).apply(runConfiguration);
    }

    private Function<RunConfiguration, List<Individual>> getInitializer(PopulationType populationType) {
        return ofNullable(populationTypeToInitializer.get(populationType))
                .orElseThrow(() -> new IllegalArgumentException("Provided population type " + populationType
                        + " does not have corresponding initializer!"));
    }

    private List<Individual> initializeRandomPopulation(RunConfiguration runConfiguration) {
        return initializeRandomPopulation(runConfiguration.function(), runConfiguration.encoding(), runConfiguration.populationSize());
    }

    private List<Individual> initializeRandomPopulation(FitnessFunctionV2<?, ?> function, Encoding encoding, int populationSize) {
        return createRandomIndividuals(function.getChromosomeLength(), encoding, populationSize)
                .toList();
    }

    private List<Individual> initializeRandomPopulationWithOptimalQuantity(RunConfiguration runConfiguration) {
        int optimalQuantity = getOptimalQuantity(runConfiguration.populationConfiguration());

        return initializeRandomPopulationWithOptimalQuantity(runConfiguration.function(), runConfiguration.encoding(),
                runConfiguration.populationSize(), optimalQuantity);
    }

    private List<Individual> initializeRandomPopulationWithOptimalQuantity(FitnessFunctionV2<?, ?> function,
                                                                           Encoding encoding,
                                                                           int populationSize,
                                                                           int optimalQuantity) {
        POPULATION_CONFIGURATION_VALIDATOR.verifyOptimalQuantity(optimalQuantity, populationSize);

        if (optimalQuantity == 0) {
            return initializeRandomPopulationWithoutOptimal(function, encoding, populationSize);
        }

        int restSize = populationSize - optimalQuantity;
        Stream<Individual> optimalIndividuals = createOptimalIndividuals(function, encoding, optimalQuantity);
        Stream<Individual> restPopulation = createRandomNotOptimalIndividuals(function, encoding, restSize);
        List<Individual> wholePopulation = concat(optimalIndividuals, restPopulation)
                .collect(toList());
        shuffle(wholePopulation);
        return copyOf(wholePopulation);
    }

    private List<Individual> initializeRandomPopulationWithOptimalPercentage(RunConfiguration runConfiguration) {
        double optimalPercentage = getOptimalPercentage(runConfiguration.populationConfiguration());

        return initializeRandomPopulationWithOptimalPercentage(runConfiguration.function(), runConfiguration.encoding(),
                runConfiguration.populationSize(), optimalPercentage);
    }

    private List<Individual> initializeRandomPopulationWithOptimalPercentage(FitnessFunctionV2<?, ?> function,
                                                                             Encoding encoding,
                                                                             int populationSize,
                                                                             double optimalPercentage) {
        POPULATION_CONFIGURATION_VALIDATOR.verifyOptimalPercentage(optimalPercentage);

        int optimalQuantity = (int) (populationSize * optimalPercentage);
        return initializeRandomPopulationWithOptimalQuantity(function, encoding, populationSize, optimalQuantity);
    }

    private List<Individual> initializeRandomPopulationWithoutOptimal(FitnessFunctionV2<?, ?> function,
                                                                      Encoding encoding,
                                                                      int populationSize) {
        return createRandomNotOptimalIndividuals(function, encoding, populationSize)
                .toList();
    }

    private Stream<Individual> createRandomIndividuals(int chromosomeLength, Encoding encoding, int quantity) {
        return IntStream.range(0, quantity)
                .mapToObj(i -> createRandomIndividual(chromosomeLength, encoding));
    }

    private Stream<Individual> createRandomNotOptimalIndividuals(FitnessFunctionV2<?, ?> function, Encoding encoding, int quantity) {
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

    private int getOptimalQuantity(PopulationConfiguration populationConfiguration) {
        return populationConfiguration.getOptimalQuantity()
                .orElseThrow(() -> new IllegalArgumentException("Provided population configuration " + populationConfiguration
                        + "does not contain required optimal quantity value for population type " + populationConfiguration.getPopulationType() + " !"));
    }

    private double getOptimalPercentage(PopulationConfiguration populationConfiguration) {
        return populationConfiguration.getOptimalPercentage()
                .orElseThrow(() -> new IllegalArgumentException("Provided population configuration " + populationConfiguration
                        + "does not contain required optimal percentage value for population type " + populationConfiguration.getPopulationType() + " !"));
    }
}
