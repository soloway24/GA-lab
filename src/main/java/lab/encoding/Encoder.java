package lab.encoding;

import lab.parameters.Encoding;
import lab.parameters.FitnessFunction;
import lab.utils.GeneticUtils;

import static lab.utils.GeneticUtils.ENCODING_LENGTH;

public class Encoder {

    public static String encode(float toEncode, Encoding encoding) {
        StringBuilder result = new StringBuilder();

        if (GeneticUtils.FITNESS_FUNCTION == FitnessFunction.QUAD_SYM)
            toEncode += 5.11f;

        switch (encoding) {
            case GRAY -> result.append(encodeGray(toEncode));
            case STANDARD -> result.append(encodeStandard(toEncode));
        }

        int diff = ENCODING_LENGTH - result.length();
        while (diff > 0) {
            result.insert(0, '0');
            diff--;
        }
        return result.toString();
    }

    private static String encodeStandard(float number) {
        return Integer.toBinaryString((Math.round(number * 100)));
    }

    private static String encodeGray(float number) {
        String standardBinary = encodeStandard(number);
        StringBuilder gray = new StringBuilder();
        char[] bits = standardBinary.toCharArray();

        gray.append(bits[0]);
        for (int i = 1; i < bits.length; i++) {
            gray.append(XOR(bits[i - 1], bits[i]));
        }
        return gray.toString();
    }

    private static int XOR(char a, char b) {
        return a != b ? 1 : 0;
    }


    public static void main(String[] args) {
        float toEncode = 2.52f;
        System.out.println(encodeGray(toEncode));
        System.out.println(encodeStandard(toEncode));
    }
}
