package lab;

import org.junit.jupiter.api.Test;

import java.util.List;

import static lab.util.CalculationUtils.getMedian;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class CalculationUtilsTest {

    @Test
    public void whenGetMedianOfEvenSizeListThenSuccess() {
        List<Double> values = List.of(3.0, 2.0, 1.0, 4.0);
        assertThat(getMedian(values), equalTo(2.5));
    }

    @Test
    public void whenGetMedianOfOddSizeListThenSuccess() {
        List<Double> values = List.of(3.0, 2.0, 1.0, 4.0, 5.0);
        assertThat(getMedian(values), equalTo(3.0));
    }
}