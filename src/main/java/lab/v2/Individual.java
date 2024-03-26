package lab.v2;

import lab.parameters.Encoding;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
public class Individual {

    public static final Individual ALL_100_ZEROS_INDIVIDUAL = new Individual("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

    private static final Random RANDOM = new Random();

    private String binaryCode;
    private Encoding encoding;

    public Individual(String binaryCode) {
        this.binaryCode = binaryCode;
    }

    public Individual(String binaryCode, Encoding encoding) {
        this.binaryCode = binaryCode;
        this.encoding = encoding;
    }

    public Individual(Individual individual) {
        this.binaryCode = individual.getBinaryCode();
        this.encoding = individual.getEncoding();
    }

    public static Individual createRandomIndividual(int length, Encoding encoding) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextBoolean() ? "1" : "0");
        }
        return new Individual(sb.toString(), encoding);
    }

    @Override
    public String toString() {
        return binaryCode;
    }
}