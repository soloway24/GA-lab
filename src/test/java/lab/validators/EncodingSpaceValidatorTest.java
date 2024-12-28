package lab.validators;

import org.junit.jupiter.api.Test;

import static java.math.BigDecimal.valueOf;
import static lab.validators.EncodingSpaceValidator.validateEncodingSpace;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EncodingSpaceValidatorTest {

    private static final int CHROMOSOME_LENGTH = 10;
    private static final double MIN_X = 0.0;
    private static final int DOUBLE_ARGUMENT_PRECISION = 2;
    private static final int ZERO_ARGUMENT_PRECISION = 0;

    @Test
    public void whenValidFunctionThenSuccess() {
        double maxX = 10.23;

        validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION);
    }

    @Test
    public void whenValidFunctionWithIntegerArgumentsThenSuccess() {
        double maxX = 1023.0;

        validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, ZERO_ARGUMENT_PRECISION);
    }

    @Test
    public void whenMinGreaterThanMaxThenFailure() {
        double maxX = -10.23;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION));

        String expectedMessage = "Provided min x " + MIN_X + " is greater than max x " + maxX + ".";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void whenEncodingSpacePrecisionGreaterThanArgumentPrecisionThenFailure() {
        double maxX = 10.231;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION));

        String expectedMessage = "Provided argument precision " + DOUBLE_ARGUMENT_PRECISION
                + " is less than actual encoding space precision " + valueOf(maxX).scale() + "."
                + " Verify the precision of minX = " + MIN_X + " and maxX = " + maxX + ".";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void whenInsufficientChromosomeLengthThenFailure() {
        double maxX = 10.24;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION));

        String expectedRegex = "Chromosome with the provided length " + CHROMOSOME_LENGTH
                + " can encode \\d+ arguments with the precision of " + DOUBLE_ARGUMENT_PRECISION +
                " while the required number is \\d+\\.";
        assertThat(exception.getMessage(), matchesPattern(expectedRegex));
    }

    @Test
    public void whenExcessiveChromosomeLengthThenFailure() {
        double maxX = 10.22;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION));

        String expectedRegex = "Chromosome with the provided length " + CHROMOSOME_LENGTH
                + " can encode \\d+ arguments with the precision of " + DOUBLE_ARGUMENT_PRECISION +
                " while the required number is \\d+\\.";
        assertThat(exception.getMessage(), matchesPattern(expectedRegex));
    }
}