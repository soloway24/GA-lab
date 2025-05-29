package lab.util;

import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;
import org.apache.commons.math3.stat.inference.TTest;

import static java.lang.Math.pow;

public class StatisticalAnalysis {

    public static void main(String[] args) {
        TTest tTest = new TTest();

        String toParse1 = "31\t263\t99,15909091\t53,68337111\n";
        String toParse2 = "20\t417\t113,3589744\t79,71577377\n";

        long n1 = 100;
        long n2 = 100;

        StatisticalSummaryValues stats1 = getStats(toParse1, n1);
        StatisticalSummaryValues stats2 = getStats(toParse2, n2);

        double pValue = tTest.tTest(stats1, stats2);
        System.out.println("p-value = " + pValue);

//        System.out.println(stats1.getStandardDeviation());
//        System.out.println(stats2.getStandardDeviation());
    }

    private static StatisticalSummaryValues getStats(String toParse, long n) {
        String[] parts = toParse.split("\t");
        int min = Integer.parseInt(parts[0]);
        int max = Integer.parseInt(parts[1]);
        double mean = Double.parseDouble(parts[2].replace(",", "."));
        double standardDeviation = Double.parseDouble(parts[3].replace("\n", "").replace(",", "."));
        double variance = pow(standardDeviation, 2);
        double sum = mean * n;

        return new StatisticalSummaryValues(mean, variance, n, max, min, sum);
    }
}