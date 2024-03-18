package lab.encoding;

import lab.function.FitnessFunctionV2;
import lab.model.Individual;
import lab.parameters.Encoding;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static lab.encoding.Decoder.decode;
import static lab.model.Individual.ALL_100_ZEROS_INDIVIDUAL;
import static lab.utils.Constants.MAX_CHROMOSOME_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecoderTest {

    private static final Individual STANDARD_INDIVIDUAL = new Individual("1000010000", Encoding.STANDARD);
    private static final Individual GRAY_INDIVIDUAL = new Individual("1000010000", Encoding.GRAY);
    private static final Individual INVALID_INDIVIDUAL = new Individual("iooooioooo");
    private static final long DECIMAL_STANDARD = 528;
    private static final long DECIMAL_GRAY = 992;

    @Mock
    private FitnessFunctionV2<Double, Double> doubleFunction;
    @Mock
    private FitnessFunctionV2<Double, Double> nonDecodableDoubleFunction;

    @Test
    public void whenStandardBinaryCodeIsValidThenSuccess() {
        double expectedX = DECIMAL_STANDARD / 100.0;
        when(doubleFunction.convertToX(DECIMAL_STANDARD)).thenReturn(of(expectedX));

        double actualX = decode(STANDARD_INDIVIDUAL, doubleFunction);
        assertEquals(expectedX, actualX);
    }

    @Test
    public void whenGrayBinaryCodeIsValidThenSuccess() {
        double expectedX = DECIMAL_GRAY / 100.0;
        when(doubleFunction.convertToX(DECIMAL_GRAY)).thenReturn(of(expectedX));

        double actualX = decode(GRAY_INDIVIDUAL, doubleFunction);
        assertEquals(expectedX, actualX);
    }

    @Test
    public void whenBinaryCodeIsTooLongThenFailure() {
        assertThrows(IllegalArgumentException.class,
                () -> decode(ALL_100_ZEROS_INDIVIDUAL, doubleFunction),
                "Decoding of individuals of the length greater than " + MAX_CHROMOSOME_LENGTH
                        + "is not supported!");
    }

    @Test
    public void whenBinaryCodeContainsInvalidCharactersThenFailure() {
        assertThrows(IllegalArgumentException.class,
                () -> decode(INVALID_INDIVIDUAL, doubleFunction),
                "String " + INVALID_INDIVIDUAL.getBinaryCode()
                        + " contains non-binary characters during binary decoding!");
    }

    @Test
    public void whenDecodingNotSupportedThenFailure() {
        when(nonDecodableDoubleFunction.convertToX(anyLong())).thenReturn(empty());
        when(nonDecodableDoubleFunction.getName()).thenReturn("Non-Decodable Double Function");

        assertThrows(IllegalStateException.class,
                () -> decode(STANDARD_INDIVIDUAL, nonDecodableDoubleFunction),
                "Provided function " + nonDecodableDoubleFunction.getName()
                        + " does not support decoding of individuals.");
    }
}