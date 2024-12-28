package lab.encoding;

import lab.function.FitnessFunction;
import lab.Individual;

import java.util.Map;
import java.util.function.Function;

import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;
import static lab.util.Constants.BINARY_BASE;
import static lab.util.Constants.MAX_CHROMOSOME_LENGTH;

public class Decoder {

    private static final Map<Encoding, Function<String, Long>> ENCODING_TO_DECODER =
            Map.of(
                    Encoding.STANDARD, Decoder::decodeStandard,
                    Encoding.GRAY, Decoder::decodeGray
            );

    public static <ARG_T extends Number> ARG_T decode(Individual individual, FitnessFunction<ARG_T, ?> fitnessFunction) {
        long decimalValue = getDecimalValue(individual);
        return fitnessFunction.convertToX(decimalValue)
                .orElseThrow(() -> new IllegalStateException("Provided function " + fitnessFunction.getName()
                        + " does not support decoding of individuals."));
    }

    private static long getDecimalValue(Individual individual) {
        String binaryCode = individual.getBinaryCode();
        verifyBinaryCode(binaryCode);

        Encoding encoding = individual.getEncoding();
        return ofNullable(ENCODING_TO_DECODER.get(encoding))
                .map(decoder -> decoder.apply(binaryCode))
                .orElseThrow(() -> new IllegalStateException("No decoder exists for the provided Encoding type - " + encoding));
    }

    private static long decodeStandard(String toDecode) {
        return parseLong(toDecode, BINARY_BASE);
    }

    private static long decodeGray(String toDecode) {
        verifyBinaryCode(toDecode);
        long decimalValue = parseLong(toDecode, BINARY_BASE);
        long mask = decimalValue;
        while ((mask = mask >> 1) != 0) {
            decimalValue = decimalValue ^ mask;
        }
        return decimalValue;
    }

    private static void verifyBinaryCode(String toDecode) {
        if (toDecode.length() > MAX_CHROMOSOME_LENGTH) {
            throw new IllegalArgumentException("Decoding of individuals of the length greater than " + MAX_CHROMOSOME_LENGTH
                    + "is not supported!");
        }
        if (!toDecode.matches("[01]+")) {
            throw new IllegalArgumentException("String " + toDecode + " contains non-binary characters during binary decoding!");
        }
    }
}
