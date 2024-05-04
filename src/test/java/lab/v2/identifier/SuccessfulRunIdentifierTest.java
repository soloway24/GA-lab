package lab.v2.identifier;

import lab.v2.Individual;
import lab.v2.function.FitnessFunctionV2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static java.util.Optional.of;
import static lab.parameters.Encoding.STANDARD;
import static lab.v2.Constants.ALLOWED_FITNESS_DELTA;
import static lab.v2.Constants.ALLOWED_X_SIGMA;
import static lab.v2.identifier.SuccessfulRunIdentifier.isSuccessfulRealFunction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuccessfulRunIdentifierTest {

    private static final Individual ALLOWED_X_INDIVIDUAL = new Individual("1111111110", STANDARD);
    private static final Individual NOT_ALLOWED_X_INDIVIDUAL_1 = new Individual("1111111101", STANDARD);
    private static final Individual NOT_ALLOWED_X_INDIVIDUAL_2 = new Individual("1111111101", STANDARD);

    private static final double MAX_FITNESS = 10.23;
    private static final double ALLOWED_FITNESS = MAX_FITNESS - ALLOWED_FITNESS_DELTA;
    private static final double NOT_ALLOWED_FITNESS = ALLOWED_FITNESS - ALLOWED_FITNESS_DELTA;
    private static final double SECOND_NOT_ALLOWED_FITNESS = ALLOWED_FITNESS - 2 * ALLOWED_FITNESS_DELTA;

    private static final double MAX_X = 10.23;
    private static final double ALLOWED_X = MAX_X - ALLOWED_X_SIGMA;
    private static final double NOT_ALLOWED_X = MAX_X - 2 * ALLOWED_X_SIGMA;

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
    private FitnessFunctionV2<Double, Double> function;

    @Test
    public void whenRealFunctionAndConvergeThenTrue() {
        initMocks();
        when(function.convertToX(ALLOWED_X_DECIMAL)).thenReturn(of(ALLOWED_X));

        assertThat(isSuccessfulRealFunction(function, ALLOWED_ALL_INDIVIDUALS, true), equalTo(true));
    }

    @Test
    public void whenRealFunctionAndConvergedAndNotAllowedBestFitnessThenFalse() {
        initMocks();
        when(function.convertToX(ALLOWED_X_DECIMAL)).thenReturn(of(ALLOWED_X));

        assertThat(isSuccessfulRealFunction(function, NOT_ALLOWED_FITNESS_INDIVIDUALS, true), equalTo(false));
    }

    @Test
    public void whenRealFunctionAndConvergedAndNotAllowedBestXThenFalse() {
        initMocks();
        when(function.convertToX(NOT_ALLOWED_X_DECIMAL)).thenReturn(of(NOT_ALLOWED_X));

        assertThat(isSuccessfulRealFunction(function, NOT_ALLOWED_X_INDIVIDUALS, true), equalTo(false));
    }

    @Test
    public void whenRealFunctionAndNotConvergedThenFalse() {
        assertThat(isSuccessfulRealFunction(function, ALLOWED_ALL_INDIVIDUALS, false), equalTo(false));
    }

    private void initMocks() {
        when(function.getOptimalX()).thenReturn(of(MAX_X));
        when(function.getMaxFitness()).thenReturn(MAX_FITNESS);
    }
}