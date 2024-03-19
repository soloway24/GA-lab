package lab.v2.population;

import lab.model.Individual;
import lab.v2.function.FitnessFunctionV2;
import lab.v2.parameters.RunConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lab.parameters.Encoding.STANDARD;
import static lab.v2.parameters.OperatorsApplicationType.NONE;
import static lab.v2.population.PopulationConfiguration.*;
import static lab.v2.population.PopulationInitializer.POPULATION_INITIALIZER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PopulationInitializerTest {

    private static final int POPULATION_SIZE = 100;
    private static final int CHROMOSOME_LENGTH = 10;
    private static final Individual OPTIMAL = new Individual("1111011110");

    @Mock
    private FitnessFunctionV2<Double, Double> function;

    @Test
    public void whenInitializeRandomPopulationThenSuccess() {
        when(function.getChromosomeLength()).thenReturn(CHROMOSOME_LENGTH);

        RunConfiguration runConfiguration = new RunConfiguration(POPULATION_SIZE, function, NONE, RANDOM, STANDARD);

        List<Individual> population = POPULATION_INITIALIZER.initializePopulation(runConfiguration);
        assertThat(population, hasSize(POPULATION_SIZE));

        List<Individual> validIndividuals = population.stream()
                .filter(this::hasLength)
                .toList();
        assertThat(validIndividuals, hasSize(POPULATION_SIZE));
    }

    @Test
    public void whenInitializeZeroOptimalPopulationThenSuccess() {
        testInitializeOptimalQuantityPopulation(ZERO_OPTIMAL);
    }

    @Test
    public void whenInitializeOneOptimalPopulationThenSuccess() {
        testInitializeOptimalQuantityPopulation(ONE_OPTIMAL);
    }

    @Test
    public void whenInitializeRandomPopulationWithOptimalPercentageThenSuccess() {
        testInitializeOptimalPercentagePopulation(FIVE_PERCENT_OPTIMAL);
    }

    @Test
    public void whenInitializeRandomPopulationWithAllOptimalPercentageThenSuccess() {
        testInitializeOptimalPercentagePopulation(TEN_PERCENT_OPTIMAL);
    }

    private void testInitializeOptimalQuantityPopulation(PopulationConfiguration optimalQuantityConfig) {
        when(function.getChromosomeLength()).thenReturn(CHROMOSOME_LENGTH);
        when(function.getOptimalIndividual(STANDARD)).thenReturn(OPTIMAL);

        RunConfiguration runConfiguration = new RunConfiguration(POPULATION_SIZE, function, NONE, optimalQuantityConfig, STANDARD);

        List<Individual> population = POPULATION_INITIALIZER.initializePopulation(runConfiguration);
        assertThat(population, hasSize(POPULATION_SIZE));

        int optimalQuantity = getOptimalQuantity(optimalQuantityConfig);
        List<Individual> notOptimalValidIndividuals = getNotOptimalValidIndividuals(population);
        assertThat(notOptimalValidIndividuals, hasSize(POPULATION_SIZE - optimalQuantity));
    }

    private void testInitializeOptimalPercentagePopulation(PopulationConfiguration optimalPercentageConfig) {
        when(function.getChromosomeLength()).thenReturn(CHROMOSOME_LENGTH);
        when(function.getOptimalIndividual(STANDARD)).thenReturn(OPTIMAL);

        RunConfiguration runConfiguration = new RunConfiguration(POPULATION_SIZE, function, NONE, optimalPercentageConfig, STANDARD);

        List<Individual> population = POPULATION_INITIALIZER.initializePopulation(runConfiguration);
        assertThat(population, hasSize(POPULATION_SIZE));

        int optimalQuantity = (int) (getOptimalPercentage(optimalPercentageConfig) * POPULATION_SIZE);
        List<Individual> notOptimalValidIndividuals = getNotOptimalValidIndividuals(population);
        assertThat(notOptimalValidIndividuals, hasSize(POPULATION_SIZE - optimalQuantity));
    }

    private List<Individual> getNotOptimalValidIndividuals(List<Individual> population) {
        return population.stream()
                .filter(individual -> hasLength(individual) && isNotOptimal(individual))
                .toList();
    }

    private boolean hasLength(Individual individual) {
        return individual.getBinaryCode().length() == CHROMOSOME_LENGTH;
    }

    private boolean isNotOptimal(Individual individual) {
        return !individual.getBinaryCode().equals(OPTIMAL.getBinaryCode());
    }

    private int getOptimalQuantity(PopulationConfiguration populationConfiguration) {
        return populationConfiguration.getOptimalQuantity()
                .orElseThrow(IllegalArgumentException::new);
    }

    private double getOptimalPercentage(PopulationConfiguration populationConfiguration) {
        return populationConfiguration.getOptimalPercentage()
                .orElseThrow(IllegalArgumentException::new);
    }
}