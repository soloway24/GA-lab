package lab.utils;

import org.junit.jupiter.api.Test;

import static lab.utils.EncodingSpaceValidator.validateEncodingSpace;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EncodingSpaceValidatorTest {

    private static final int CHROMOSOME_LENGTH = 10;
    private static final double MIN_X = 0.0;
    private static final int DOUBLE_ARGUMENT_PRECISION = 2;
    private static final int INTEGER_ARGUMENT_PRECISION = 0;

    @Test
    public void whenValidFunctionThenSuccess() {
        double maxX = 10.23;

        validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION);
    }

    @Test
    public void whenValidFunctionWithIntegerArgumentsThenSuccess() {
        double maxX = 1023.0;

        validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, INTEGER_ARGUMENT_PRECISION);
    }

    @Test
    public void whenMinGreaterThanMaxThenFailure() {
        double maxX = -10.23;

        assertThrows(IllegalArgumentException.class,
                () -> validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION),
                "Provided min x " + MIN_X + " is greater than max x " + maxX + " !");
    }

    @Test
    public void whenWrongArgumentPrecisionThenFailure() {
        double maxX = 10.231;

        assertThrows(IllegalArgumentException.class,
                () -> validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION),
                "Provided argument precision " + DOUBLE_ARGUMENT_PRECISION
                        + " is is greater than actual encoding space precision 3 !"
                        + " Verify the precision of minX = " + MIN_X + " and maxX = " + maxX + ".");
    }

    @Test
    public void whenInsufficientEncodingSpaceThenFailure() {
        double maxX = 10.24;

        assertThrows(IllegalArgumentException.class,
                () -> validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION),
                "Chromosome with the provided length " + CHROMOSOME_LENGTH
                        + " can encode 1024 arguments with the precision of 2 while the expected number is 1025 !");
    }

    @Test
    public void whenExcessiveEncodingSpaceThenFailure() {
        double maxX = 10.22;

        assertThrows(IllegalArgumentException.class,
                () -> validateEncodingSpace(CHROMOSOME_LENGTH, MIN_X, maxX, DOUBLE_ARGUMENT_PRECISION),
                "Chromosome with the provided length " + CHROMOSOME_LENGTH
                        + " encodes 1024 arguments with the precision of 2 while the expected number is 1023 !");
    }
}