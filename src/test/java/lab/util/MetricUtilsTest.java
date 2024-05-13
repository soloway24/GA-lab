package lab.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.Math.*;
import static lab.util.MetricUtils.computeKendallTauB;
import static lab.util.MetricUtils.computePFET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class MetricUtilsTest {


    @Test
    public void whenComputePfetThenSuccess() {
        List<Double> fitnesses = List.of(0., 1., 1., 2., 3., 4., 5., 5., 7., 9.);
        List<Long> offspringCounts = List.of(0L, 0L, 1L, 0L, 2L, 1L, 0L, 2L, 2L, 2L);

        double pfet = computePFET(fitnesses, offspringCounts);
        System.out.println("PFET value: " + pfet);

        assertThat(pfet, equalTo(0.5818566052396751));
    }

    @Test
    public void whenComputePKendallThenSuccess() {
        double[] fitnesses = new double[]{0., 1., 1., 2., 3., 4., 5., 5., 7., 9.};
        double[] offspringCounts = new double[]{0L, 0L, 1L, 0L, 2L, 1L, 0L, 2L, 2L, 2L};

        double ptau = computeKendallTauB(fitnesses, offspringCounts);
        System.out.println("Ptau value: " + ptau);

        assertThat(ptau, equalTo(0.539163866017192));
    }

    @Test
    public void wfesafa() {
        System.out.println(abs(10 * cos(2 * PI * 7) - pow(7, 2)) + 10 * cos(2 * PI * 4.94) - pow(4.94, 2));

        double exponentPower = -2 * log(2) * pow((0.101 - 0.1) / 0.8, 2);
        System.out.println(exp(exponentPower) * pow(sin(5 * PI * 0.101), 6));

        System.out.println(sin(5 * PI * 0.2));
        System.out.println(sin(PI));

        exponentPower = -2 * log(2) * pow((0.08 - 0.08) / 0.854, 2);
        System.out.println(exp(exponentPower) * pow(sin(5 * PI * (pow(0.08, 0.75) - 0.05)), 6));


        exponentPower = -2 * log(2) * pow((0.8 - 0.08) / 0.854, 2);
        System.out.println(exp(exponentPower) * pow(sin(5 * PI * (pow(0.8, 0.75) - 0.05)), 6));
    }
}