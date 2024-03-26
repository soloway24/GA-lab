package lab.v2;

import lab.model.Individual;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static lab.parameters.Encoding.STANDARD;
import static lab.v2.operator.OperatorType.CROSSOVER;
import static lab.v2.operator.OperatorType.NONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ConvergenceIdentifierTest {

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

    private final ConvergenceIdentifier convergenceIdentifier = new ConvergenceIdentifier();

    @Test
    public void whenNoneOperatorThenConverged() {
        assertThat(convergenceIdentifier.hasConverged(buildIndividuals(ALL_SAME_INDIVIDUALS), NONE), equalTo(true));
    }

    @Test
    public void whenNoneOperatorThenNotConverged() {
        assertThat(convergenceIdentifier.hasConverged(buildIndividuals(HOMOGENOUS_INDIVIDUALS), NONE), equalTo(false));
    }

    @Test
    public void whenSomeOperatorThenConverged() {
        assertThat(convergenceIdentifier.hasConverged(buildIndividuals(HOMOGENOUS_INDIVIDUALS), CROSSOVER), equalTo(true));
    }

    @Test
    public void whenSomeOperatorThenNotConverged() {
        assertThat(convergenceIdentifier.hasConverged(buildIndividuals(NOT_HOMOGENOUS_INDIVIDUALS), CROSSOVER), equalTo(false));
    }

    private List<Individual> buildIndividuals(Map<Individual, Integer> individualToQuantity) {
        List<Individual> individuals = new ArrayList<>();
        individualToQuantity.forEach(
                (individual, quantity) -> {
                    IntStream.range(0, quantity).forEach(i -> individuals.add(individual));
                });
        return individuals;
    }
}