package lab.encoding;

import lab.parameters.FitnessFunction;
import lab.utils.GeneticUtils;

public class Decoder {

    public static double decode(String toDecode, FitnessFunction fitnessFunction) {
        float result = 0;
        switch (GeneticUtils.ENCODING) {
            case GRAY -> result = decodeGray(toDecode);
            case BINARY -> result = decodeBinary(toDecode);
        }

        if (fitnessFunction == FitnessFunction.F2)
            result -= 512;

        return Math.round(result) / 100.;
    }

    private static float decodeBinary(String toDecode) {
        char[] bits = toDecode.toCharArray();
        float res = 0;
        for (int i = bits.length - 1, j = 0; i >= 0; i--, j++) {
            res += bits[i] == '1' ? Math.pow(2, j) : 0;
        }
        return res;
    }


    private static float decodeGray(String toDecode) {
        int num = Integer.parseInt(toDecode, 2);
        int mask = num;
        while ((mask = mask >> 1) != 0) {
            num = num ^ mask;
        }
        return num;
    }

}
