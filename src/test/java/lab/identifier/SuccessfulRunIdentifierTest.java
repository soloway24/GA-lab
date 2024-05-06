package lab.identifier;

import lab.Constants;
import lab.Individual;
import lab.function.FitnessFunction;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static java.util.Optional.of;
import static lab.encoding.Encoding.STANDARD;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuccessfulRunIdentifierTest {

    private static final Individual ALLOWED_X_INDIVIDUAL = new Individual("1111111110", STANDARD);
    private static final Individual NOT_ALLOWED_X_INDIVIDUAL_1 = new Individual("1111111101", STANDARD);
    private static final Individual NOT_ALLOWED_X_INDIVIDUAL_2 = new Individual("1111111101", STANDARD);

    private static final double MAX_FITNESS = 10.23;
    private static final double ALLOWED_FITNESS = MAX_FITNESS - Constants.ALLOWED_FITNESS_DELTA;
    private static final double NOT_ALLOWED_FITNESS = ALLOWED_FITNESS - Constants.ALLOWED_FITNESS_DELTA;
    private static final double SECOND_NOT_ALLOWED_FITNESS = ALLOWED_FITNESS - 2 * Constants.ALLOWED_FITNESS_DELTA;

    private static final double MAX_X = 10.23;
    private static final double ALLOWED_X = MAX_X - Constants.ALLOWED_X_SIGMA;
    private static final double NOT_ALLOWED_X = MAX_X - 2 * Constants.ALLOWED_X_SIGMA;

    private static final long ALLOWED_X_DECIMAL = (long) (ALLOWED_X * 100);
    private static final long NOT_ALLOWED_X_DECIMAL = (long) (NOT_ALLOWED_X * 100);

    private static final Map<Individual, Double> ALLOWED_ALL_INDIVIDUALS = Map.of(
            ALLOWED_X_INDIVIDUAL, ALLOWED_FITNESS,
            NOT_ALLOWED_X_INDIVIDUAL_1, NOT_ALLOWED_FITNESS
    );
    private static final Map<Individual, Double> NOT_ALLOWED_FITNESS_INDIVIDUALS = Map.of(
            ALLOWED_X_INDIVIDUAL, NOT_ALLOWED_FITNESS,
            NOT_ALLOWED_X_INDIVIDUAL_1, SECOND_NOT_ALLOWED_FITNESS
    );
    private static final Map<Individual, Double> NOT_ALLOWED_X_INDIVIDUALS = Map.of(
            NOT_ALLOWED_X_INDIVIDUAL_1, ALLOWED_FITNESS,
            NOT_ALLOWED_X_INDIVIDUAL_2, ALLOWED_FITNESS
    );

    @Mock
    private FitnessFunction<Double, Double> function;

    @Test
    public void whenRealFunctionAndConvergeThenTrue() {
        initMocks();
        when(function.convertToX(ALLOWED_X_DECIMAL)).thenReturn(of(ALLOWED_X));

        MatcherAssert.assertThat(SuccessfulRunIdentifier.isSuccessfulRealFunction(function, ALLOWED_ALL_INDIVIDUALS, true), equalTo(true));
    }

    @Test
    public void whenRealFunctionAndConvergedAndNotAllowedBestFitnessThenFalse() {
        initMocks();
        when(function.convertToX(ALLOWED_X_DECIMAL)).thenReturn(of(ALLOWED_X));

        MatcherAssert.assertThat(SuccessfulRunIdentifier.isSuccessfulRealFunction(function, NOT_ALLOWED_FITNESS_INDIVIDUALS, true), equalTo(false));
    }

    @Test
    public void whenRealFunctionAndConvergedAndNotAllowedBestXThenFalse() {
        initMocks();
        when(function.convertToX(NOT_ALLOWED_X_DECIMAL)).thenReturn(of(NOT_ALLOWED_X));

        MatcherAssert.assertThat(SuccessfulRunIdentifier.isSuccessfulRealFunction(function, NOT_ALLOWED_X_INDIVIDUALS, true), equalTo(false));
    }

    @Test
    public void whenRealFunctionAndNotConvergedThenFalse() {
        MatcherAssert.assertThat(SuccessfulRunIdentifier.isSuccessfulRealFunction(function, ALLOWED_ALL_INDIVIDUALS, false), equalTo(false));
    }

    private void initMocks() {
        when(function.getOptimalX()).thenReturn(of(MAX_X));
        when(function.getMaxFitness()).thenReturn(MAX_FITNESS);
    }
}