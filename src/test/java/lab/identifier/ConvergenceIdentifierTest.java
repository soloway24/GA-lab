package lab.identifier;

import lab.Individual;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static lab.encoding.Encoding.STANDARD;
import static lab.identifier.ConvergenceIdentifier.*;
import static lab.operator.OperatorType.CROSSOVER;
import static lab.operator.OperatorType.NONE;
import static lab.util.CalculationUtils.getIndexedIndividuals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ConvergenceIdentifierTest {

    private static final double SAME_PERCENTAGE = 0.9;

    private static final Individual INDIVIDUAL_1 = new Individual("0000000000", STANDARD);
    private static final Individual INDIVIDUAL_2 = new Individual("1000000000", STANDARD);
    private static final Map<Individual, Integer> ALL_SAME_INDIVIDUALS = Map.of(
            INDIVIDUAL_1, 100
    );
    private static final Map<Individual, Integer> HOMOGENOUS_INDIVIDUALS = Map.of(
            INDIVIDUAL_1, 99,
            INDIVIDUAL_2, 1
    );
    private static final Map<Individual, Integer> NOT_HOMOGENOUS_INDIVIDUALS = Map.of(
            INDIVIDUAL_1, 98,
            INDIVIDUAL_2, 2
    );
    private static final Map<Individual, Integer> SAME_90_INDIVIDUALS = Map.of(
            INDIVIDUAL_1, 90,
            INDIVIDUAL_2, 10
    );
    private static final Map<Individual, Integer> SAME_89_INDIVIDUALS = Map.of(
            INDIVIDUAL_1, 89,
            INDIVIDUAL_2, 11
    );

    private final ConvergenceIdentifier convergenceIdentifier = new ConvergenceIdentifier();

    @Test
    public void whenNoneOperatorThenConverged() {
        Deque<Double> prevAvgFs = new LinkedList<>();
        assertThat(convergenceIdentifier.hasConverged(buildIndividuals(ALL_SAME_INDIVIDUALS), prevAvgFs, NONE), equalTo(true));
    }

    @Test
    public void whenNoneOperatorThenNotConverged() {
        Deque<Double> prevAvgFs = new LinkedList<>();
        assertThat(convergenceIdentifier.hasConverged(buildIndividuals(HOMOGENOUS_INDIVIDUALS), prevAvgFs, NONE), equalTo(false));
    }

    @Test
    public void whenSomeOperatorThenConverged() {
        Deque<Double> prevAvgFs = new LinkedList<>();
        assertThat(convergenceIdentifier.hasConverged(buildIndividuals(HOMOGENOUS_INDIVIDUALS), prevAvgFs, CROSSOVER), equalTo(true));
    }

    @Test
    public void whenSomeOperatorThenNotConverged() {
        Deque<Double> prevAvgFs = new LinkedList<>();
        assertThat(convergenceIdentifier.hasConverged(buildIndividuals(NOT_HOMOGENOUS_INDIVIDUALS), prevAvgFs, CROSSOVER), equalTo(false));
    }

    @Test
    public void whenAreAllTheSameThenTrue() {
        assertThat(areAllTheSame(buildIndividuals(ALL_SAME_INDIVIDUALS).keySet()), equalTo(true));
    }

    @Test
    public void whenAreAllTheEqualToThenTrue() {
        assertThat(areAllEqualTo(buildIndividuals(ALL_SAME_INDIVIDUALS).keySet(), INDIVIDUAL_1), equalTo(true));
    }

    @Test
    public void when100SameAndAreTheSameWithPercentageThenTrue() {
        assertThat(areTheSameWithPercentage(buildIndividuals(ALL_SAME_INDIVIDUALS).keySet(), SAME_PERCENTAGE), equalTo(true));
    }

    @Test
    public void when90SameAndAreTheSameWithPercentageThenTrue() {
        assertThat(areTheSameWithPercentage(buildIndividuals(SAME_90_INDIVIDUALS).keySet(), SAME_PERCENTAGE), equalTo(true));
    }

    @Test
    public void when89SameAndAreTheSameWithPercentageThenFalse() {
        assertThat(areTheSameWithPercentage(buildIndividuals(SAME_89_INDIVIDUALS).keySet(), SAME_PERCENTAGE), equalTo(false));
    }

    @Test
    public void when100SameAndAreEqualToWithPercentageThenTrue() {
        assertThat(areEqualToWithPercentage(buildIndividuals(ALL_SAME_INDIVIDUALS).keySet(), INDIVIDUAL_1, SAME_PERCENTAGE), equalTo(true));
    }

    @Test
    public void when90SameAndAreEqualToWithPercentageThenTrue() {
        assertThat(areEqualToWithPercentage(buildIndividuals(SAME_90_INDIVIDUALS).keySet(), INDIVIDUAL_1, SAME_PERCENTAGE), equalTo(true));
    }

    @Test
    public void when890SameAndAreEqualToWithPercentageThenTrue() {
        assertThat(areEqualToWithPercentage(buildIndividuals(SAME_89_INDIVIDUALS).keySet(), INDIVIDUAL_1, SAME_PERCENTAGE), equalTo(false));
    }

    private Map<Individual, ? extends Number> buildIndividuals(Map<Individual, Integer> individualToQuantity) {
        List<Individual> individuals = new ArrayList<>();
        individualToQuantity.forEach(
                (individual, quantity) -> {
                    IntStream.range(0, quantity).forEach(i -> individuals.add(individual));
                });
        return getIndexedIndividuals(individuals).stream()
                .collect(toUnmodifiableMap(identity(), ind -> 1));
    }
}