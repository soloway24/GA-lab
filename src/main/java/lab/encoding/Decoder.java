package lab.encoding;

import lab.function.FitnessFunction;
import lab.utils.GeneticUtils;

import static java.lang.Integer.parseInt;

public class Decoder {

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
}
