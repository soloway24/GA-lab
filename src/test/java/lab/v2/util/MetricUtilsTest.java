package lab.v2.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static lab.v2.util.MetricUtils.computeKendallTauB;
import static lab.v2.util.MetricUtils.computePFET;
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
}