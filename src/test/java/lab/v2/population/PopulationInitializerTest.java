package lab.v2.population;

import lab.model.Individual;
import lab.v2.function.FitnessFunctionV2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static lab.parameters.Encoding.STANDARD;
import static lab.v2.population.PopulationType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PopulationInitializerTest {

    private static final int POPULATION_SIZE = 100;
    private static final int CHROMOSOME_LENGTH = 10;
    private static final Individual OPTIMAL = new Individual("1111011110");

    private final PopulationInitializer populationInitializer = new PopulationInitializer(PopulationTypeValidator.getInstance());

    @Mock
    private FitnessFunctionV2<Double, Double> function;

    @Test
    public void whenInitializeRandomPopulationThenSuccess() {
        when(function.getChromosomeLength()).thenReturn(CHROMOSOME_LENGTH);

        PopulationConfiguration populationConfiguration = new PopulationConfiguration(function, RANDOM, STANDARD, POPULATION_SIZE);

        Population population = populationInitializer.initializePopulation(populationConfiguration);
        assertThat(population.getSize(), is(POPULATION_SIZE));

        List<Individual> validIndividuals = population.individuals().stream()
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

    @Test
    public void whenPopulationConfigRequiresOptimalAndFunctionDoesNotSupportEncodingThenFailure() {
        when(function.getOptimalIndividual(STANDARD)).thenReturn(empty());

        PopulationConfiguration populationConfiguration = new PopulationConfiguration(function, ONE_OPTIMAL, STANDARD, POPULATION_SIZE);

        assertThrows(IllegalStateException.class,
                () -> populationInitializer.initializePopulation(populationConfiguration));
    }

    private void testInitializeOptimalQuantityPopulation(PopulationType optimalQuantityConfig) {
        when(function.getChromosomeLength()).thenReturn(CHROMOSOME_LENGTH);
        when(function.getOptimalIndividual(STANDARD)).thenReturn(of(OPTIMAL));

        PopulationConfiguration populationConfiguration = new PopulationConfiguration(function, optimalQuantityConfig, STANDARD, POPULATION_SIZE);

        Population population = populationInitializer.initializePopulation(populationConfiguration);
        assertThat(population.getSize(), is(POPULATION_SIZE));

        int optimalQuantity = getOptimalQuantity(optimalQuantityConfig);
        List<Individual> notOptimalValidIndividuals = getNotOptimalValidIndividuals(population.individuals());
        assertThat(notOptimalValidIndividuals, hasSize(POPULATION_SIZE - optimalQuantity));
    }

    private void testInitializeOptimalPercentagePopulation(PopulationType optimalPercentageConfig) {
        when(function.getChromosomeLength()).thenReturn(CHROMOSOME_LENGTH);
        when(function.getOptimalIndividual(STANDARD)).thenReturn(of(OPTIMAL));

        PopulationConfiguration populationConfiguration = new PopulationConfiguration(function, optimalPercentageConfig, STANDARD, POPULATION_SIZE);

        Population population = populationInitializer.initializePopulation(populationConfiguration);
        assertThat(population.getSize(), is(POPULATION_SIZE));

        int optimalQuantity = (int) (getOptimalPercentage(optimalPercentageConfig) * POPULATION_SIZE);
        List<Individual> notOptimalValidIndividuals = getNotOptimalValidIndividuals(population.individuals());
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

    private int getOptimalQuantity(PopulationType populationType) {
        return populationType.getOptimalQuantity()
                .orElseThrow(IllegalArgumentException::new);
    }

    private double getOptimalPercentage(PopulationType populationType) {
        return populationType.getOptimalPercentage()
                .orElseThrow(IllegalArgumentException::new);
    }
}