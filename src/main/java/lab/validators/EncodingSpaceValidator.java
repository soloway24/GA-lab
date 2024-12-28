package lab.validators;

import java.math.BigDecimal;
import java.math.BigInteger;

import static lab.util.Constants.BINARY_BASE;
import static lab.util.Constants.PRECISION_BASE;

public class EncodingSpaceValidator {

    public static void validateEncodingSpace(int chromosomeLength, double minX, double maxX, int argumentPrecision) {
        if (maxX < minX) {
            throw new IllegalArgumentException("Provided min x " + minX + " is greater than max x " + maxX + ".");
        }

        BigDecimal encodingSpace = adjustScaleIfInteger(BigDecimal.valueOf(maxX - minX));
        int encodingSpacePrecision = encodingSpace.scale();
        if (encodingSpacePrecision > argumentPrecision) {
            throw new IllegalArgumentException("Provided argument precision " + argumentPrecision
                    + " is less than actual encoding space precision " + encodingSpacePrecision + "."
                    + " Verify the precision of minX = " + minX + " and maxX = " + maxX + ".");
        }

        BigDecimal encodingsNumberMultiplier = BigDecimal.valueOf(PRECISION_BASE).pow(argumentPrecision);
        BigInteger expectedEncodingsNumber = encodingSpace
                .multiply(encodingsNumberMultiplier)
                .add(BigDecimal.valueOf(1))
                .toBigInteger();
        BigInteger actualEncodingsNumber = BigInteger.valueOf(BINARY_BASE).pow(chromosomeLength);
        if (!expectedEncodingsNumber.equals(actualEncodingsNumber)) {
            throw new IllegalArgumentException("Chromosome with the provided length " + chromosomeLength
                    + " can encode " + actualEncodingsNumber + " arguments with the precision of " + argumentPrecision
                    + " while the required number is " + expectedEncodingsNumber + ".");
        }
    }

    private static BigDecimal adjustScaleIfInteger(BigDecimal value) {
        try {
            return new BigDecimal(value.toBigIntegerExact());
        } catch (ArithmeticException e) {
            return value;
        }
    }
}