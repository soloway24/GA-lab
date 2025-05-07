package lab.encoding;

import lab.Individual;
import lab.function.FitnessFunction;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

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
        return decode(individual.getBinaryCode(), individual.getEncoding(), fitnessFunction);
    }

    public static <ARG_T extends Number> List<ARG_T> decodeMultipleArguments(Individual individual, FitnessFunction<ARG_T, ?> fitnessFunction) {
        String binaryCode = individual.getBinaryCode();
        int arity = fitnessFunction.getArity();
        int chromosomeLength = fitnessFunction.getChromosomeLength();

        verifyMultipleArgumentsBinaryCodes(binaryCode, arity, chromosomeLength);
        List<String> binaryCodes = getMultipleArgumentCodes(binaryCode, arity, chromosomeLength);

        return binaryCodes.stream()
                .map(code -> Decoder.decode(code, individual.getEncoding(), fitnessFunction))
                .toList();
    }

    private static <ARG_T extends Number> ARG_T decode(String binaryCode, Encoding encoding, FitnessFunction<ARG_T, ?> fitnessFunction) {
        long decimalValue = getDecimalValue(binaryCode, encoding);
        return fitnessFunction.convertToX(decimalValue)
                .orElseThrow(() -> new IllegalStateException("Provided function " + fitnessFunction.getName()
                        + " does not support decoding of individuals."));
    }

    private static long getDecimalValue(String binaryCode, Encoding encoding) {
        verifyBinaryCode(binaryCode);

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

    public static List<String> getMultipleArgumentCodes(String multipleArgumentsCode, int arity, int chromosomeLength) {
        return IntStream.range(0, arity)
                .mapToObj(i -> multipleArgumentsCode.substring(i * chromosomeLength, (i + 1) * chromosomeLength))
                .toList();
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

    private static void verifyMultipleArgumentsBinaryCodes(String multipleArgumentsCode, int arity, int chromosomeLength) {
        if (multipleArgumentsCode.length() != arity * chromosomeLength) {
            throw new IllegalArgumentException("Binary code with multiple arguments has length " + multipleArgumentsCode.length()
                    + " which is not equal to argument quantity * chromosome length " + arity * chromosomeLength);
        }
    }
}
