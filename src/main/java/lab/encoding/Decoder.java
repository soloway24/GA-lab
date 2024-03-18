package lab.encoding;

import lab.function.FitnessFunction;
import lab.function.FitnessFunctionV2;
import lab.model.Individual;
import lab.parameters.Encoding;
import lab.utils.GeneticUtils;

import java.util.Map;
import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;
import static lab.utils.Constants.BINARY_BASE;
import static lab.utils.Constants.MAX_CHROMOSOME_LENGTH;

public class Decoder {

    private static final Map<Encoding, Function<String, Long>> ENCODING_TO_DECODER =
            Map.of(
                    Encoding.STANDARD, Decoder::decodeStandardV2,
                    Encoding.GRAY, Decoder::decodeGrayV2
            );

    public static <ARG_T> ARG_T decode(Individual individual, FitnessFunctionV2<ARG_T, ?> fitnessFunction) {
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

    public static double decode(String toDecode, FitnessFunction fitnessFunction) {
        float result = 0;
        switch (GeneticUtils.ENCODING) {
            case GRAY -> result = decodeGray(toDecode);
            case STANDARD -> result = decodeStandard(toDecode);
        }

        if (fitnessFunction == FitnessFunction.QUAD_SYM)
            result -= 512;

        return Math.round(result) / 100.;
    }

    private static float decodeStandard(String toDecode) {
        char[] bits = toDecode.toCharArray();
        float res = 0;
        for (int i = bits.length - 1, j = 0; i >= 0; i--, j++) {
            res += bits[i] == '1' ? Math.pow(2, j) : 0;
        }
        return res;
    }

    private static float decodeGray(String toDecode) {
        int decimalValue = parseInt(toDecode, 2);
        int mask = decimalValue;
        while ((mask = mask >> 1) != 0) {
            decimalValue = decimalValue ^ mask;
        }
        return decimalValue;
    }

    private static long decodeStandardV2(String toDecode) {
        return parseLong(toDecode, BINARY_BASE);
    }

    private static long decodeGrayV2(String toDecode) {
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
