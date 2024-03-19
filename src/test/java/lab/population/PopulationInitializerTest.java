package lab.population;

import lab.function.FitnessFunctionV2;
import lab.model.Individual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static lab.parameters.Encoding.STANDARD;
import static lab.population.PopulationInitializer.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PopulationInitializerTest {

    private static final int CHROMOSOME_LENGTH = 10;
    private static final int POPULATION_SIZE = 100;
    private static final int OPTIMAL_SIZE = 1;
    private static final double ALL_OPTIMAL_PERCENTAGE = 1.0;
    private static final double OVER_SCALED_OPTIMAL_PERCENTAGE = 0.555;
    private static final int EXCESSIVE_SCAlE = 3;
    private static final Individual OPTIMAL = new Individual("1111011110");

    @Mock
    private FitnessFunctionV2<Double, Double> function;

    @Test
    public void whenInitializeRandomPopulationThenSuccess() {
        List<Individual> population = initializeRandomPopulation(CHROMOSOME_LENGTH, STANDARD, POPULATION_SIZE);
        assertThat(population, hasSize(POPULATION_SIZE));

        List<Individual> validIndividuals = population.stream()
                .filter(individual -> CHROMOSOME_LENGTH == individual.getBinaryCode().length())
                .toList();
        assertThat(validIndividuals, hasSize(POPULATION_SIZE));
    }

    @Test
    public void whenInitializeRandomPopulationWithoutOptimalThenSuccess() {
        when(function.getChromosomeLength()).thenReturn(CHROMOSOME_LENGTH);
        when(function.getOptimalIndividual(STANDARD)).thenReturn(OPTIMAL);

        List<Individual> population = initializeRandomPopulationWithoutOptimal(function, STANDARD, POPULATION_SIZE);
        assertThat(population, hasSize(POPULATION_SIZE));

        List<Individual> notOptimalValidIndividuals = getNotOptimalValidIndividuals(population);
        assertThat(notOptimalValidIndividuals, hasSize(POPULATION_SIZE));
    }

    @Test
    public void whenInitializeRandomPopulationWithOptimalThenSuccess() {
        when(function.getChromosomeLength()).thenReturn(CHROMOSOME_LENGTH);
        when(function.getOptimalIndividual(STANDARD)).thenReturn(OPTIMAL);

        List<Individual> population = initializeRandomPopulationWithOptimal(function, STANDARD, POPULATION_SIZE, OPTIMAL_SIZE);
        assertThat(population, hasSize(POPULATION_SIZE));

        List<Individual> notOptimalValidIndividuals = getNotOptimalValidIndividuals(population);
        assertThat(notOptimalValidIndividuals, hasSize(POPULATION_SIZE - OPTIMAL_SIZE));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.01, 0.99})
    public void whenInitializeRandomPopulationWithOptimalPercentageThenSuccess(double optimalPercentage) {
        when(function.getChromosomeLength()).thenReturn(CHROMOSOME_LENGTH);
        when(function.getOptimalIndividual(STANDARD)).thenReturn(OPTIMAL);

        List<Individual> population = initializeRandomPopulationWithOptimal(function, STANDARD, POPULATION_SIZE, optimalPercentage);
        assertThat(population, hasSize(POPULATION_SIZE));

        int optimalSize = (int) (optimalPercentage * POPULATION_SIZE);
        List<Individual> notOptimalValidIndividuals = getNotOptimalValidIndividuals(population);
        assertThat(notOptimalValidIndividuals, hasSize(POPULATION_SIZE - optimalSize));
    }

    @Test
    public void whenInitializeRandomPopulationWithAllOptimalPercentageThenSuccess() {
        when(function.getOptimalIndividual(STANDARD)).thenReturn(OPTIMAL);

        List<Individual> population = initializeRandomPopulationWithOptimal(function, STANDARD, POPULATION_SIZE, ALL_OPTIMAL_PERCENTAGE);
        assertThat(population, hasSize(POPULATION_SIZE));

        List<Individual> notOptimalValidIndividuals = getNotOptimalValidIndividuals(population);
        assertThat(notOptimalValidIndividuals, hasSize(0));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 1.01})
    public void whenInitializeRandomPopulationWithOptimalWithInvalidPercentageThenFailure(double invalidOptimalPercentage) {
        assertThrows(IllegalArgumentException.class,
                () -> initializeRandomPopulationWithOptimal(function, STANDARD, POPULATION_SIZE, invalidOptimalPercentage),
                "Provided percentage of optimal individuals in the population " + invalidOptimalPercentage
                        + " is not in the range of [0.0, 1.0]!");
    }

    @Test
    public void whenInitializeRandomPopulationWithOptimalWithOverScaledPercentageThenFailure() {
        assertThrows(IllegalArgumentException.class,
                () -> initializeRandomPopulationWithOptimal(function, STANDARD, POPULATION_SIZE, OVER_SCALED_OPTIMAL_PERCENTAGE),
                "Provided percentage of optimal individuals in the population " + OVER_SCALED_OPTIMAL_PERCENTAGE
                        + " should have max scale of 2 in order to represent a whole number of individuals, but has scale of "
                        + EXCESSIVE_SCAlE + " !");
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

}