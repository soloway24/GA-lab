package lab.utils;

import lab.encoding.Decoder;
import lab.model.Individual;
import lab.parameters.ContestType;
import lab.parameters.Encoding;
import lab.parameters.FitnessFunction;
import lab.parameters.GeneticOperatorsApplication;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GeneticUtils {

    public static final int ENCODING_LENGTH = 10;
    public static List<Float> SELECTION_PROBABILITY = List.of(0.95f, 0.9f, 0.8f, 0.75f);
    //public static List<Integer> POPULATION_SIZE = List.of(10);
    public static List<Integer> POPULATION_SIZE = List.of(200, 500);
    public static float P_SWAP = 0.9f;

    public static int N = 100;
    public static int N_RUNS = 100;
    public static Encoding ENCODING;
    public static ContestType CONTEST_TYPE;
    public static FitnessFunction FITNESS_FUNCTION;
    public static GeneticOperatorsApplication GENETIC_OPERATORS;

    private static final float SIGMA = 100;
    private static final float C = 0.25f; // 0.25f, 1f, 2f

    public static double FconstALL(Individual individual) {
        return 100;
    }

    public static double FHD(Individual individual) {
        char[] bits = individual.getData().toCharArray();
        int k = 0;

        for (char bit : bits) {
            k += bit == '0' ? 1 : 0;
        }

        return bits.length - k + k * SIGMA;
    }

    public static double F1(Individual individual) {
        return Math.pow(Decoder.decode(individual.getData(), FitnessFunction.F1), 2);
    }


    public static double F2(Individual individual) {
        return 26.2144 - Math.pow(Decoder.decode(individual.getData(), FitnessFunction.F2), 2);
    }

    public static void main(String[] args) {
        GeneticUtils.ENCODING = Encoding.BINARY;
        FitnessFunction func = FitnessFunction.F2;
        Individual best = func.getBest();


        Individual ind = new Individual("0111111110");
        System.out.println(ind);
        System.out.println(Decoder.decode(ind.getData(), func));
        System.out.println(ind.getHealth(func));

        Date now = new Date();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));
        System.err.println(now);
    }
}
